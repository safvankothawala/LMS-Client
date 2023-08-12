package com.lms.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.json.JSONObject;

class HomePage extends JFrame {
	private CardLayout cardLayout;
	private JPanel cards;

	public HomePage(String title, CardLayout cardLayout, JPanel cards) {
		super(title);
		this.cardLayout = cardLayout;
		this.cards = cards;

		JLabel headerLabel = new JLabel("Lottery Client App", SwingConstants.CENTER);
		JLabel footerLabel = new JLabel("Private and Confidential", SwingConstants.LEFT);
		JButton registerButton = new JButton("Register Customer");
		registerButton.setPreferredSize(new Dimension(400, 40));

		Font scriptFont = new Font("Script", Font.BOLD, 42);
		headerLabel.setFont(scriptFont);

		Font buttonFont = registerButton.getFont().deriveFont(Font.BOLD, 30);
		registerButton.setFont(buttonFont);

		// Create a panel for centering the button
		JPanel buttonContainer = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonContainer.add(registerButton, gbc);

		JPanel homeCard = new JPanel(new BorderLayout());
		homeCard.add(headerLabel, BorderLayout.NORTH);
		homeCard.add(buttonContainer, BorderLayout.CENTER);
		footerLabel.setOpaque(true);
		footerLabel.setBackground(Color.DARK_GRAY);
		footerLabel.setForeground(Color.WHITE);
		homeCard.add(footerLabel, BorderLayout.SOUTH);

		JPanel registerCard = new RegisterCustomerForm(cardLayout, cards);
		WelcomePage welcomePage = new WelcomePage(cardLayout, cards);

		cards.add(homeCard, "home");
		cards.add(registerCard, "register");
		// cards.add(welcomePage, "welcomePage");

		setLayout(new BorderLayout());
		add(cards, BorderLayout.CENTER);

		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cards, "register");
			}
		});

		boolean isCustomerDataAvailable = false;
		// Check if the CustomerData.json file exists
		JSONObject customerData = new JSONObject();
		try {
			String filename = "CustomerData.json";
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			StringBuilder jsonContent = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				jsonContent.append(line);
			}

			customerData = new JSONObject(jsonContent.toString());
			String customerName = customerData.optString("customerName");
			String customerIdentity = customerData.optString("customerIdentity");
			bufferedReader.close();

			if (customerIdentity != null && customerName != null) {
				isCustomerDataAvailable = true;
			}

			if (isCustomerDataAvailable) {
				WelcomePage.CUSTOMER_NAME = customerName;
				WelcomePage.CUSTOMER_IDENTITY = customerIdentity;
				WelcomePage welcomePage1 = new WelcomePage(cardLayout, cards);
				cards.add(welcomePage1, "welcomePage");
				cardLayout.show(cards, "welcomePage");
			} else {
				cardLayout.show(cards, "home");
			}
		} catch (Exception e) {
			cardLayout.show(cards, "home");
		}
	}

}
