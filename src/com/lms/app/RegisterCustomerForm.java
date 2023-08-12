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

import org.json.JSONObject;

class RegisterCustomerForm extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;
	public static String appVersion;


	public RegisterCustomerForm(CardLayout cardLayout, JPanel cards) {
		this.cardLayout = cardLayout;
		this.cards = cards;

		setLayout(new BorderLayout());
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel headerLabel = new JLabel("Register Customer");
		headerLabel.setFont(new Font("Arial", Font.BOLD, 42));
		headerPanel.add(headerLabel);
		Font txtFieldLabelFont = new Font("Arial", Font.PLAIN, 24);

		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(10, 10, 10, 10);

		Font labelFont = new Font("Arial", Font.BOLD, 24);

		JLabel nameLabel = new JLabel("Customer Name:");
		nameLabel.setFont(labelFont);
		JTextField nameField = new JTextField(20);

		JLabel identityLabel = new JLabel("Identity:");
		identityLabel.setFont(labelFont);
		JTextField identityField = new JTextField(20);

		JLabel paymentMethodLabel = new JLabel("Payment Method:");
		paymentMethodLabel.setFont(labelFont);
		JTextField paymentMethodField = new JTextField(20);

		JLabel licenseKeyLabel = new JLabel("License Key:");
		licenseKeyLabel.setFont(labelFont);
		JTextField licenseKeyField = new JTextField(20);

		nameField.setFont(txtFieldLabelFont);
		licenseKeyField.setFont(txtFieldLabelFont);
		identityField.setFont(txtFieldLabelFont);
		paymentMethodField.setFont(txtFieldLabelFont);

		formPanel.add(nameLabel, gbc);
		gbc.gridx++;
		formPanel.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		formPanel.add(identityLabel, gbc);
		gbc.gridx++;
		formPanel.add(identityField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		formPanel.add(paymentMethodLabel, gbc);
		gbc.gridx++;
		formPanel.add(paymentMethodField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		formPanel.add(licenseKeyLabel, gbc);
		gbc.gridx++;
		formPanel.add(licenseKeyField, gbc);

		/*
		 * JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); JButton
		 * submitButton = new JButton("Submit"); submitButton.setFont(new Font("Arial",
		 * Font.BOLD, 24));
		 * 
		 * buttonPanel.add(submitButton);
		 */

		//
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
		//

		// Create a footer panel
		JPanel footerPanel = new JPanel(new BorderLayout());
		JLabel footerLabel = new JLabel("App Version: " + appVersion);
		footerLabel.setForeground(Color.WHITE); 
		footerPanel.add(footerLabel, BorderLayout.WEST);
		footerPanel.setBackground(Color.BLACK);

		// Add the footer panel to the bottom of the form
		add(footerPanel, BorderLayout.SOUTH);
		add(headerPanel, BorderLayout.NORTH);
		add(formPanel, BorderLayout.CENTER);
		// add(buttonPanel, BorderLayout.SOUTH);

		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String identity = identityField.getText();
				String licenseKey = licenseKeyField.getText();
				String paymentMethod = paymentMethodField.getText();
				String customerName = nameField.getText();

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
