package edu.advising.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TopBarController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label badgeLabel;

    @FXML
    public void initialize() {

        welcomeLabel.setText("Welcome, Admin");

        int unread = 3;

        badgeLabel.setVisible(unread > 0);
    }

    @FXML
    private void handleNotifications() {
        System.out.println("Notifications clicked");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
    }
}