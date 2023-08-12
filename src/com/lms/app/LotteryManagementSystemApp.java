package com.lms.app;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LotteryManagementSystemApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			CardLayout cardLayout = new CardLayout();
			JPanel cards = new JPanel(cardLayout);

			String version = readVersionFromFile();
			System.out.println("App-Version: " + version);
			HomePage.appVersion = version;
			WelcomePage.appVersion = version;

			HomePage homePage = new HomePage("Lottery Management System", cardLayout, cards);
			homePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			homePage.pack();
			homePage.setVisible(true);

			// Create a Timer to perform tasks every minute
			Timer minuteTimer = new Timer(10000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					checkForVersionUpgrade();
				}

				private void checkForVersionUpgrade() {
					try {
						URL url = new URL("http://localhost:8080/lms/checkversion/" + version);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");

						int responseCode = connection.getResponseCode();

						if (responseCode == HttpURLConnection.HTTP_OK) {
							try (InputStream inputStream = connection.getInputStream()) {
								String responseBody = readResponse(inputStream);
								JSONObject jsonResponse = new JSONObject(responseBody);

								int responseCodeValue = jsonResponse.getInt("responseCode");
								if (responseCodeValue == 0) {
									System.out.println("Version Upgrade Check Successful.");
								} else {
									String errorMessage = jsonResponse.getString("responseMessage");
									System.out.println(errorMessage);
									showErrorMessage(errorMessage);
									return; // Stop further execution
								}
							}
						} else {
							System.out.println("HTTP Response Code: " + responseCode);
						}

						connection.disconnect();
					} catch (IOException | JSONException e) {
						e.printStackTrace();
						System.out.println("Error in checking version upgrade: " + e.getMessage());
					}
				}

				private String readResponse(InputStream inputStream) throws IOException {
					StringBuilder response = new StringBuilder();
					try (BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
					}
					return response.toString();
				}

			});
			minuteTimer.setInitialDelay(0); // Start immediately
			minuteTimer.start();
		});
	}

	private static String readVersionFromFile() {
		String version = null;
		Scanner scanner = null;
		// Read the app-version.json file
		try (InputStream inputStream = new FileInputStream("app-version.json")) {
			scanner = new Scanner(inputStream);
			StringBuilder jsonStringBuilder = new StringBuilder();
			while (scanner.hasNextLine()) {
				jsonStringBuilder.append(scanner.nextLine());
			}
			JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());
			version = jsonObject.getString("version");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}

		return version;
	}

	private static void showErrorMessage(String errorMessage) {
		UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 24)); // Set font size
		UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 24));

		JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

}