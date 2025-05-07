package com.hamza6dev.oopsieeee;
import Appointment.Appointment;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import User.*;


public class Dashboard extends Application {
    private String accountType;
    private String accountID;
    private User user;

    public Dashboard(String accountType, String accountID) {
        this.accountType = accountType;
        this.accountID = accountID;
        initializeUser();
        System.out.println(user);
    }

    @Override
    public void start(Stage primaryStage) {

        // === Navigation Bar (Top) ===
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: white;");
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setSpacing(30);

        // Logo (Left)
        HBox logoBox = new HBox(5);
        Label logo = new Label("CheapAHH");
        logo.setStyle("-fx-text-fill: blue; -fx-font-family: Poppins");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logoBox.getChildren().add(logo);

        Button logout = new Button("Log out");
        logout.setAlignment(Pos.CENTER_RIGHT);

        Region middleSpacer = new Region();
        HBox.setHgrow(middleSpacer, Priority.ALWAYS);

        logout.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 5px 20px");

        logout.setOnAction(e -> {
            HelloApplication home = new HelloApplication();
            this.user = null;
            this.accountType = null;
            this.accountID = null;
            try {
                home.start((Stage) ((Node) e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Add logo to the navbar
        navBar.getChildren().addAll(logoBox, middleSpacer ,logout);

        // === Sidebar (Left) ===
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px;");
        sidebar.setPrefWidth(200);


        Button dashboardBtn = createSidebarButton("Dashboard");
        Button patientsBtn = createSidebarButton("Patients");
        Button appointmentsBtn = createSidebarButton("Appointments");

        Button messagesBtn = createSidebarButton("Messages");

        sidebar.getChildren().addAll(dashboardBtn, patientsBtn, appointmentsBtn, messagesBtn);

        // === Content Area (Right) ===
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));

        // Profile picture (placeholder icon)
        ImageView profileImage = new ImageView(new Image("default-avatar.png")); // or use a URL
        profileImage.setFitWidth(100);
        profileImage.setFitHeight(100);
        profileImage.setPreserveRatio(true);

        // Text labels
        Label nameLabel = new Label(user.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold  ;");

        Label infoLabel = new Label(user.getGender() + ", " + user.getAge() + " years");

        Label emailLabel = new Label(user.getEmail());
        Label phoneLabel = new Label(user.getPhone());

        // Layouts for info
        HBox emailPhoneBox = new HBox(20, emailLabel, phoneLabel);
        VBox textBox = new VBox(5, nameLabel, infoLabel, emailPhoneBox);
        textBox.setPadding(new Insets(20, 0, 20, 0));
        emailPhoneBox.setAlignment(Pos.CENTER_LEFT);

        // Card layout
        HBox profileCard = new HBox(20, profileImage, textBox);
        profileCard.setPadding(new Insets(20));
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");


        // Main Layout
        VBox main = renderMainContainer();


        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        content.getChildren().addAll(profileCard, main);

        // creating appointment table
        TableView<Appointment> table = new TableView<>();

        // Columns
        TableColumn<Appointment, String> idCol = new TableColumn<>("Appointment ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));



        table.getColumns().addAll(idCol, patientCol, dateCol, timeCol, statusCol);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Apply styling to the table
        table.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Apply styling to columns
        for (TableColumn<Appointment, ?> column : table.getColumns()) {
            column.setStyle(
                    "-fx-alignment: CENTER-LEFT;" +
                            "-fx-font-family: 'Arial';" +
                            "-fx-font-weight: normal;" +
                            "-fx-background-color: blue;" +  // Set the background color of the column
                            "-fx-text-fill: white;"        // Set the text color to white
            );
        }

        // Provide placeholder text for when there are no rows
        Label tablePlaceholder = new Label("No appointments available");
        tablePlaceholder.setStyle("-fx-text-fill: #888;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: Arial;");
        table.setPlaceholder(tablePlaceholder);


        // making all the sidebar buttons in use
        dashboardBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().addAll(profileCard, main);
        });

        patientsBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(new Label("Patients"));
        });

        appointmentsBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().addAll(table);
        });

        messagesBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(new Label("Messages"));
        });

        // === Final Layout with BorderPane ===
        BorderPane root = new BorderPane();
        root.setTop(navBar);          // Add the navigation bar to the top
        root.setLeft(sidebar);        // Add the sidebar to the left
        root.setCenter(content);      // Add the main content to the center

        // === Scene and Stage ===
        Scene scene = new Scene(root, 1080, 720);
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper Method: Create Buttons for Sidebar
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");

        btn.setOnMouseEntered(e-> {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: blue; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");
        });

        btn.setOnMouseExited(e-> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");
        });

        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    private HBox createInfoRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        Label value = new Label(valueText);
        HBox row = new HBox(10, label, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox renderMainContainer() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Dynamically render content based on accountType
        if ("doctor".equals(accountType)) {
            mainContainer.getChildren().addAll(renderDoctorContent());
        } else if ("patient".equals(accountType)) {
            mainContainer.getChildren().addAll(renderPatientContent());
        }

        return mainContainer;
    }


    private VBox renderDoctorContent() {
        Doctor doc = null;
        try {
            doc = (Doctor) user;
        } catch (Exception e) {
            System.out.println("Unable to cast user to Doctor");
        }
        // Doctor-related content
        // Section Title
        Label availabilityTitle = new Label("Availability");
        availabilityTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Availability Section
        GridPane availabilityGrid = new GridPane();
        availabilityGrid.setVgap(10);
        availabilityGrid.setHgap(40);
        availabilityGrid.setPadding(new Insets(10, 0, 20, 0));

        availabilityGrid.add(new Label("Available Days:"), 0, 0);
        availabilityGrid.add(new Label("Monday, Tuesday, Wednesday"), 1, 0);

        availabilityGrid.add(new Label("Start Time:"), 0, 1);
        availabilityGrid.add(new Label("07:30"), 1, 1);

        availabilityGrid.add(new Label("End Time:"), 0, 2);
        availabilityGrid.add(new Label("17:30"), 1, 2);

        VBox availabilitySection = new VBox(10, availabilityTitle, availabilityGrid);
        availabilitySection.setPadding(new Insets(10));

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", user.getUserID()),
                createInfoRow("Address :", user.getAddress())
        );
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(" -fx-border-radius: 5; -fx-background-radius: 5;");

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Specialization :", doc.getSpeciality()),
                createInfoRow("License Number :", doc.getPMDC_NO())
        );
        rightInfo.setPadding(new Insets(10));
        rightInfo.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        // Info Grid
        HBox infoSection = new HBox(40, leftInfo, rightInfo);
        infoSection.setPadding(new Insets(0, 0, 20, 0));

        // Fee and Experience
        VBox bottomRow = new VBox(10,
                createInfoRow("Consultation Fee :", "$200.0"),
                createInfoRow("Experience Years :", doc.getExperience() + " years")
        );
        bottomRow.setPadding(new Insets(10));

        VBox doctorInfo = new VBox(10, availabilitySection, infoSection, bottomRow);
        doctorInfo.setPadding(new Insets(10));

        return doctorInfo;
    }

    private VBox renderPatientContent() {
        Patient patient = null;
        try {
            patient = (Patient) user;
        } catch (Exception e) {
            System.out.println("Unable to cast user to Patient");
        }

        // Create labels
        Label diagnosis = new Label("Diagnosis: Hypertension");
        Label admitStatus = new Label("Admit Status: Outpatient");
        Label attendingDoctor = new Label("Attending Doctor: Dr. Smith");
        Label roomNumber = new Label("Room Number: None");
        Label prescribedMedications = new Label("Prescribed Medications: Amlodipine");

        // Create a GridPane to place labels
        GridPane grid = new GridPane();
        grid.setVgap(10);  // Vertical spacing between rows
        grid.setHgap(20);  // Horizontal spacing (if needed for multiple columns)
        grid.setPadding(new Insets(10));  // Padding around the grid

        // Add labels to the grid (row by row)
        grid.add(new Label("Diagnosis:"), 0, 0); // Column 0, Row 0
        grid.add(diagnosis, 1, 0);               // Column 1, Row 0

        grid.add(new Label("Admit Status:"), 0, 1); // Column 0, Row 1
        grid.add(admitStatus, 1, 1);                // Column 1, Row 1

        grid.add(new Label("Attending Doctor:"), 0, 2); // Column 0, Row 2
        grid.add(attendingDoctor, 1, 2);                // Column 1, Row 2

        grid.add(new Label("Room Number:"), 0, 3); // Column 0, Row 3
        grid.add(roomNumber, 1, 3);                // Column 1, Row 3

        grid.add(new Label("Prescribed Medications:"), 0, 4); // Column 0, Row 4
        grid.add(prescribedMedications, 1, 4);                // Column 1, Row 4

        // Set alignment and styles for the grid
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setStyle("-fx-font-size: 14px; -fx-font-family: Arial;");

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", patient.getUserID()),
                createInfoRow("Address :", patient.getAddress())
        );
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(" -fx-border-radius: 5; -fx-background-radius: 5;");

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Admit Status :", patient.isAdmit() ? "Admitted" : "Not Admitted"),
                createInfoRow("Diagnosis:", "Delulu"),
                createInfoRow("Medications :", "Be real my nigga")
        );
        rightInfo.setPadding(new Insets(10));
        rightInfo.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        // Info Grid
        HBox infoSection = new HBox(40, leftInfo, rightInfo);
        infoSection.setPadding(new Insets(0, 0, 20, 0));

        VBox patientInfo = new VBox(10, infoSection);
        patientInfo.setPadding(new Insets(10));

        return patientInfo;
    }

    private void initializeUser() {
        try {
            if(accountType.equals("doctor"))
                user = DataFetcher.getDoctorData(accountType, accountID);
            else if(accountType.equals("patient"))
                user = DataFetcher.getPatientData(accountType, accountID);
        } catch(Exception e) {
            System.out.println("Database Error: Unable to retrieve data.");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}