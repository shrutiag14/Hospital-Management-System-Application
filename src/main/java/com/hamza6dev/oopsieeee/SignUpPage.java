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
import java.sql.PreparedStatement;

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

            // Storing common fields
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String accountType = accountTypeBox.getValue();
            String selectedGender = null;

            // Initialize placeholders for dynamic fields (common to both account types)
            String dob = null;
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
                        case "Years of Experience" -> experience = Integer.parseInt(textField.getText());
                    }
                } else if (field instanceof PasswordField) {
                    // Already captured in common fields
                } else if (field instanceof DatePicker) {
                    DatePicker dobPicker = (DatePicker) field;
                    dob = (dobPicker.getValue() != null) ? dobPicker.getValue().toString() : "Not selected";
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

            try (Connection conn = DatabaseConnection.getConnection()) {
                if(accountType.equals("Patient")) {
                    String sql = "INSERT INTO patients (patient_id, name, email, password, gender, address, phone, date_of_birth, is_admit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, User.randomIdGenerator());
                    stmt.setString(2, name);
                    stmt.setString(3, email);
                    stmt.setString(4, password);
                    stmt.setString(5, selectedGender); // whatever you selected
                    stmt.setString(6,  address);
                    stmt.setString(7, phone);
                    stmt.setDate(8, dob != null ? java.sql.Date.valueOf(dob) : null);
                    stmt.setBoolean(9,  isAdmitted);

                    stmt.executeUpdate();
                    System.out.println("Patient successfully inserted!");
                } else if(accountType.equals("Doctor")) {
                    String sql = "INSERT INTO doctors (doctor_id, name, email, password, gender, address, phone, date_of_birth, pmdc_no, availability_hours, salary, qualifications, speciality, experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?,?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, User.randomIdGenerator());
                    stmt.setString(2, name);
                    stmt.setString(3, email);
                    stmt.setString(4, password);
                    stmt.setString(5, selectedGender); // whatever you selected
                    stmt.setString(6,  address);
                    stmt.setString(7, phone);
                    stmt.setDate(8, dob != null ? java.sql.Date.valueOf(dob) : null);
                    stmt.setString(9,  pmdcNumber);
                    stmt.setString(10, "9AM-5PM");
                    stmt.setDouble(11, 10000);
                    stmt.setString(12, qualifications);
                    stmt.setString(13, specialty);
                    stmt.setInt(14, experience);


                    stmt.executeUpdate();
                    System.out.println("Patient successfully inserted!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
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
