package com.hamza6dev.oopsieeee;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import User.*;

public class LoginPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CheapAHH Login");

        // Logo on the left
        HBox logoBox = new HBox(5);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Button logo = new Button("CheapAHH");
//        Label logo = new Label("CheapAHH");
        logo.setStyle("-fx-text-fill: blue; -fx-font-family: Poppins; -fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 24px");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));;

        // adding a set action to return to home page

        logo.setOnAction((e)-> {
            HelloApplication application = new HelloApplication();
            application.start(primaryStage);
        } ) ;

        logoBox.getChildren().addAll(logo);
        logoBox.setAlignment(Pos.CENTER);

        // --- Welcome Text ---
        Label welcomeText = new Label("Welcome back");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label subtitleText = new Label("Please log in to your account");
        subtitleText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleText.setTextFill(Color.GRAY);

        VBox welcomeBox = new VBox(5, welcomeText, subtitleText);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);

        // --- Login Fields ---
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);

        VBox fieldBox = new VBox(15, emailField, passwordField);

        // --- Forgot Password Link ---
        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setBorder(Border.EMPTY);
        forgotLink.setTextFill(Color.BLUE);
        forgotLink.setStyle("-fx-font-size: 12px;");
        forgotLink.setPadding(new Insets(0, 0, 0, 0));

        // --- Login Button ---
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        loginBtn.setPrefHeight(45);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // Inside the LoginPage class
        loginBtn.setOnAction(e-> {
            System.out.println("========== Login Attempt ==========");

            // Fetch user inputs
            String email = emailField.getText();
            String password = passwordField.getText();

            try {
                // Validation checks
                if (email == null || email.isEmpty()) {
                    throw new IllegalArgumentException("Email field cannot be empty.");
                }
                if (!User.isValidEmail(email)) {
                    throw new IllegalArgumentException("Invalid email! Please provide a valid email.");
                }
                if (password == null || password.isEmpty()) {
                    throw new IllegalArgumentException("Password field cannot be empty.");
                }
                if (!User.isValidPassword(password)) {
                    throw new IllegalArgumentException("Invalid password! Password must be between 8-20 characters and contain valid characters.");
                }

                // Check user credentials in the database
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM user WHERE email = ? AND password = ?"; // Replace 'users' with your table name
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, email);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        // Successfully authenticated
                        AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome back, " + rs.getString("name") + "!");
                        String accountType = rs.getString("user_type");
                        String accountID = rs.getString("user_id");
                        try {
                            Dashboard dashboard = new Dashboard(accountType, accountID);
                            dashboard.start((Stage) ((Node) e.getSource()).getScene().getWindow());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // Invalid credentials
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Authentication Failed", "Incorrect email or password. Please try again.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while connecting to the database.");
                }

            } catch (IllegalArgumentException ex) {
                // Display validation error to the user
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Invalid Input", ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An unexpected error occurred. Please try again later.");
            }

            System.out.println("==================================");
        });

        // --- Signup Text ---
        Label noAccount = new Label("Don’t have an account?");
        Hyperlink signUpLink = new Hyperlink("Sign up");
        signUpLink.setBorder(Border.EMPTY);
        signUpLink.setTextFill(Color.BLUE);

        signUpLink.setOnAction(e -> {
            SignUpPage signUp = new SignUpPage();
            try {
                signUp.start((Stage) ((Node) e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox signUpBox = new HBox(5, noAccount, signUpLink);
        signUpBox.setAlignment(Pos.CENTER);

        // --- Container Card ---
        VBox loginCard = new VBox(20, logoBox, welcomeBox, fieldBox, forgotLink, loginBtn, signUpBox);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40));
        loginCard.setMaxWidth(400);
        loginCard.setMaxHeight(720* 0.6);
        loginCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 8);");

        // --- Center the card ---
        StackPane root = new StackPane(loginCard);
        root.setStyle("-fx-background-color: #f8f8f8;");
        Scene scene = new Scene(root, 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
