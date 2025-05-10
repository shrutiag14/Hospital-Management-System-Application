package com.hamza6dev.oopsieeee;

import Appointment.*;
import Appointment.Appointment.AppointmentStatus;
import Notifications.EmailNotification;
import User.*;

import Exceptions.DuplicateAppointmentException;
import Exceptions.InvalidAppointmentException;
import HealthData.Vitals;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

public class Dashboard extends Application {
    private String accountType;
    private String accountID;
    private User user;
    private VBox content;

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

        logout.setStyle(
                "-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 5px 20px");

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
        navBar.getChildren().addAll(logoBox, middleSpacer, logout);

        // === Sidebar (Left) ===
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px;");
        sidebar.setPrefWidth(200);

        Button dashboardBtn = createSidebarButton("Dashboard");
        Button patientsBtn = createSidebarButton("Patients");
        Button doctorsBtn = createSidebarButton("Doctors");
        Button appointmentsBtn = createSidebarButton("Appointments");
        Button messagesBtn = createSidebarButton("Messages");
        Button emailBtn = createSidebarButton("Email");
        Button patientEvaluation = createSidebarButton("Patient Evaluation");

        if (user instanceof Doctor) {
            Button vitalsBtn = createSidebarButton("View Vitals");
            vitalsBtn.setOnAction(_ -> {
                content.getChildren().clear();
                content.getChildren().add(createVitalsViewPage());
            });
            sidebar.getChildren().addAll(dashboardBtn, patientsBtn, appointmentsBtn, vitalsBtn, messagesBtn, emailBtn,
                    patientEvaluation);
        } else if (user instanceof Patient) {
            Button uploadVitalsBtn = createSidebarButton("Upload Vitals");
            uploadVitalsBtn.setOnAction(_ -> {
                content.getChildren().clear();
                content.getChildren().add(createVitalsUploadPage());
            });
            sidebar.getChildren().addAll(dashboardBtn, doctorsBtn, appointmentsBtn, uploadVitalsBtn, messagesBtn,
                    emailBtn);
        }

        // === Content Area (Right) ===
        content = new VBox(30);
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

        // ***************************** USERS TAB ****************************

        VBox users = null;
        if (user instanceof Doctor) {
            users = createDoctorPatients(user.getUserID());
        }

        if (user instanceof Patient) {
            users = createPatientDoctors(user.getUserID());
        }

        // ****************************** APPOINTMENT TAB **********************8
        // Appointments Table
        TableView<Appointment> table = new TableView<>();

        // Columns
        // Appointment ID Column
        TableColumn<Appointment, String> idCol = new TableColumn<>("Appointment ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));

        // Patient Name Column
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor Name");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        // Date Column (local date as an object or formatted string)
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate")); // Use formatted date

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, timeCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // appointment data
        List<Appointment> appointments = getAppointments();
        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList(appointments);
        table.setItems(appointmentData);

        // Styling for TableView
        table.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Set a placeholder for the table
        Label placeholder = new Label("No appointments available.");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        table.setPlaceholder(placeholder);

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by patient name, doctor name or appointment ID");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);

        // Search Bar Listener for Real-Time Filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filter the list of appointments
            ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

            for (Appointment appointment : appointmentData) {
                // Check if the patient name or appointment ID contains the search term
                // (case-insensitive)
                if ((appointment.getPatient().getName().toLowerCase().contains(newValue.toLowerCase())) ||
                        (appointment.getAppointmentID().toLowerCase().contains(newValue.toLowerCase())) ||
                        (appointment.getDoctor().getName().toLowerCase().contains(newValue.toLowerCase()))) {
                    filteredAppointments.add(appointment);
                }
            }

            // Update the table view with the filtered appointments
            table.setItems(filteredAppointments);
        });

        // Action Buttons
        Button addAppointmentBtn = new Button("Add Appointment");
        addAppointmentBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        addAppointmentBtn.setPrefHeight(40);
        addAppointmentBtn.setOnAction(event -> {
            AppointmentManager appointmentManager = new AppointmentManager();

            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(10));

            // Title
            Text heading1 = new Text("Book an Appointment");
            heading1.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            heading1.setFill(Color.BLUE); // ← your requested color
            heading1.setTextAlignment(TextAlignment.LEFT);
            VBox.setMargin(heading1, new Insets(0, 0, 10, 0));

            VBox formContainer = new VBox(15);
            formContainer.setPadding(new Insets(30));
            formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
            formContainer.setMaxWidth(600);

            Label patientSectionLabel = new Label("Patient Information");
            patientSectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            GridPane patientGrid = new GridPane();
            patientGrid.setHgap(15);
            patientGrid.setVgap(15);
            patientGrid.setPadding(new Insets(10, 0, 20, 0));

            Label patientName = new Label("Patient Name:");
            patientName.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            TextField nameFieldAppointment = new TextField(user.getName());
            nameFieldAppointment.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");
            nameFieldAppointment.setEditable(false);

            patientGrid.add(patientName, 0, 0);
            patientGrid.add(nameFieldAppointment, 1, 0);

            Label detailsSectionLabel = new Label("Appointment Details");
            detailsSectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(15);
            detailsGrid.setVgap(15);
            detailsGrid.setPadding(new Insets(10, 0, 20, 0));

            Label doctorLabel = new Label("Select Doctor:");
            doctorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            ComboBox<Doctor> doctorComboBox = new ComboBox<>();
            doctorComboBox.setPrefWidth(250);
            doctorComboBox.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

            ArrayList<Doctor> doctors = new ArrayList<>();
            try {
                doctors.addAll(DataFetcher.getAllDoctors());
            } catch (SQLException e) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Unable to fetch doctors from database.");
            }

            doctorComboBox.getItems().addAll(doctors);

            // Set CellFactory to display the combined text in the ComboBox
            doctorComboBox.setCellFactory(param -> new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor doctor, boolean empty) {
                    super.updateItem(doctor, empty);
                    if (empty || doctor == null) {
                        setText(null);
                    } else {
                        setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                    }
                }
            });

            // Set the ButtonCell to display the selected doctor's name and specialty
            doctorComboBox.setButtonCell(new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor doctor, boolean empty) {
                    super.updateItem(doctor, empty);
                    if (empty || doctor == null) {
                        setText(null);
                    } else {
                        setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                    }
                }
            });

            Label dateLabel = new Label("Select Date:");
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            DatePicker datePicker = new DatePicker();
            datePicker.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

            Label timeLabel = new Label("Select Time:");
            timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            ComboBox<LocalTime> timeComboBox = new ComboBox<>();
            timeComboBox.setPrefWidth(250);
            timeComboBox.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");
            // Add times every 30 minutes
            IntStream.rangeClosed(8, 17).forEach(hour -> {
                timeComboBox.getItems().add(LocalTime.of(hour, 0));
                timeComboBox.getItems().add(LocalTime.of(hour, 30));
            });

            detailsGrid.add(doctorLabel, 0, 0);
            detailsGrid.add(doctorComboBox, 1, 0);
            detailsGrid.add(dateLabel, 0, 1);
            detailsGrid.add(datePicker, 1, 1);
            detailsGrid.add(timeLabel, 0, 2);
            detailsGrid.add(timeComboBox, 1, 2);

            Button bookBtn = new Button("Book Appointment");
            bookBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-padding: 10px 20px; -fx-background-radius: 5px;");
            bookBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(bookBtn, Priority.ALWAYS);

            Label statusLabel = new Label();
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            bookBtn.setOnAction(e -> {
                try {
                    LocalDate date = datePicker.getValue();
                    LocalTime time = timeComboBox.getValue();
                    Doctor selectedDoctor = doctorComboBox.getValue();

                    if (date == null || time == null || selectedDoctor == null) {
                        statusLabel.setText("Please fill all fields correctly.");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    LocalDateTime dateTime = LocalDateTime.of(date, time);
                    appointmentManager.requestAppointment(dateTime, selectedDoctor, (Patient) user);
                    statusLabel.setText("Appointment booked successfully!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                    // === Update the TableView ===
                    List<Appointment> updatedAppointments = getAppointments(); // Fetch new appointments
                    ObservableList<Appointment> updatedData = FXCollections.observableArrayList(updatedAppointments);
                    table.setItems(updatedData); // Update the TableView

                    content.getChildren().clear();
                    content.getChildren().addAll(profileCard, main);

                } catch (InvalidAppointmentException | DuplicateAppointmentException ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                } catch (Exception ex) {
                    statusLabel.setText("An unexpected error occurred.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            });

            formContainer.getChildren().addAll(
                    patientSectionLabel,
                    patientGrid,
                    detailsSectionLabel,
                    detailsGrid,
                    bookBtn,
                    statusLabel);

            mainContainer.getChildren().addAll(heading1, formContainer);
            content.getChildren().clear();
            content.getChildren().add(mainContainer);
        });

        Button updateAppointmentBtn = new Button("Update Appointment");
        updateAppointmentBtn.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-size: 14px;");
        updateAppointmentBtn.setPrefHeight(40);
        updateAppointmentBtn.setOnAction(event -> {
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Update Error",
                        "Please select an appointment to update.");
                return;
            }

            if (!(String.valueOf(selectedAppointment.getStatus()).equalsIgnoreCase("PENDING"))) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Status Update Error",
                        "Cannot change status for an appointment that is not in pending stage.");
                return;
            }

            // Open the Update Appointment Dialog
            openUpdateAppointmentDialog(selectedAppointment, table);
        });

        Button cancelAppointment = new Button("Cancel Appointment");
        cancelAppointment.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelAppointment.setPrefHeight(40);
        cancelAppointment.setOnAction(event -> {
            // Get the selected appointment from the table
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Cancel Error",
                        "Please select an appointment to delete.");
                return;
            }

            // Prompt user for confirmation before deletion
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Cancel Confirmation");
            confirmationAlert.setHeaderText("Are you sure you want to cancel this appointment?");

            // Wait for user's response (OK or Cancel)
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        // Prepare the DELETE query
                        String cancelQuery = "UPDATE appointment " +
                                "SET status = 'CANCELLED' " +
                                "WHERE appointment_id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(cancelQuery)) {
                            // Set the appointment ID parameter
                            stmt.setString(1, selectedAppointment.getAppointmentID());

                            // Execute the query
                            int rowsAffected = stmt.executeUpdate();

                            if (rowsAffected > 0) {
                                // Refresh the TableView by fetching updated appointments
                                List<Appointment> updatedAppointments = getAppointments();
                                ObservableList<Appointment> updatedData = FXCollections
                                        .observableArrayList(updatedAppointments);
                                table.setItems(updatedData);

                                // Display success message
                                AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Cancel Success",
                                        "Appointment cancelled successfully!");
                            } else {
                                throw new SQLException("Appointment not found in the database.");
                            }
                        }
                    } catch (SQLException ex) {
                        // Handle database-related errors
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Delete Error",
                                "An error occurred while deleting the appointment: " + ex.getMessage());
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        // Handle other errors
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Delete Error",
                                "An unexpected error occurred: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
        });

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");
        viewDetailsBtn.setPrefHeight(40);
        viewDetailsBtn.setOnAction(event -> {
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();
            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "View Error",
                        "Please select an appointment to view.");
                return;
            }
            // Display the appointment details in a pop-up dialog
            showAppointmentDetailsPopup(selectedAppointment);
        });

        Button changeStatusButton = new Button("Change Status");
        changeStatusButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        changeStatusButton.setPrefHeight(40);
        changeStatusButton.setOnAction(event -> {
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Status Update Error",
                        "Please select an appointment to update the status.");
                return;
            }

            if (!(String.valueOf(selectedAppointment.getStatus()).equalsIgnoreCase("PENDING"))) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Status Update Error",
                        "Cannot change status for an appointment that is not in pending stage.");
                return;
            }

            // Open Change Status Dialog
            openChangeStatusDialog(selectedAppointment, table);
        });

        // Button Layout
        HBox buttonLayout = new HBox(10);

        if (user instanceof Patient)
            buttonLayout = new HBox(10, addAppointmentBtn, updateAppointmentBtn, cancelAppointment, viewDetailsBtn);
        else if (user instanceof Doctor)
            buttonLayout = new HBox(10, changeStatusButton, viewDetailsBtn);

        buttonLayout.setAlignment(Pos.CENTER_LEFT);
        buttonLayout.setPadding(new Insets(10, 0, 10, 0));

        // Final Layout for Appointments Tab
        VBox appointmentsLayout = new VBox(20);
        appointmentsLayout.getChildren().addAll(searchField, table, buttonLayout);
        appointmentsLayout.setPadding(new Insets(20, 0, 0, 0));

        // making all the sidebar buttons in use
        dashboardBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().addAll(profileCard, main);
        });
        VBox finalUsers = users;
        patientsBtn.setOnAction(event -> {
            content.getChildren().clear();

            Text patients = new Text("Patients");
            patients.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            patients.setFill(Color.BLUE);
            patients.setTextAlignment(TextAlignment.LEFT);
            VBox.setMargin(patients, new Insets(0, 0, 10, 0));

            content.getChildren().addAll(patients, finalUsers);
        });

        VBox finalUsers1 = users;
        doctorsBtn.setOnAction(event -> {
            content.getChildren().clear();

            Text doctors = new Text("Patients");
            doctors.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            doctors.setFill(Color.BLUE);
            doctors.setTextAlignment(TextAlignment.LEFT);
            VBox.setMargin(doctors, new Insets(0, 0, 10, 0));

            content.getChildren().addAll(doctors, finalUsers1);
        });

        appointmentsBtn.setOnAction(e -> {
            content.getChildren().clear();

            content.getChildren().addAll(appointmentsLayout);
        });

        messagesBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(new Label("Messages"));
        });

        emailBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(createEmailPage());
        });

        patientEvaluation.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(createEvaluationPage(user.getUserID()));
        });

        // === Final Layout with BorderPane ===
        BorderPane root = new BorderPane();
        root.setTop(navBar); // Add the navigation bar to the top
        root.setLeft(sidebar); // Add the sidebar to the left
        root.setCenter(content); // Add the main content to the center

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
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: white; -fx-text-fill: blue; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");
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
        availabilityGrid.add(new Label(String.valueOf(doc.getStartTime())), 1, 1);

        availabilityGrid.add(new Label("End Time:"), 0, 2);
        availabilityGrid.add(new Label(String.valueOf(doc.getEndTime())), 1, 2);

        VBox availabilitySection = new VBox(10, availabilityTitle, availabilityGrid);
        availabilitySection.setPadding(new Insets(10));

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", user.getUserID()),
                createInfoRow("Address :", user.getAddress()));
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(" -fx-border-radius: 5; -fx-background-radius: 5;");

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Specialization :", doc.getSpeciality()),
                createInfoRow("License Number :", doc.getPMDC_NO()));
        rightInfo.setPadding(new Insets(10));
        rightInfo.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        // Info Grid
        HBox infoSection = new HBox(40, leftInfo, rightInfo);
        infoSection.setPadding(new Insets(0, 0, 20, 0));

        // Fee and Experience
        VBox bottomRow = new VBox(10,
                createInfoRow("Consultation Fee :", String.valueOf(doc.getConsulationFee())),
                createInfoRow("Experience Years :", (doc.getExperience() + " years")));
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
        grid.setVgap(10); // Vertical spacing between rows
        grid.setHgap(20); // Horizontal spacing (if needed for multiple columns)
        grid.setPadding(new Insets(10)); // Padding around the grid

        // Add labels to the grid (row by row)
        grid.add(new Label("Diagnosis:"), 0, 0); // Column 0, Row 0
        grid.add(diagnosis, 1, 0); // Column 1, Row 0

        grid.add(new Label("Admit Status:"), 0, 1); // Column 0, Row 1
        grid.add(admitStatus, 1, 1); // Column 1, Row 1

        grid.add(new Label("Attending Doctor:"), 0, 2); // Column 0, Row 2
        grid.add(attendingDoctor, 1, 2); // Column 1, Row 2

        grid.add(new Label("Room Number:"), 0, 3); // Column 0, Row 3
        grid.add(roomNumber, 1, 3); // Column 1, Row 3

        grid.add(new Label("Prescribed Medications:"), 0, 4); // Column 0, Row 4
        grid.add(prescribedMedications, 1, 4); // Column 1, Row 4

        // Set alignment and styles for the grid
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setStyle("-fx-font-size: 14px; -fx-font-family: Arial;");

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", patient.getUserID()),
                createInfoRow("Address :", patient.getAddress()));
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(" -fx-border-radius: 5; -fx-background-radius: 5;");

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Admit Status :", patient.isAdmit() ? "Admitted" : "Not Admitted"),
                createInfoRow("Diagnosis:", "Delulu"),
                createInfoRow("Medications :", "Be real my nigga"));
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
            if (accountType.equals("doctor"))
                user = DataFetcher.getDoctorData(accountType, accountID);
            else if (accountType.equals("patient"))
                user = DataFetcher.getPatientData(accountType, accountID);
        } catch (Exception e) {
            System.out.println("Database Error: Unable to retrieve data.");
        }
    }

    public List<Appointment> getAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        // Check if the user is null
        if (this.user == null) {
            System.out.println("No user is currently logged in.");
            return appointments;
        }

        // Determine whether the user is a doctor or a patient
        String query;
        if (user instanceof Doctor) {
            query = "SELECT appointment_id, date_time, doctor_id, patient_id, status " +
                    "FROM appointment WHERE doctor_id = ?";
        } else if (user instanceof Patient) {
            query = "SELECT appointment_id, date_time, doctor_id, patient_id, status " +
                    "FROM appointment WHERE patient_id = ?";
        } else {
            System.out.println("Invalid user type.");
            return appointments;
        }

        // Execute the query
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the user ID as a parameter
            stmt.setString(1, user.getUserID());

            // Execute the query and process the ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a new Appointment object
                    String appointmentId = rs.getString("appointment_id");
                    LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                    String doctorId = rs.getString("doctor_id");
                    String patientId = rs.getString("patient_id");
                    AppointmentStatus status = AppointmentStatus.valueOf(rs.getString("status"));

                    Doctor doctor = null;
                    Patient patient = null;

                    if (user instanceof Doctor) {
                        doctor = (Doctor) user; // Current user is a doctor
                        patient = DataFetcher.getPatientData("patient", patientId); // Fetch patient details
                    } else if (user instanceof Patient) {
                        patient = (Patient) user; // Current user is a patient
                        doctor = DataFetcher.getDoctorData("doctor", doctorId); // Fetch doctor details
                    }

                    appointments.add(new Appointment(appointmentId, dateTime, doctor, patient, status));
                }
            } catch (InvalidAppointmentException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching appointments: " + e.getMessage());
        }

        return appointments;
    }

    private void showAppointmentDetailsPopup(Appointment appointment) {
        // Create a new Dialog for displaying details
        Dialog<Void> detailsDialog = new Dialog<>();
        detailsDialog.setTitle("Appointment Details");

        // Create the Dialog's content
        VBox detailsContainer = new VBox(10);
        detailsContainer.setPadding(new Insets(20));

        // Add details fields
        Label titleLabel = new Label("Appointment Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox idRow = createDetailRow("Appointment ID:", appointment.getAppointmentID());
        HBox doctorRow = createDetailRow("Doctor:",
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : "N/A");
        HBox patientRow = createDetailRow("Patient:",
                appointment.getPatient() != null ? appointment.getPatient().getName() : "N/A");
        HBox dateRow = createDetailRow("Date:", appointment.getDateTime().toLocalDate().toString());
        HBox timeRow = createDetailRow("Time:", appointment.getDateTime().toLocalTime().toString());
        HBox statusRow = createDetailRow("Status:", appointment.getStatus().toString());

        // Add all rows to container
        detailsContainer.getChildren().addAll(titleLabel, idRow, doctorRow, patientRow, dateRow, timeRow, statusRow);

        // Set the content of the dialog
        detailsDialog.getDialogPane().setContent(detailsContainer);

        // Disable the default buttons
        detailsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Show the dialog
        detailsDialog.showAndWait();
    }

    private HBox createDetailRow(String label, String value) {
        Label labelField = new Label(label);
        labelField.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");

        Label valueField = new Label(value);
        valueField.setStyle("-fx-text-fill: #555; -fx-font-size: 14px;");

        HBox row = new HBox(10, labelField, valueField);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox createUpdateRow(String labelText, Node inputField) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");

        inputField.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

        HBox row = new HBox(10, label, inputField);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void openUpdateAppointmentDialog(Appointment appointment, TableView<Appointment> table) {
        // Create a Dialog window for updating the appointment
        Dialog<Void> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Appointment");

        VBox dialogContainer = new VBox(15);
        dialogContainer.setPadding(new Insets(20));

        // Add Title
        Label titleLabel = new Label("Update Appointment Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create Input Fields
        DatePicker datePicker = new DatePicker(appointment.getDateTime().toLocalDate());
        ComboBox<LocalTime> timeComboBox = new ComboBox<>();
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();

        // Populate Times
        for (int hour = 8; hour <= 17; hour++) {
            timeComboBox.getItems().add(LocalTime.of(hour, 0));
            timeComboBox.getItems().add(LocalTime.of(hour, 30));
        }
        timeComboBox.setValue(appointment.getDateTime().toLocalTime());

        doctorComboBox.setPrefWidth(250);
        doctorComboBox.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            doctors.addAll(DataFetcher.getAllDoctors());
        } catch (SQLException e) {
            AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Unable to fetch doctors from database.");
        }

        doctorComboBox.getItems().addAll(doctors);

        // Set CellFactory to display the combined text in the ComboBox
        doctorComboBox.setCellFactory(param -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                }
            }
        });

        // Set the ButtonCell to display the selected doctor's name and specialty
        doctorComboBox.setButtonCell(new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                }
            }
        });
        doctorComboBox.setValue(appointment.getDoctor());

        // Add Input Fields to Dialog
        dialogContainer.getChildren().addAll(
                titleLabel,
                createUpdateRow("New Date", datePicker),
                createUpdateRow("New Time", timeComboBox),
                createUpdateRow("New Doctor", doctorComboBox));

        // Add Buttons
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        HBox buttons = new HBox(10, updateButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        dialogContainer.getChildren().add(buttons);

        // Handle Update Button Click
        updateButton.setOnAction(e -> {
            // Validation
            LocalDate date = datePicker.getValue();
            LocalTime time = timeComboBox.getValue();
            Doctor doctor = doctorComboBox.getValue();

            if (date == null || time == null || doctor == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
                return;
            }

            LocalDateTime newDateTime = LocalDateTime.of(date, time);
            if (newDateTime.isBefore(LocalDateTime.now())) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "The selected date and time must be in the future.");
                return;
            }

            try {
                // Check for Duplicate Appointments
                AppointmentManager appointmentManager = new AppointmentManager();
                if (appointmentManager.isDuplicateAppointment(newDateTime, doctor, appointment.getPatient())) {
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Duplicate Error",
                            "An appointment already exists at this date and time for the selected doctor.");
                    return;
                }

                // Update Appointment in Database
                updateAppointmentInDatabase(appointment.getAppointmentID(), newDateTime, doctor);

                // Refresh the TableView
                table.setItems(FXCollections.observableArrayList(getAppointments()));

                // Show Success Notification
                AlertDialogueBox.showAlert(Alert.AlertType.CONFIRMATION, "Update Successful",
                        "The appointment has been successfully updated.");
                updateDialog.close();
            } catch (SQLException ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                        "An error occurred while updating the appointment: " + ex.getMessage());
            }
        });

        // Set Dialog Content and Show Dialog
        updateDialog.getDialogPane().setContent(dialogContainer);
        updateDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); // Disable default buttons
        updateDialog.showAndWait();
    }

    private void updateAppointmentInDatabase(String appointmentId, LocalDateTime newDateTime, Doctor newDoctor)
            throws SQLException {
        // SQL Query to Update Appointment in the Database
        String updateQuery = "UPDATE appointment " +
                "SET date_time = ?, doctor_id = ?, status = ? " +
                "WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            // Set Parameters
            stmt.setTimestamp(1, Timestamp.valueOf(newDateTime)); // New Date and Time
            stmt.setString(2, newDoctor.getUserID()); // New Doctor ID
            stmt.setString(3, "PENDING");
            stmt.setString(4, appointmentId); // Appointment ID

            // Execute the Update Query
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update appointment. Appointment ID not found.");
            }
        }
    }

    private void openChangeStatusDialog(Appointment appointment, TableView<Appointment> table) {
        // Create a Dialog for Changing Status
        Dialog<Void> statusDialog = new Dialog<>();
        statusDialog.setTitle("Change Appointment Status");

        VBox dialogContainer = new VBox(15);
        dialogContainer.setPadding(new Insets(20));

        Label titleLabel = new Label("Change Status for Appointment: " + appointment.getAppointmentID());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<AppointmentStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(AppointmentStatus.values());
        statusComboBox.setValue(appointment.getStatus());

        HBox statusRow = createUpdateRow("New Status", statusComboBox);
        dialogContainer.getChildren().addAll(titleLabel, statusRow);

        // Add Change Button
        Button changeButton = new Button("Change Status");
        changeButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");

        changeButton.setOnAction(e -> {
            AppointmentStatus selectedStatus = statusComboBox.getValue();

            if (selectedStatus == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a status.");
                return;
            }

            try {
                AppointmentManager appointmentManager = new AppointmentManager();
                appointmentManager.updateAppointmentStatus(appointment, selectedStatus);

                // Refresh Table Data
                table.setItems(FXCollections.observableArrayList(getAppointments()));

                // Success Notification
                AlertDialogueBox.showAlert(Alert.AlertType.CONFIRMATION, "Status Update Successful",
                        "The status has been successfully updated.");
                statusDialog.close();
            } catch (Exception ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Update Failed",
                        "Unable to update status: " + ex.getMessage());
            }
        });

        HBox buttons = new HBox(10, changeButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        dialogContainer.getChildren().add(buttons);

        // Set Dialog Content
        statusDialog.getDialogPane().setContent(dialogContainer);
        statusDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        statusDialog.showAndWait();
    }

    public VBox createDoctorPatients(String doctorID) {
        // Get the list of patients returned from the getAllPatientsForDoctor method
        List<Patient> patientList = DataFetcher.getAllPatientsForDoctor(doctorID);

        // Convert the list of patients to an ObservableList for TableView
        ObservableList<Patient> patients = FXCollections.observableArrayList(patientList);

        // Create the TableView
        TableView<Patient> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style the table
        tableView.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Create columns for Patient Table
        TableColumn<Patient, String> userIdColumn = new TableColumn<>("Patient ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Patient, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Patient, Boolean> isAdmittedColumn = new TableColumn<>("Is Admitted");
        isAdmittedColumn.setCellValueFactory(new PropertyValueFactory<>("admit"));

        // Add the columns to the table
        tableView.getColumns().addAll(userIdColumn, nameColumn, dobColumn, genderColumn, addressColumn, phoneColumn,
                isAdmittedColumn);

        // Set the data to the table
        tableView.setItems(patients);

        // Set a placeholder for the table
        Label placeholder = new Label("No patients found for " + user.getName() + ".");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        tableView.setPlaceholder(placeholder);

        // Add the table to a layout (VBox in this case)
        return new VBox(20, tableView);
    }

    public VBox createPatientDoctors(String patientID) {
        // Get the list of doctors returned from the getAllPatientsForDoctor method
        List<Doctor> doctorList = DataFetcher.getAllDoctorsForPatient(patientID);

        // Convert the list of doctors to an ObservableList for TableView
        ObservableList<Doctor> doctors = FXCollections.observableArrayList(doctorList);

        // Create the TableView
        TableView<Doctor> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style the table
        tableView.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Create columns for Patient Table
        TableColumn<Doctor, String> userIdColumn = new TableColumn<>("Doctor ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Doctor, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Doctor, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Doctor, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Doctor, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Doctor, String> specialityColumn = new TableColumn<>("Speciality");
        specialityColumn.setCellValueFactory(new PropertyValueFactory<>("speciality"));

        // Add the columns to the table
        tableView.getColumns().addAll(userIdColumn, nameColumn, genderColumn, phoneColumn, emailColumn,
                specialityColumn);

        // Set the data to the table
        tableView.setItems(doctors);

        // Set a placeholder for the table
        Label placeholder = new Label("No doctors found for " + user.getName() + ".");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        tableView.setPlaceholder(placeholder);

        // Add the table to a layout (VBox in this case)
        return new VBox(20, tableView);
    }

    public VBox createEmailPage() {
        // Main container
        VBox container = new VBox(30);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_LEFT);

        Text title = new Text("Send Email Notification");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setFill(Color.BLUE);

        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setMaxWidth(500);
        form.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        // Input fields
        TextField senderEmailField = new TextField();
        senderEmailField.setPromptText("Sender Email");
        senderEmailField.setPrefHeight(40);

        PasswordField senderPasswordField = new PasswordField();
        senderPasswordField.setPromptText("App Password");
        senderPasswordField.setPrefHeight(40);

        TextField recipientField = new TextField();
        recipientField.setPromptText("Recipient Email");
        recipientField.setPrefHeight(40);

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        subjectField.setPrefHeight(40); // New field for subject

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message...");
        messageArea.setPrefHeight(150);

        Button sendBtn = new Button("Send Email");
        sendBtn.setPrefHeight(40);
        sendBtn.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");

        sendBtn.setOnAction(e -> {
            String senderEmail = senderEmailField.getText().trim();
            String senderPassword = senderPasswordField.getText().trim();
            String recipientEmail = recipientField.getText().trim();
            String subject = subjectField.getText().trim(); // Get the subject
            String message = messageArea.getText().trim();

            if (senderEmail.isEmpty() || senderPassword.isEmpty() || recipientEmail.isEmpty() || message.isEmpty()
                    || subject.isEmpty()) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all fields.");
                return;
            }

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                Message email = new MimeMessage(session);
                email.setFrom(new InternetAddress(senderEmail));
                email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                email.setSubject(subject); // Set the subject of the email
                email.setText(message);
                Transport.send(email);

                AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Success", "Email sent to: " + recipientEmail);

            } catch (AuthenticationFailedException ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Authentication Failed",
                        "It looks like your email or password is incorrect.\n\n"
                                + "If you're using Gmail, you must enable 2-Step Verification and generate an App Password.\n\n"
                                + "Steps:\n"
                                + "1. Go to your Google Account > Security\n"
                                + "2. Turn on 2-Step Verification\n"
                                + "3. Under 'Signing in to Google', choose 'App Passwords'\n"
                                + "4. Generate a password and paste it here instead of your regular password.");
            } catch (MessagingException ex) {
                ex.printStackTrace();
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Error", "Failed to send email. Please try again.");
            }
        });

        form.getChildren().addAll(senderEmailField, senderPasswordField, recipientField, subjectField, messageArea,
                sendBtn); // Add subjectField to form
        container.getChildren().addAll(title, form);

        return container;
    }

    private VBox createVitalsUploadPage() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Upload Vitals CSV");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Button uploadBtn = new Button("Select and Upload CSV");
        uploadBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        uploadBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                try {
                    parseAndStoreVitalsCSV(file);
                    AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Success", "Vitals uploaded successfully.");
                } catch (Exception ex) {
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Error",
                            "Failed to upload vitals: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        container.getChildren().addAll(title, uploadBtn);
        return container;
    }

    private void parseAndStoreVitalsCSV(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
                Connection conn = DatabaseConnection.getConnection()) {

            String line;
            String sql = "INSERT INTO vitalsign_history (patient_id, recorded_at, heart_rate, oxygen_level, blood_pressure, temperature) "
                    +
                    "VALUES (?, NOW(), ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4)
                    continue; // heartRate, oxygenLevel, bloodPressure, temperature

                stmt.setString(1, user.getUserID()); // patient_id from logged-in user
                stmt.setInt(2, Integer.parseInt(fields[0])); // heart_rate
                stmt.setInt(3, Integer.parseInt(fields[1])); // oxygen_level
                stmt.setString(4, fields[2]); // blood_pressure
                stmt.setDouble(5, Double.parseDouble(fields[3])); // temperature

                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private VBox createVitalsViewPage() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        Label title = new Label("Patient Vitals");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Patient selection dropdown
        ComboBox<Patient> patientSelector = new ComboBox<>();
        patientSelector.setPromptText("Select a patient");

        // Load patients under this doctor
        List<Patient> patients = DataFetcher.getAllPatientsForDoctor(user.getUserID());
        patientSelector.getItems().addAll(patients);

        patientSelector.setCellFactory(param -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.getName());
            }
        });

        patientSelector.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.getName());
            }
        });

        // Table for vitals
        TableView<Vitals> vitalsTable = new TableView<>();
        vitalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Vitals, String> timeCol = new TableColumn<>("Recorded At");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));

        TableColumn<Vitals, Integer> heartRateCol = new TableColumn<>("Heart Rate");
        heartRateCol.setCellValueFactory(new PropertyValueFactory<>("heartRate"));

        TableColumn<Vitals, Integer> oxygenCol = new TableColumn<>("Oxygen Level");
        oxygenCol.setCellValueFactory(new PropertyValueFactory<>("oxygenLevel"));

        TableColumn<Vitals, String> bpCol = new TableColumn<>("Blood Pressure");
        bpCol.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));

        TableColumn<Vitals, Double> tempCol = new TableColumn<>("Temperature");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        vitalsTable.getColumns().addAll(timeCol, heartRateCol, oxygenCol, bpCol, tempCol);

        patientSelector.setOnAction(e -> {
            Patient selected = patientSelector.getValue();
            if (selected != null) {
                ObservableList<Vitals> vitals = FXCollections
                        .observableArrayList(fetchVitalsForPatient(selected.getUserID()));
                vitalsTable.setItems(vitals);
            }
        });

        container.getChildren().addAll(title, patientSelector, vitalsTable);
        return container;
    }

    private List<Vitals> fetchVitalsForPatient(String patientID) {
        List<Vitals> vitalsList = new ArrayList<>();
        String query = "SELECT recorded_at, heart_rate, oxygen_level, blood_pressure, temperature FROM vitalsign_history WHERE patient_id = ? ORDER BY recorded_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patientID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vitalsList.add(new Vitals(
                            rs.getTimestamp("recorded_at").toString(),
                            rs.getInt("heart_rate"),
                            rs.getInt("oxygen_level"),
                            rs.getString("blood_pressure"),
                            rs.getDouble("temperature")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vitalsList;
    }

    public VBox createEvaluationPage(String doctorID) {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-padding: 20; -fx-alignment: top-left;");

        Text heading = new Text("Patient Evaluation");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setFill(Color.BLUE);

        ComboBox<String> patientDropdown = new ComboBox<>();
        patientDropdown.setPromptText("Select a patient");

        // Fetch patient data associated with the specific doctor
        ArrayList<Patient> patientsList = DataFetcher.getAllPatientsForDoctor(doctorID);
        for (Patient patient : patientsList) {
            patientDropdown.getItems().add(patient.getUserID() + " - " + patient.getName());
        }

        VBox detailsContainer = new VBox(20); // Box to hold patient details dynamically
        detailsContainer.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");
        detailsContainer.setPrefWidth(800); // Optional: Adjust the width to match `renderPatientContent`

        // Event to display selected patient information
        patientDropdown.setOnAction(e -> {
            String selectedValue = patientDropdown.getSelectionModel().getSelectedItem();

            if (selectedValue != null) {
                String patientId = selectedValue.split(" - ")[0];

                try {
                    Patient patient = DataFetcher.getPatientData("patient", patientId);

                    if (patient != null) {
                        // Clear previous details
                        detailsContainer.getChildren().clear();

                        // Add sections for patient information (Left & Right Infos)
                        VBox leftInfo = new VBox(10,
                                createInfoRow("User ID:", patient.getUserID()),
                                createInfoRow("Name:", patient.getName()),
                                createInfoRow("Date of Birth:", String.valueOf(patient.getDateOfBirth())),
                                createInfoRow("Gender:", patient.getGender()));
                        leftInfo.setStyle("-fx-padding: 10;");

                        VBox rightInfo = new VBox(10,
                                createInfoRow("Email:", patient.getEmail()),
                                createInfoRow("Phone:", patient.getPhone()),
                                createInfoRow("Address:", patient.getAddress()),
                                createInfoRow("Is Admitted:", patient.isAdmit() ? "Yes" : "No"));
                        rightInfo.setStyle("-fx-padding: 10;");

                        // Combine left and right sections
                        HBox infoSection = new HBox(40, leftInfo, rightInfo);
                        infoSection.setAlignment(Pos.TOP_LEFT);

                        // Add all sections to the container
                        detailsContainer.getChildren().addAll(infoSection);
                    } else {
                        detailsContainer.getChildren().clear();
                        detailsContainer.getChildren().add(new Label("No details available."));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    detailsContainer.getChildren().clear();
                    detailsContainer.getChildren().add(new Label("Error fetching patient data."));
                }
            }
        });

        mainLayout.getChildren().addAll(heading, patientDropdown, detailsContainer);
        return mainLayout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}