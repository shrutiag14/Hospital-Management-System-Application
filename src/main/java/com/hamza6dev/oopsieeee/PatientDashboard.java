package com.hamza6dev.oopsieeee;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PatientDashboard extends Application {

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        VBox sidebar = createSidebar();
        VBox header = createHeader();
        VBox mainContent = createMainContent();
        VBox rightPanel = createRightPanel();

        root.setLeft(sidebar);
        root.setTop(header);
        root.setCenter(mainContent);
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Patient Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #f5f5fc;");
        sidebar.setPrefWidth(200);

        Label title = new Label("Hospital");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.DARKMAGENTA);

        Button dashboard = createSidebarButton("Dashboard");
        Button appointments = createSidebarButton("Appointments");
        Button records = createSidebarButton("Medical Records");
        Button prescriptions = createSidebarButton("Prescriptions");
        Button profile = createSidebarButton("Profile");
        Button logout = new Button("Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle("-fx-background-color: darkmagenta; -fx-text-fill: white;");

        sidebar.getChildren().addAll(title, dashboard, appointments, records, prescriptions, profile, logout);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
        return button;
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #ffffff;");
        Label title = new Label("Welcome, Patient");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        header.getChildren().add(title);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        HBox cards = new HBox(20);
        cards.getChildren().addAll(
                createCard("Upcoming Appointment", "10:00 AM"),
                createCard("Pending Fees", "$120"),
                createCard("Active Prescriptions", "2")
        );

        VBox recent = new VBox(10);
        recent.getChildren().addAll(
                new Label("Recent Activities:"),
                new Label("- 12 Apr: General Checkup"),
                new Label("- 10 Apr: Prescription Updated")
        );

        content.getChildren().addAll(cards, recent);
        return content;
    }

    private VBox createRightPanel() {
        VBox right = new VBox(15);
        right.setPadding(new Insets(20));
        right.setAlignment(Pos.TOP_CENTER);
        right.setStyle("-fx-background-color: #ffffff;");
        right.setPrefWidth(250);

        Image image;
        try {
            image = new Image("https://www.w3schools.com/howto/img_avatar.png", 80, 80, true, true);
            if (image.isError()) throw new Exception("Failed");
        } catch (Exception e) {
            image = new Image(getClass().getResourceAsStream("/default_avatar.png"));
        }

        ImageView profilePic = new ImageView(image);
        Label name = new Label("John Doe");
        name.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox notifications = new VBox(5);
        notifications.getChildren().addAll(
                new Label("- New appointment confirmed"),
                new Label("- Lab results uploaded"),
                new Label("- Reminder: Checkup tomorrow")
        );

        Label notifTitle = new Label("Notifications");
        notifTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button help = new Button("Need Help?");
        help.setStyle("-fx-background-color: darkmagenta; -fx-text-fill: white;");

        right.getChildren().addAll(profilePic, name, notifTitle, notifications, help);
        return right;
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox();
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
        card.setPrefSize(200, 100);

        Label labelTitle = new Label(title);
        labelTitle.setFont(Font.font("Arial", 14));
        Label labelValue = new Label(value);
        labelValue.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        card.getChildren().addAll(labelTitle, labelValue);
        return card;
    }


}
