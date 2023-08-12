package com.lms.app;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.JSONObject;

class RegisterCustomerForm extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;

	public RegisterCustomerForm(CardLayout cardLayout, JPanel cards) {
		this.cardLayout = cardLayout;
		this.cards = cards;

		setLayout(new GridLayout(6, 2, 10, 10));
		Font labelFont = new Font("Arial", Font.BOLD, 30); // Create a new font with size 30
		Font headerlabelFont = new Font("Arial", Font.BOLD, 42); // Create a new font with size 30

		// Create components
		JLabel headerLabel = new JLabel("Register Customer", SwingConstants.LEFT);
		headerLabel.setFont(headerlabelFont); // Set the new font for the header label
		headerLabel.setOpaque(true); // Allow background color to be set
		headerLabel.setForeground(Color.BLACK); // Set text color to white

		JLabel customeNameLabel = new JLabel("Customer Name: ");
		JLabel identityLabel = new JLabel("Identity:");
		JLabel paymentMethodLabel = new JLabel("Payment Method:");
		JLabel licenseKeyLabel = new JLabel("License Key:");

		// Set the new font for all other labels
		customeNameLabel.setFont(labelFont);
		identityLabel.setFont(labelFont);
		paymentMethodLabel.setFont(labelFont);
		licenseKeyLabel.setFont(labelFont);

		JTextField customerNameField = new JTextField(30);
		JTextField identityField = new JTextField(30);
		JTextField paymentMethodField = new JTextField(30);
		JTextField licenseKeyField = new JTextField(30);

		customerNameField.setFont(labelFont);
		identityField.setFont(labelFont);
		paymentMethodField.setFont(labelFont);
		licenseKeyField.setFont(labelFont);

		JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 70, 70));
		JButton submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(100, 30)); // Adjust dimensions as needed
		Font buttonFont = submitButton.getFont();
		submitButton.setFont(new Font(buttonFont.getName(), buttonFont.getStyle(), 32)); // Adjust font size

		submitPanel.add(submitButton);

		// Set layout
		setLayout(new GridLayout(6, 2));

		// Add components to the frame
		add(headerLabel);

		add(new JLabel()); // Empty label for spacing

		add(customeNameLabel);
		add(customerNameField);

		add(identityLabel);
		add(identityField);

		add(paymentMethodLabel);
		add(paymentMethodField);

		add(licenseKeyLabel);
		add(licenseKeyField);

		add(new JLabel()); // Empty label for spacing
		add(submitButton);

		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String identity = identityField.getText();
				String licenseKey = licenseKeyField.getText();
				String paymentMethod = paymentMethodField.getText();
				String customerName = customerNameField.getText();

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("customerIdentity", identity);
				jsonObject.put("paymentMethod", paymentMethod);
				jsonObject.put("customerName", customerName);

				JSONObject jsonObjectForLicense = new JSONObject();
				jsonObjectForLicense.put("licenseKey", licenseKey);

				jsonObject.put("license", jsonObjectForLicense);

				System.out.println("Create Customer JSON: \n" + jsonObject.toString());

				try {
					URL url = new URL("http://localhost:8080/lms/customer/create");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setDoOutput(true);

					try (OutputStream os = connection.getOutputStream()) {
						byte[] input = jsonObject.toString().getBytes("utf-8");
						os.write(input, 0, input.length);
					}

					int htpResponseCode = connection.getResponseCode();

					if (htpResponseCode == HttpURLConnection.HTTP_OK) {

						// Get the response body
						try (InputStream inputStream = connection.getInputStream()) {
							StringBuilder response = new StringBuilder();
							byte[] buffer = new byte[1024];
							int bytesRead;
							while ((bytesRead = inputStream.read(buffer)) != -1) {
								response.append(new String(buffer, 0, bytesRead, "UTF-8"));
							}

							String responseBody = response.toString();

							// Debug: Print the response body
							System.out.println("Response Body: \n" + responseBody);

							JSONObject jsonResponse = new JSONObject(responseBody);
							int responseCode = (int) jsonResponse.get("responseCode");
							if (responseCode >= 0) {

								String filename = "CustomerData.json";
								File file = new File(filename);

								// Create a JSON object with customer identity and name
								jsonResponse.put("customerIdentity", identity);
								jsonResponse.put("customerName", customerName);

								// Write the JSON data to the file
								try (FileWriter fileWriter = new FileWriter(file)) {
									fileWriter.write(jsonResponse.toString(4)); // Use indentation of 4 spaces
									System.out.println("Customer data written to " + filename);
								} catch (IOException ioException) {
									System.out.println("Error writing to file: " + ioException.getMessage());
								}

								WelcomePage.CUSTOMER_NAME = customerName;
								WelcomePage.CUSTOMER_IDENTITY = identity;
								SuccessPage successPage = new SuccessPage("Customer Registration Successful",
										cardLayout, cards);
								successPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								successPage.pack();
								successPage.setVisible(true);
							} else {
								ErrorPage errorPage = new ErrorPage(cardLayout, cards,
										"Error: " + jsonResponse.getString("responseMessage"));
								cards.add(errorPage, "errorPage");
								cardLayout.show(cards, "errorPage");
							}

						} catch (Exception ex) {
							ErrorPage errorPage = new ErrorPage(cardLayout, cards,
									"Exception in Client: " + ex.getMessage());
							cards.add(errorPage, "errorPage");
							cardLayout.show(cards, "errorPage");
						}
					} else {
						ErrorPage errorPage = new ErrorPage(cardLayout, cards, "HTTP Error: " + htpResponseCode);
						cards.add(errorPage, "errorPage");
						cardLayout.show(cards, "errorPage");
					}

					connection.disconnect();
				} catch (Exception ex) {
					ErrorPage errorPage = new ErrorPage(cardLayout, cards, "Exception in Client: " + ex.getMessage());
					cards.add(errorPage, "errorPage");
					cardLayout.show(cards, "errorPage");
				}
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int preferredWidth = screenSize.width / 2;
		int preferredHeight = screenSize.height / 2;
		return new Dimension(preferredWidth, preferredHeight);
	}
}
