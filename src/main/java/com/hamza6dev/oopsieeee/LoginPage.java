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
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
//                showAlert(Alert.AlertType.ERROR, "Login Error", "Email and Password are required!");
                System.out.println("Login Error: " + "Email and Password are required!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT *, 'patient' AS account_type FROM patients WHERE email = ? AND password = ? " +
                                "UNION " +
                                "SELECT *, 'doctor' AS account_type FROM doctors WHERE email = ? AND password = ?"
                );

                stmt.setString(1, email);
                stmt.setString(2, password);
                stmt.setString(3, email);
                stmt.setString(4, password);

                ResultSet rs = stmt.executeQuery();


                if (rs.next()) {
                    // Login successful, redirect or display success message
//                    showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome back, " + resultSet.getString("name") + "!");
                    System.out.println("Login Success: " + "Welcome back, " + rs.getString("name") + "!");
                    // Example: Redirect to another application page
//                    DashboardPage dashboard = new DashboardPage(); // Hypothetical class for the dashboard
//                    dashboard.start((Stage) ((Node) e.getSource()).getScene().getWindow());
                } else {
                    // Login failed
//                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid email or password.");
                    System.out.println("Login Error: " + "Invalid email or password.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
                System.out.println("Database Error: " + "Failed to connect to the database.");
            }
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
