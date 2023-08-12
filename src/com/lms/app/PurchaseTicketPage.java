package com.lms.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseTicketPage extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;

	public PurchaseTicketPage(CardLayout cardLayout, JPanel cards) {
		this.cardLayout = cardLayout;
		this.cards = cards;

		setLayout(new BorderLayout());

		JLabel headerLabel = new JLabel("Purchase Ticket");
		headerLabel.setFont(new Font("Arial", Font.BOLD, 32));

		JPanel headerPanel = new JPanel();
		headerPanel.add(headerLabel);

		JPanel formPanel = new JPanel();
		formPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 10, 10);

		// Fetch the list of active draws from the API
		List<Draw> activeDraws = fetchActiveDraws();
		JComboBox<Draw> drawDropdown = new JComboBox<>(activeDraws.toArray(new Draw[0]));
		drawDropdown.setPreferredSize(new Dimension(200, 30));
		drawDropdown.setFont(new Font("Arial", Font.PLAIN, 20));

		JLabel selectDrawLabel = new JLabel("Select Draw:");
		selectDrawLabel.setFont(new Font("Arial", Font.BOLD, 24));
		formPanel.add(selectDrawLabel, gbc);

		gbc.gridy++;
		formPanel.add(drawDropdown, gbc);

		JLabel ticketOwnerIdentityLabel = new JLabel("Ticket Owner Identity:");
		ticketOwnerIdentityLabel.setFont(new Font("Arial", Font.BOLD, 24));
		gbc.gridy++;
		formPanel.add(ticketOwnerIdentityLabel, gbc);

		JTextField ticketOwnerIdentityField = new JTextField(20);
		ticketOwnerIdentityField.setFont(new Font("Arial", Font.PLAIN, 24));
		gbc.gridy++;
		formPanel.add(ticketOwnerIdentityField, gbc);

		// Add the submit button
		gbc.gridy++;
		JButton submitButton = new JButton("Submit");
		submitButton.setFont(new Font("Arial", Font.BOLD, 24));
		formPanel.add(submitButton, gbc);

		// Set the action listener for the submit button
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String drawNumber = ((Draw) drawDropdown.getSelectedItem()).drawNumber;
				String ticketOwnerIdentity = ticketOwnerIdentityField.getText();
				callPurchaseTicketAPI(drawNumber, ticketOwnerIdentity);
			}
		});

		// Footer Panel
		JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel footerLabel = new JLabel("Private and Confidential");
		footerLabel.setForeground(Color.GRAY);
		footerPanel.add(footerLabel);

		JPanel footerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footerContainer.add(footerPanel);

		// Add the header, form panel, and footer to this PurchaseTicketPage
		add(headerPanel, BorderLayout.NORTH);
		add(formPanel, BorderLayout.CENTER);
		add(footerContainer, BorderLayout.SOUTH);
	}

	private List<Draw> fetchActiveDraws() {
		List<Draw> activeDraws = new ArrayList<>();

		try {
			URL url = new URL("http://localhost:8080/lms/draw/getActiveDraws");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int httpResponseCode = connection.getResponseCode();

			if (httpResponseCode == HttpURLConnection.HTTP_OK) {
				try (InputStream inputStream = connection.getInputStream()) {
					String responseBody = readResponse(inputStream);
					JSONObject jsonResponse = new JSONObject(responseBody);

					int responseCodeValue = jsonResponse.getInt("responseCode");
					if (responseCodeValue == 0) {
						JSONArray drawsArray = jsonResponse.getJSONArray("draws");
						for (int i = 0; i < drawsArray.length(); i++) {
							JSONObject drawObject = drawsArray.getJSONObject(i);
							int drawID = drawObject.getInt("drawID");
							String drawNumber = drawObject.getString("drawNumber");
							// Parse other properties and create Draw object
							Draw draw = new Draw(drawID, drawNumber);
							activeDraws.add(draw);
						}
					} else {
						showTxErroPage("Error in getting Draws. HTTP Response Code: " + httpResponseCode);
					}
				}
			}

			connection.disconnect();
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			showTxErroPage("Error in getting Active Draws: " + e.getMessage());
		}

		return activeDraws;
	}

	private void showTransactionSuccessPage(int ticketAssociationID, String ticketNumber) {
		String successMessage = "Ticket purchased successfully. Ticket Number: " + ticketNumber;
		TransactionSuccessPage successPage = new TransactionSuccessPage(cardLayout, cards, successMessage);
		cards.add(successPage, "transactionSuccessPage");

		cardLayout.show(cards, "transactionSuccessPage");

	}

	private void showTxErroPage(String message) {

		TransactionErrorPage errorPage = new TransactionErrorPage(cardLayout, cards, message);
		cards.add(errorPage, "transactionErrorPage");
		cardLayout.show(cards, "transactionErrorPage");

	}

	private String readResponse(InputStream inputStream) throws IOException {
		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		}
		return response.toString();
	}

	private void callPurchaseTicketAPI(String drawNumber, String ticketOwnerIdentity) {
		try {
			URL url = new URL("http://localhost:8080/lms/purchaseticket");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			String requestBody = String.format("{\"drawNumber\":\"%s\",\"ticketOwnerIdentity\":\"%s\"}", drawNumber,
					ticketOwnerIdentity);

			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int httpResponseCode = connection.getResponseCode();

			if (httpResponseCode == HttpURLConnection.HTTP_OK) {
				// Handle success response
				try (InputStream inputStream = connection.getInputStream()) {
					String responseBody = readResponse(inputStream);
					JSONObject jsonResponse = new JSONObject(responseBody);

					int purchaseResponseCodeValue = jsonResponse.getInt("responseCode");
					if (purchaseResponseCodeValue == 0) {
						int ticketAssociationID = jsonResponse.getInt("ticketAssociationID");
						String ticketNumber = jsonResponse.getString("ticketNumber");
						showTransactionSuccessPage(ticketAssociationID, ticketNumber);
					} else {
						// Handle error response
						showTxErroPage(jsonResponse.getString("responseMessage"));
					}
				}

			} else {
				showTxErroPage("Error in purchasing Ticket. HTTP Response Code: " + httpResponseCode);
			}

			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			showTxErroPage("Error in purchasing Ticket: " + e.getMessage());
		}
	}

	// Define a class to represent a Draw object
	private class Draw {
		private int drawID;
		private String drawNumber;
		// Other properties of Draw

		public Draw(int drawID, String drawNumber) {
			this.drawID = drawID;
			this.drawNumber = drawNumber;
			// Initialize other properties
		}

		@Override
		public String toString() {
			return drawNumber; // Display the draw number in the dropdown
		}
	}
}
