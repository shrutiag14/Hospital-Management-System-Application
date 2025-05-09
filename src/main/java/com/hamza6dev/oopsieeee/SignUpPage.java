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
import User.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class SignUpPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CheapAHH Sign Up");

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

        // Titles
        Label heading = new Label("Create an Account");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.BLACK);

        Label subheading = new Label("Fill in the details below to get started");
        subheading.setFont(Font.font("Arial", 14));
        subheading.setTextFill(Color.GRAY);

        VBox titleBox = new VBox(5, heading, subheading);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Fields
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setPrefHeight(40);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);

        ComboBox<String> accountTypeBox = new ComboBox<>();
        accountTypeBox.getItems().addAll("Patient", "Doctor");
        accountTypeBox.setPromptText("Select account type");
        accountTypeBox.setPrefHeight(40);

// Container for dynamic fields
        VBox dynamicFieldsBox = new VBox(10);
        dynamicFieldsBox.setAlignment(Pos.CENTER_LEFT);

// Listen to selection
        accountTypeBox.setOnAction(e -> {
            dynamicFieldsBox.getChildren().clear();
            if ("Patient".equals(accountTypeBox.getValue())) {
                // Patient-specific fields
                DatePicker dobPicker = new DatePicker();
                dobPicker.setPromptText("Date of Birth");

                // Gender options
                ToggleGroup genderGroup = new ToggleGroup();
                RadioButton maleBtn = new RadioButton("male");
                RadioButton femaleBtn = new RadioButton("female");
                maleBtn.setToggleGroup(genderGroup);
                femaleBtn.setToggleGroup(genderGroup);
                HBox genderBox = new HBox(10, maleBtn, femaleBtn);

                TextField addressField = new TextField();
                addressField.setPromptText("Address");

                TextField phoneField = new TextField();
                phoneField.setPromptText("Phone");

                CheckBox isAdmitBox = new CheckBox("Currently Admitted");

                dynamicFieldsBox.getChildren().addAll(
                        nameField, dobPicker, genderBox, addressField, phoneField, emailField,
                        passwordField, isAdmitBox
                );
            } else if ("Doctor".equals(accountTypeBox.getValue())) {
                // Doctor-specific fields
                DatePicker dobPicker = new DatePicker();
                dobPicker.setPromptText("Date of Birth");

                // Gender
                ToggleGroup genderGroup = new ToggleGroup();
                RadioButton maleBtn = new RadioButton("male");
                RadioButton femaleBtn = new RadioButton("female");
                maleBtn.setToggleGroup(genderGroup);
                femaleBtn.setToggleGroup(genderGroup);
                HBox genderBox = new HBox(10, maleBtn, femaleBtn);

                TextField addressField = new TextField();
                addressField.setPromptText("Address");

                TextField phoneField = new TextField();
                phoneField.setPromptText("Phone");

                TextField pmdcField = new TextField();
                pmdcField.setPromptText("PMDC Number");


                TextField qualificationsField = new TextField();
                qualificationsField.setPromptText("Qualifications");

                TextField specialtyField = new TextField();
                specialtyField.setPromptText("Specialty");

                TextField experienceField = new TextField();
                experienceField.setPromptText("Years of Experience");

                dynamicFieldsBox.getChildren().addAll(
                        nameField, dobPicker, genderBox, addressField, phoneField, emailField,
                        passwordField, pmdcField,
                        qualificationsField, specialtyField, experienceField
                );
            }
        });


        VBox inputFields = new VBox(15, nameField, emailField, passwordField);

        // Sign Up Button
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        signUpBtn.setPrefHeight(45);
        signUpBtn.setMaxWidth(Double.MAX_VALUE);

        signUpBtn.setOnAction(e -> {
            System.out.println("========== Sign-Up Data ==========");
            String userId = User.randomIdGenerator();
            // Storing common fields
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String accountType = accountTypeBox.getValue();
            String selectedGender = null;

            // Initialize placeholders for dynamic fields (common to both account types)
            LocalDate dob = null;
            String address = null;
            String phone = null;

            // Specific placeholders for each account type
            boolean isAdmitted = false; // Patient-specific
            String pmdcNumber = null;  // Doctor-specific
            String qualifications = null;
            String specialty = null;
            int experience = 0;

            // Handle dynamic fields
            for (Node field : dynamicFieldsBox.getChildren()) {
                if (field instanceof TextField) {
                    TextField textField = (TextField) field;

                    switch (textField.getPromptText()) {
                        case "Address" -> address = textField.getText();
                        case "Phone" -> phone = textField.getText();
                        case "PMDC Number" -> pmdcNumber = textField.getText();
                        case "Qualifications" -> qualifications = textField.getText();
                        case "Specialty" -> specialty = textField.getText();
                        case "Years of Experience" -> {
                            if (!textField.getText().isEmpty()) {
                                experience = Integer.parseInt(textField.getText());
                            }
                        }
                    }
                } else if (field instanceof PasswordField) {
                    // Already captured in common fields
                } else if (field instanceof DatePicker) {
                    DatePicker dobPicker = (DatePicker) field;
                    dob = dobPicker.getValue();
                } else if (field instanceof CheckBox) {
                    CheckBox isAdmitBox = (CheckBox) field;
                    isAdmitted = isAdmitBox.isSelected();
                } else if (field instanceof HBox) { // For gender radio buttons
                    for (Node child : ((HBox) field).getChildren()) {
                        if (child instanceof RadioButton && ((RadioButton) child).isSelected()) {
                            selectedGender = ((RadioButton) child).getText();
                        }
                    }
                }
            }

            // Validation before running the SQL queries
            try {
                if (name == null || name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be empty.");
                }
                if (email == null || !User.isValidEmail(email)) {
                    throw new IllegalArgumentException("Invalid email. Please provide a valid email.");
                }
                if (password == null || !User.isValidPassword(password)) {
                    throw new IllegalArgumentException("Invalid password. It must be between 8-20 characters and contain valid characters.");
                }
                if (selectedGender == null) {
                    throw new IllegalArgumentException("Please select a gender.");
                }
                if (dob == null) {
                    throw new IllegalArgumentException("Please select a valid date of birth.");
                }
                if (address == null || address.isEmpty()) {
                    throw new IllegalArgumentException("Address cannot be empty.");
                }
                if (phone == null || phone.isEmpty()) {
                    throw new IllegalArgumentException("Phone cannot be empty.");
                }

                if (accountType.equalsIgnoreCase("Doctor")) {
                    if (pmdcNumber == null || pmdcNumber.isEmpty()) {
                        throw new IllegalArgumentException("PMDC number cannot be empty for a Doctor account.");
                    }
                    if (qualifications == null || qualifications.isEmpty()) {
                        throw new IllegalArgumentException("Qualification cannot be empty for a Doctor account.");
                    }
                    if (specialty == null || specialty.isEmpty()) {
                        throw new IllegalArgumentException("Specialty cannot be empty for a Doctor account.");
                    }
                    if (pmdcNumber == null || pmdcNumber.isEmpty()) {
                        throw new IllegalArgumentException("PMDC Number cannot be empty for doctor accounts.");
                    }
                } else if (accountType.equalsIgnoreCase("Patient")) {

                }

                System.out.println("========== Validated Data ==========");

                // If all validations pass, insert data into the database
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (accountType.equalsIgnoreCase("patient")) {
                        // Query for inserting into User table
                        String userQuery = "INSERT INTO User (user_id, name, date_of_birth, gender, address, phone, email, password, user_type) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'patient')";

                        // Query for inserting into Patient table
                        String patientQuery = "INSERT INTO Patient (patient_id, is_admitted, pending_fee) VALUES (?, ?, 0)";

                        PreparedStatement userStmt = conn.prepareStatement(userQuery);
                        PreparedStatement patientStmt = conn.prepareStatement(patientQuery);

                        // Insert into User table
                        userStmt.setString(1, userId);
                        userStmt.setString(2, name);
                        userStmt.setDate(3, Date.valueOf(dob)); // Assuming date_of_birth is passed as a String in YYYY-MM-DD format
                        userStmt.setString(4, selectedGender);
                        userStmt.setString(5, address);
                        userStmt.setString(6, phone);
                        userStmt.setString(7, email);
                        userStmt.setString(8, password);
                        userStmt.executeUpdate();

                        // Insert into Patient table
                        patientStmt.setString(1, userId); // Use the same user_id as patient_id
                        patientStmt.setBoolean(2, isAdmitted); // True/False or 1/0
                        patientStmt.executeUpdate();

                        System.out.println("New patient entry successfully inserted.");
                    } else if(accountType.equalsIgnoreCase("doctor")) {
                        String userQuery = "INSERT INTO User (user_id, name, date_of_birth, gender, address, phone, email, password, user_type) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'doctor')";

                        // Query to insert into Doctor table
                        String doctorQuery = "INSERT INTO Doctor (doctor_id, PMDC_NO, salary, qualification, speciality, years_experience, shift_start_time, shift_end_time, consultation_fee) " +
                                "VALUES (?, ?, ?, ?, ?, ?, '09:00', '17:00', 200.0)";

                        PreparedStatement userStmt = conn.prepareStatement(userQuery);
                        PreparedStatement doctorStmt = conn.prepareStatement(doctorQuery);

                            // Insert into User table
                            userStmt.setString(1, userId);
                            userStmt.setString(2, name);
                            userStmt.setDate(3, Date.valueOf(dob)); // Assuming date_of_birth is passed in YYYY-MM-DD format
                            userStmt.setString(4, selectedGender);
                            userStmt.setString(5, address);
                            userStmt.setString(6, phone);
                            userStmt.setString(7, email);
                            userStmt.setString(8, password);
                            userStmt.executeUpdate();

                            // Insert into Doctor table
                            doctorStmt.setString(1, userId);  // Use the same user_id as doctor_id
                            doctorStmt.setString(2, pmdcNumber);
                            doctorStmt.setDouble(3, 69000);
                            doctorStmt.setString(4, qualifications);
                            doctorStmt.setString(5, specialty);
                            doctorStmt.setInt(6, experience);
                            doctorStmt.executeUpdate();
                            System.out.println("New doctor entry successfully inserted.");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while inserting the data. Please try again.");
                }

                // redirecting user to dashboard once he/she is signed yp
                try {
                    Dashboard dashboard = new Dashboard(accountType, userId);
                    dashboard.start((Stage) ((Node) e.getSource()).getScene().getWindow());
                } catch (Exception ex) {
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Application Error: ", "An error occurred while redirecting to the dashboard. Please log in again to continue.");
                    HelloApplication application = new HelloApplication();
                    application.start(primaryStage);
                    ex.printStackTrace();
                }

                } catch (IllegalArgumentException ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Invalid Input", ex.getMessage());
            }

            System.out.println("==================================");
        });


        // Back to Login
        Label alreadyHave = new Label("Already have an account?");
        Hyperlink backLink = new Hyperlink("Login");
        backLink.setBorder(Border.EMPTY);
        backLink.setStyle("-fx-text-fill: blue;");
        backLink.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            try {
                loginPage.start((Stage) ((Node) e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox loginBox = new HBox(5, alreadyHave, backLink);

        // Card container
        VBox card = new VBox(20,
                logoBox,
                inputFields,
                accountTypeBox,
                dynamicFieldsBox,
                signUpBtn,
                loginBox
        );

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(400);
        card.setMaxHeight(720* 0.9);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 8);");

        // Root layout
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #f8f8f8;");
        Scene scene = new Scene(root, 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
