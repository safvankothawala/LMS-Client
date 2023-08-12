package com.lms.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.json.JSONObject;

public class WelcomePage extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;
	static String CUSTOMER_NAME;
	static String CUSTOMER_IDENTITY;
	private String winnerTicketNumber;
	private String winnerTicketOwnerIdentity;
	private String winnerDrawNumber;
	public static String appVersion;

	public WelcomePage(CardLayout cardLayout, JPanel cards) {
		this.cardLayout = cardLayout;
		this.cards = cards;

		checkForWinningTicket();

		setLayout(new BorderLayout());

		JPanel welcomePanel = new JPanel(new BorderLayout());
		CreateTicketOwnerPage createTicketOwnerPage = new CreateTicketOwnerPage(cardLayout, cards);

		// Add the cards to the panel
		cards.add(createTicketOwnerPage, "createTicketOwner");

		JLabel welcomeLabel = new JLabel("Lottery Customer Application", SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Arial", Font.BOLD, 42)); // Increase font size
		welcomeLabel.setForeground(Color.BLACK); // Set the text color to black
		welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

		JLabel customerNameLabel = new JLabel("<html><font color='black'>Customer:</font> " + CUSTOMER_NAME + "</html>",
				SwingConstants.CENTER);
		customerNameLabel.setFont(new Font("Arial", Font.BOLD, 32)); // Set font size
		customerNameLabel.setForeground(Color.BLUE); // Set the customer name text color to blue
		welcomePanel.add(customerNameLabel, BorderLayout.CENTER);

		JPanel footerPanel = new JPanel(new BorderLayout());
		JLabel footerLabel = new JLabel("App Version: " + appVersion);
		footerLabel.setForeground(Color.BLACK); 
		footerLabel.setBackground(Color.DARK_GRAY);
		footerPanel.add(footerLabel, BorderLayout.WEST);

		JPanel footerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footerContainer.add(footerPanel);

		add(welcomePanel, BorderLayout.NORTH); // Welcome text above the buttons
		add(footerContainer, BorderLayout.SOUTH); // Footer at the bottom

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton purchaseButton = new JButton("Purchase Ticket");
		JButton createButton = new JButton("Create Ticket Owner");

		// Increase font size of buttons
		Font buttonFont = new Font("Arial", Font.BOLD, 22);
		purchaseButton.setFont(buttonFont);
		createButton.setFont(buttonFont);

		purchaseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PurchaseTicketPage purchaseTicketPage = new PurchaseTicketPage(cardLayout, cards);
				cards.add(purchaseTicketPage, "purchaseTicket"); // Add the purchaseTicketPage to the cards panel
				cardLayout.show(cards, "purchaseTicket"); // Switch to the purchaseTicketPage
			}
		});

		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cards, "createTicketOwner"); // Switch to the "createTicketOwner" card
			}
		});

		buttonPanel.add(purchaseButton);
		buttonPanel.add(createButton);

		add(buttonPanel, BorderLayout.CENTER); // Buttons in the center

		addPanelForWinnerTicket(welcomePanel);

	}

	private void addPanelForWinnerTicket(JPanel welcomePanel) {
		if (winnerTicketNumber != null) {
			JPanel winnerPanel = new JPanel(new BorderLayout());
			JLabel winnerTicketLabel = new JLabel(
					"Winner Ticket Number - " + winnerTicketNumber + " , Draw Number - " + winnerDrawNumber,
					SwingConstants.CENTER);
			winnerTicketLabel.setFont(new Font("Arial", Font.BOLD, 24));
			winnerTicketLabel.setForeground(Color.ORANGE);
			welcomePanel.add(winnerTicketLabel, BorderLayout.CENTER);
			JLabel winnerTicketMessage = new JLabel(
					"Lottery prize will be transfered to Owner - " + winnerTicketOwnerIdentity, SwingConstants.CENTER);
			winnerTicketMessage.setFont(new Font("Arial", Font.BOLD, 24));
			winnerTicketMessage.setForeground(Color.BLUE);
			welcomePanel.add(winnerTicketMessage, BorderLayout.SOUTH);
			add(winnerPanel, BorderLayout.SOUTH);
		}
	}

	private void checkForWinningTicket() {
		try {
			if (CUSTOMER_IDENTITY != null) {
				URL url = new URL("http://localhost:8080/lms/checkwinner/" + CUSTOMER_IDENTITY);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "text/event-stream");
				int attempt = 0;
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					try (Scanner scanner = new Scanner(connection.getInputStream())) {
						while (scanner.hasNextLine()) {
							if (attempt > 1) {
								break;
							}
							String line = scanner.nextLine();
							System.out.println(line);
							if (line.startsWith("data:") && !line.equals("data:")) {
								// Process the SSE event data
								String eventData = line.substring(5).trim();
								JSONObject eventDataJson = new JSONObject(eventData);

								System.out.println("Received SSE Event: " + eventData);

								if (eventDataJson.getInt("responseCode") == 0) {
									winnerTicketNumber = eventDataJson.getString("ticketNumber");
									winnerTicketOwnerIdentity = eventDataJson.getString("ticketOwnerIdentity");
									winnerDrawNumber = eventDataJson.getString("drawNumber");
									// Display the ticket number and ticket owner identity
									System.out.println("Winning Ticket Number: " + winnerTicketNumber);
									System.out.println("Ticket Owner Identity: " + winnerTicketOwnerIdentity);
								} else {
									System.out.println("No winning ticket found");
									attempt++;
								}

								break;

							} else {
								System.out.println("No winning ticket found");
								attempt++;
							}
						}
					}
				} else {
					System.out.println("HTTP Response Code: " + responseCode);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Call the method to check for winning ticket
		checkForWinningTicket();
	}

}
