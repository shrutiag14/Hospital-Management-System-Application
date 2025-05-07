package com.hamza6dev.oopsieeee;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ----- Navigation Bar -----
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: white;");
        navBar.setAlignment(Pos.CENTER);
        navBar.setSpacing(30);

        // Logo on the left
        HBox logoBox = new HBox(5);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logo = new Label("CheapAHH");
        logo.setStyle("-fx-text-fill: blue; -fx-font-family: Poppins");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));;
        logoBox.getChildren().add(logo);

        // Center navigation links
        HBox centerLinks = new HBox(20);
        centerLinks.setAlignment(Pos.CENTER);

        Hyperlink homeLink = new Hyperlink("Home");
        homeLink.setBorder(Border.EMPTY);

        MenuButton servicesMenu = new MenuButton("Services");
        servicesMenu.getItems().addAll(
                new MenuItem("Cardiology"),
                new MenuItem("Neurology"),
                new MenuItem("Pediatrics")
        );
        servicesMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 14px;");

        Hyperlink doctorsBtn = new Hyperlink("Doctors");
        Hyperlink aboutBtn = new Hyperlink("About us");
        Hyperlink contactBtn = new Hyperlink("Contact us");

        for (Hyperlink b : new Hyperlink[]{homeLink, doctorsBtn, aboutBtn, contactBtn}) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: 600; -fx-text-decoration: none;");
        }

        centerLinks.getChildren().addAll(homeLink, servicesMenu, doctorsBtn, aboutBtn, contactBtn);

        // Auth buttons on the right
        HBox authBox = new HBox(10);
        authBox.setAlignment(Pos.CENTER_RIGHT);
        Button signIn = new Button("Sign in");
        signIn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 5px 20px");
        Button signUp = new Button("Sign up");
        signUp.setStyle("-fx-border-color: blue; -fx-text-fill: blue; -fx-background-color: white; -fx-border-radius: 5px; -fx-padding: 5px 20px");
        authBox.getChildren().addAll(signIn, signUp);

        signIn.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            try {
                loginPage.start((Stage) ((Node) e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        signUp.setOnAction(e -> {
            SignUpPage loginPage = new SignUpPage();
            try {
                loginPage.start((Stage) ((Node) e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Spacer logic
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Final navbar layout: logo | spacer | nav links | spacer | auth
        navBar.getChildren().addAll(logoBox, leftSpacer, centerLinks, rightSpacer, authBox);

        // ----- Left Text Section -----
        Text heading1 = new Text("We care");
        heading1.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        heading1.setFill(Color.BLUE);
        Text heading2 = new Text("about your health");
        heading2.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        Text paragraph = new Text("Good health is the state of mental, physical and social well being\nand it does not just mean absence of diseases.");
        paragraph.setFont(Font.font("Arial", 14));
        paragraph.setFill(Color.GRAY);

        Button bookBtn = new Button("Book an appointment →");
        bookBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 5px 8px");
        Button watchBtn = new Button("▶ Watch videos");
        watchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue; -fx-font-size: 13px; -fx-padding: 5px 8px");

        HBox actionBtns = new HBox(10, bookBtn, watchBtn);
        actionBtns.setPadding(new Insets(10, 0, 10, 0));

        Text joinText = new Text("Become member of our hospital community? ");
        Hyperlink joinLink = new Hyperlink("Sign up");
        HBox joinBox = new HBox(joinText, joinLink);
        joinBox.setStyle("-fx-display: flex; -fx-align-items: center;");

        VBox leftText = new VBox(10, heading1, heading2, paragraph, actionBtns, joinBox);

        // ----- Search Section -----
        TextField nameField = new TextField();
        nameField.setPromptText("Name of Doctor");
        TextField specialityField = new TextField();
        specialityField.setPromptText("Speciality");
        CheckBox availabilityBox = new CheckBox("Availability");
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-padding: 5px 8px");

        HBox searchBox = new HBox(10, nameField, specialityField, availabilityBox, searchBtn);
        searchBox.setPadding(new Insets(20));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        VBox leftSection = new VBox(leftText, searchBox);
        leftSection.setPadding(new Insets(40));
        leftSection.setSpacing(20);

        // ----- Main Split Layout -----
        HBox mainContent = new HBox(50, leftSection);
        mainContent.setPadding(new Insets(100));
        mainContent.setAlignment(Pos.CENTER_LEFT);



//        Button patientBtn = new Button("Doctor");
//        patientBtn.setOnAction(e -> {
//            PatientDashboard dashboard = new PatientDashboard();
//            try {
//                dashboard.start((Stage) ((Node) e.getSource()).getScene().getWindow());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });

        VBox root = new VBox(navBar, mainContent);
        root.setStyle("-fx-background-color: #f8f8f8;");



        Scene scene = new Scene(root, 1080, 720);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("CheapAH");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
