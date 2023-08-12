package com.lms.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class SuccessPage extends JFrame {
	private CardLayout cardLayout;
	private JPanel cards;

	public SuccessPage(String title, CardLayout cardLayout, JPanel cards) {
		super(title);
		this.cardLayout = cardLayout;
		this.cards = cards;

		JLabel successLabel = new JLabel(title, SwingConstants.CENTER);
		successLabel.setFont(new Font("Arial", Font.PLAIN, 32));

		JButton okButton = new JButton("OK");
		okButton.setFont(new Font("Arial", Font.BOLD, 32)); // Set font for the button text
		okButton.setPreferredSize(new Dimension(200, 50)); // Adjust dimensions as needed

		setLayout(new BorderLayout());

		add(successLabel, BorderLayout.CENTER);
		add(okButton, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(600, 400)); // Adjust dimensions as needed

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // Close the SuccessPage frame

				WelcomePage welcomePage = new WelcomePage(cardLayout, cards);
				cards.add(welcomePage, "welcomePage");

				cardLayout.show(cards, "welcomePage"); // Switch to the "Welcome Customer" card
			}
		});
	}
}
