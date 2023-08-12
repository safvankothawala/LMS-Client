package com.lms.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePage extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;
	static String CUSTOMER_NAME;
	static String CUSTOMER_IDENTITY;

	public WelcomePage(CardLayout cardLayout, JPanel cards) {
		this.cardLayout = cardLayout;
		this.cards = cards;

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
		JLabel footerLabel = new JLabel("Private and Confidential");
		footerLabel.setForeground(Color.GRAY); // Set the text color to gray
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
	}
}
