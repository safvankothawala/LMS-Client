package com.lms.app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LotteryManagementSystemApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			CardLayout cardLayout = new CardLayout();
			JPanel cards = new JPanel(cardLayout);

			HomePage homePage = new HomePage("Lottery Management System", cardLayout, cards);
			homePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			homePage.pack();
			homePage.setVisible(true);
		});
	}

}