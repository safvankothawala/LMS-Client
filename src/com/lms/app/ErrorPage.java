package com.lms.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorPage extends JPanel {
	private CardLayout cardLayout;
	private JPanel cards;

	public ErrorPage(CardLayout cardLayout, JPanel cards, String errorMessage) {
		this.cardLayout = cardLayout;
		this.cards = cards;

		setLayout(new BorderLayout());

		JLabel errorLabel = new JLabel(errorMessage, SwingConstants.CENTER);
		errorLabel.setFont(new Font("Arial", Font.BOLD, 32));

		JButton okButton = new JButton("OK");
		okButton.setFont(new Font("Arial", Font.BOLD, 24));
		okButton.setPreferredSize(new Dimension(170, 40));

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cards, "register");
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);

		add(errorLabel, BorderLayout.CENTER);

		add(buttonPanel, BorderLayout.SOUTH);
	}

}
