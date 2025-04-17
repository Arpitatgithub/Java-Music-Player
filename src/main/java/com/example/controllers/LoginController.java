package com.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if ("user".equals(username) && "pass".equals(password)) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/views/player.fxml"));
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                errorLabel.setText("Failed to load player.");
            }
        } else {
            errorLabel.setText("Invalid credentials!");
        }
    }
}