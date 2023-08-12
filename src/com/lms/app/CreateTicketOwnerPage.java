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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.JSONObject;

public class CreateTicketOwnerPage extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;

	public CreateTicketOwnerPage(CardLayout cardLayout, JPanel cards) {
		this.cardLayout = cardLayout;
		this.cards = cards;

		setLayout(new BorderLayout());

		// Create Ticket Owner Label
		JLabel createTicketOwnerLabel = new JLabel("Create Ticket Owner", SwingConstants.CENTER);
		Font labelFont = new Font("Arial", Font.BOLD, 42);
		Font txtFieldLabelFont = new Font("Arial", Font.PLAIN, 24);
		Font fieldLabelFont = new Font("Arial", Font.BOLD, 24);
		createTicketOwnerLabel.setFont(labelFont);
		createTicketOwnerLabel.setOpaque(true);
		createTicketOwnerLabel.setForeground(Color.BLACK);

		// Customer Name Label
		JLabel customerNameLabel = new JLabel("Customer: " + WelcomePage.CUSTOMER_NAME);
		customerNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
		customerNameLabel.setForeground(Color.BLUE);

		// Form
		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);

		JLabel nameLabel = new JLabel("Name:");
		JLabel mobileNumberLabel = new JLabel("Mobile Number:");
		JLabel identityLabel = new JLabel("Identity:");
		JLabel paymentMethodLabel = new JLabel("Payment Method:");

		nameLabel.setFont(fieldLabelFont);
		mobileNumberLabel.setFont(fieldLabelFont);
		identityLabel.setFont(fieldLabelFont);
		paymentMethodLabel.setFont(fieldLabelFont);

		JTextField nameField = new JTextField(15);
		JTextField mobileNumberField = new JTextField(15);
		JTextField identityField = new JTextField(15);
		JTextField paymentMethodField = new JTextField(15);

		nameField.setFont(txtFieldLabelFont);
		mobileNumberField.setFont(txtFieldLabelFont);
		identityField.setFont(txtFieldLabelFont);
		paymentMethodField.setFont(txtFieldLabelFont);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		formPanel.add(nameLabel, gbc);
		gbc.gridx = 1;
		formPanel.add(nameField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(mobileNumberLabel, gbc);
		gbc.gridx = 1;
		formPanel.add(mobileNumberField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(identityLabel, gbc);
		gbc.gridx = 1;
		formPanel.add(identityField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		formPanel.add(paymentMethodLabel, gbc);
		gbc.gridx = 1;
		formPanel.add(paymentMethodField, gbc);

		// Submit Button
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 70, 70));
		JButton submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(200, 40));
		submitButton.setFont(new Font(submitButton.getFont().getName(), Font.BOLD, 32));
		submitPanel.add(submitButton);
		formPanel.add(submitPanel, gbc);

		// Assemble the panel
		add(createTicketOwnerLabel, BorderLayout.NORTH);
		add(formPanel, BorderLayout.CENTER);
		add(customerNameLabel, BorderLayout.SOUTH);

		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String mobileNumber = mobileNumberField.getText();
				String identity = identityField.getText();
				String paymentMethod = paymentMethodField.getText();

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("mobileNumber", mobileNumber);
				jsonObject.put("name", name);
				jsonObject.put("paymentMethod", paymentMethod);
				jsonObject.put("ticketOwnerIdentity", identity);

				JSONObject customerObject = new JSONObject();
				customerObject.put("customerIdentity", WelcomePage.CUSTOMER_IDENTITY);
				jsonObject.put("customer", customerObject);

				try {
					URL url = new URL("http://localhost:8080/lms/ticketowner/create");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setDoOutput(true);

					try (OutputStream os = connection.getOutputStream()) {
						byte[] input = jsonObject.toString().getBytes("utf-8");
						os.write(input, 0, input.length);
					}

					int httpResponseCode = connection.getResponseCode();

					if (httpResponseCode == HttpURLConnection.HTTP_OK) {

						InputStream responseStream = connection.getInputStream();
						String response = convertInputStreamToString(responseStream);
						JSONObject jsonResponse = new JSONObject(response);
						int responseCode = (int) jsonResponse.get("responseCode");
						if (responseCode >= 0) {
							// successful ticket owner creation
							String successMessage = "Ticket Owner with Identity " + identity + " created successfully";
							TransactionSuccessPage successPage = new TransactionSuccessPage(cardLayout, cards,
									successMessage);
							cards.add(successPage, "transactionSuccessPage");
							cardLayout.show(cards, "transactionSuccessPage");

						} else {
							String errorMessage = jsonResponse.getString("responseMessage");
							TransactionErrorPage errorPage = new TransactionErrorPage(cardLayout, cards, errorMessage);
							cards.add(errorPage, "transactionErrorPage");
							cardLayout.show(cards, "transactionErrorPage");
						}

					} else {
						// Handle other response codes or errors
						InputStream errorStream = connection.getErrorStream();
						String errorResponse = convertInputStreamToString(errorStream);
						JSONObject jsonResponse = new JSONObject(errorResponse);
						String errorMessage = jsonResponse.getString("responseMessage");
						TransactionErrorPage errorPage = new TransactionErrorPage(cardLayout, cards, errorMessage);
						cards.add(errorPage, "transactionErrorPage");
						cardLayout.show(cards, "transactionErrorPage");

					}

					connection.disconnect();
				} catch (Exception ex) {
					ex.printStackTrace();
					// Handle exception
					String errorMessage = "Exception in creating Ticket Owner: " + ex.getMessage();
					TransactionErrorPage errorPage = new TransactionErrorPage(cardLayout, cards, errorMessage);
					cards.add(errorPage, "transactionErrorPage");
					cardLayout.show(cards, "transactionErrorPage");
				}
			}
		});
	}

	private String convertInputStreamToString(InputStream inputStream) throws IOException {
		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		}
		return response.toString();
	}
}
