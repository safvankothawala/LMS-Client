package com.lms.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionErrorPage extends JPanel {
    private CardLayout cardLayout;
    private JPanel cards;

    public TransactionErrorPage(CardLayout cardLayout, JPanel cards, String errorMessage) {
        this.cardLayout = cardLayout;
        this.cards = cards;

        setLayout(new BorderLayout());

        JLabel errorLabel = new JLabel("Error", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 32));
        errorLabel.setForeground(Color.RED); 

        JLabel messageLabel = new JLabel(errorMessage, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 32));

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(170, 40));
        okButton.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(errorLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "welcomePage");
            }
        });
    }
}
