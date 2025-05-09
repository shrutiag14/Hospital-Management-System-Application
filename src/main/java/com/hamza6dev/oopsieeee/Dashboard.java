package com.hamza6dev.oopsieeee;
import Appointment.*;
import Appointment.Appointment.AppointmentStatus;

import Exceptions.DuplicateAppointmentException;
import Exceptions.InvalidAppointmentException;
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
import javafx.stage.Stage;
import User.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


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
        searchField.setPromptText("Search by name, ID, or date...");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // In a real application, implement a filter logic to search appointments
            // For instance, filter the table based on the input text
            System.out.println("Searching: " + newValue);
        });

        List<Appointment> appointments = getAppointments();
        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList(appointments);
        table.setItems(appointmentData);


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
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to fetch doctors from database.");
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
                    statusLabel
            );

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
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Update Error", "Please select an appointment to update.");
                return;
            }

            // Open the Update Appointment Dialog
            openUpdateAppointmentDialog(selectedAppointment, table);
        });

        Button deleteAppointmentBtn = new Button("Delete Appointment");
        deleteAppointmentBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteAppointmentBtn.setPrefHeight(40);
        deleteAppointmentBtn.setOnAction(event -> {
            // Get the selected appointment from the table
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Delete Error", "Please select an appointment to delete.");
                return;
            }

            // Prompt user for confirmation before deletion
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Confirmation");
            confirmationAlert.setHeaderText("Are you sure you want to delete this appointment?");
            confirmationAlert.setContentText(selectedAppointment.toString());

            // Wait for user's response (OK or Cancel)
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        // Prepare the DELETE query
                        String deleteQuery = "DELETE FROM appointment WHERE appointment_id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                            // Set the appointment ID parameter
                            stmt.setString(1, selectedAppointment.getAppointmentID());

                            // Execute the query
                            int rowsAffected = stmt.executeUpdate();

                            if (rowsAffected > 0) {
                                // Refresh the TableView by fetching updated appointments
                                List<Appointment> updatedAppointments = getAppointments();
                                ObservableList<Appointment> updatedData = FXCollections.observableArrayList(updatedAppointments);
                                table.setItems(updatedData);

                                // Display success message
                                AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Appointment deleted successfully!");
                            } else {
                                throw new SQLException("Appointment not found in the database.");
                            }
                        }
                    } catch (SQLException ex) {
                        // Handle database-related errors
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Delete Error", "An error occurred while deleting the appointment: " + ex.getMessage());
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        // Handle other errors
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Delete Error", "An unexpected error occurred: " + ex.getMessage());
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
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "View Error", "Please select an appointment to view.");
                return;
            }
            // Display the appointment details in a pop-up dialog
            showAppointmentDetailsPopup(selectedAppointment);
        });

        // Button Layout
        HBox buttonLayout = new HBox(10, addAppointmentBtn, updateAppointmentBtn, deleteAppointmentBtn, viewDetailsBtn);
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

        patientsBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(new Label("Patients"));
        });

        appointmentsBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().addAll(appointmentsLayout);
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
        availabilityGrid.add(new Label(String.valueOf(doc.getStartTime())), 1, 1);

        availabilityGrid.add(new Label("End Time:"), 0, 2);
        availabilityGrid.add(new Label(String.valueOf(doc.getEndTime())), 1, 2);

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
                createInfoRow("Consultation Fee :", String.valueOf(doc.getConsulationFee())),
                createInfoRow("Experience Years :", (doc.getExperience() + " years"))
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
        HBox doctorRow = createDetailRow("Doctor:", appointment.getDoctor() != null ? appointment.getDoctor().getName() : "N/A");
        HBox patientRow = createDetailRow("Patient:", appointment.getPatient() != null ? appointment.getPatient().getName() : "N/A");
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
        ComboBox<Appointment.AppointmentStatus> statusComboBox = new ComboBox<>();

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
            AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to fetch doctors from database.");
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

        // Populate Status
        statusComboBox.getItems().addAll(Appointment.AppointmentStatus.values());
        statusComboBox.setValue(appointment.getStatus());

        // Add Input Fields to Dialog
        dialogContainer.getChildren().addAll(
                titleLabel,
                createUpdateRow("New Date", datePicker),
                createUpdateRow("New Time", timeComboBox),
                createUpdateRow("New Doctor", doctorComboBox),
                createUpdateRow("New Status", statusComboBox)
        );


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
            Appointment.AppointmentStatus status = statusComboBox.getValue();

            if (date == null || time == null || doctor == null || status == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
                return;
            }

            LocalDateTime newDateTime = LocalDateTime.of(date, time);
            if (newDateTime.isBefore(LocalDateTime.now())) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error", "The selected date and time must be in the future.");
                return;
            }

            try {
                // Check for Duplicate Appointments
                AppointmentManager appointmentManager = new AppointmentManager();
                if (appointmentManager.isDuplicateAppointment(newDateTime, doctor, appointment.getPatient())) {
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Duplicate Error", "An appointment already exists at this date and time for the selected doctor.");
                    return;
                }

                // Update Appointment in Database
                updateAppointmentInDatabase(appointment.getAppointmentID(), newDateTime, doctor, status);

                // Refresh the TableView
                table.setItems(FXCollections.observableArrayList(getAppointments()));

                // Show Success Notification
                AlertDialogueBox.showAlert(Alert.AlertType.CONFIRMATION, "Update Successful", "The appointment has been successfully updated.");
                updateDialog.close();
            } catch (SQLException ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the appointment: " + ex.getMessage());
            }
        });

        // Set Dialog Content and Show Dialog
        updateDialog.getDialogPane().setContent(dialogContainer);
        updateDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); // Disable default buttons
        updateDialog.showAndWait();
    }

    private void updateAppointmentInDatabase(String appointmentId, LocalDateTime newDateTime, Doctor newDoctor, Appointment.AppointmentStatus newStatus) throws SQLException {
        // SQL Query to Update Appointment in the Database
        String updateQuery = "UPDATE appointment " +
                "SET date_time = ?, doctor_id = ?, status = ? " +
                "WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            // Set Parameters
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(newDateTime)); // New Date and Time
            stmt.setString(2, newDoctor.getUserID());                     // New Doctor ID
            stmt.setString(3, newStatus.name());                          // New Status
            stmt.setString(4, appointmentId);                             // Appointment ID

            // Execute the Update Query
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update appointment. Appointment ID not found.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
