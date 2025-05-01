package com.hamza6dev.oopsieeee;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class DoctorDashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // === Sidebar ===
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: #2D3E50;");
        sidebar.setPrefWidth(200);

        Label title = new Label("Doctor Panel");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Button dashboardBtn = createSidebarButton("Dashboard");
        Button patientsBtn = createSidebarButton("Patients");
        Button appointmentsBtn = createSidebarButton("Appointments");
        Button messagesBtn = createSidebarButton("Messages");
        Button logoutBtn = createSidebarButton("Logout");

        sidebar.getChildren().addAll(title, dashboardBtn, patientsBtn, appointmentsBtn, messagesBtn, logoutBtn);

        // === Top bar ===
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(20));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: #F4F6F8;");

        Label doctorName = new Label("Dr. John Smith");
        doctorName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        topBar.getChildren().add(doctorName);

        // === Dashboard Content ===
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Welcome Message
        Label welcomeLabel = new Label("Welcome back, Dr. John!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Analytics Cards
        HBox cards = new HBox(20);
        cards.getChildren().addAll(
                createCard("Patients Today", "23", "#E57373"),
                createCard("Appointments", "15", "#64B5F6"),
                createCard("Pending Reviews", "4", "#FFD54F")
        );

        // Appointments Table
        TableView<String> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<String, String> nameCol = new TableColumn<>("Patient Name");
        TableColumn<String, String> timeCol = new TableColumn<>("Time");
        TableColumn<String, String> reasonCol = new TableColumn<>("Reason");

        nameCol.setMinWidth(200);
        timeCol.setMinWidth(150);
        reasonCol.setMinWidth(300);

        table.getColumns().addAll(nameCol, timeCol, reasonCol);

        content.getChildren().addAll(welcomeLabel, cards, new Label("Today's Appointments:"), table);

        // === Final Layout ===
        root.setLeft(sidebar);
        root.setTop(topBar);
        root.setCenter(content);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Doctor Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    private VBox createCard(String title, String value, String colorHex) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefSize(180, 100);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 10px;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.WHITE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
