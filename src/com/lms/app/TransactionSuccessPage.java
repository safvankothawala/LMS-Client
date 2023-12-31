package com.lms.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionSuccessPage extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;
	private String txsuccessMessage;

	public TransactionSuccessPage(CardLayout cardLayout, JPanel cards, String successMessage) {
		this.cardLayout = cardLayout;
		this.cards = cards;
		this.txsuccessMessage = successMessage;

		setLayout(new BorderLayout());

		JLabel successLabel = new JLabel("Success", SwingConstants.CENTER);
		successLabel.setFont(new Font("Arial", Font.BOLD, 32));
		successLabel.setForeground(Color.GREEN);

		JLabel messageLabel = new JLabel(txsuccessMessage, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Arial", Font.PLAIN, 32));

		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(170, 40));
		okButton.setFont(new Font("Arial", Font.BOLD, 24));

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(messageLabel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(okButton);

		centerPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(successLabel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WelcomePage welcomePage = new WelcomePage(cardLayout, cards);
				cards.add(welcomePage, "welcomePage");
				cardLayout.show(cards, "welcomePage");
			}
		});
	}

	public String getTxsuccessMessage() {
		return txsuccessMessage;
	}

	public void setTxsuccessMessage(String txsuccessMessage) {
		this.txsuccessMessage = txsuccessMessage;
	}

	

}
