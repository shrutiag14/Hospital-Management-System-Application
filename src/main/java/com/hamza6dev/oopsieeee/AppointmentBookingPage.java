package com.hamza6dev.oopsieeee;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import User.Doctor;
import User.Patient;
import User.Session;
import Appointment.AppointmentManager;
import Exceptions.DuplicateAppointmentException;
import Exceptions.InvalidAppointmentException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.IntStream;

public class AppointmentBookingPage {
    private final AppointmentManager appointmentManager = new AppointmentManager();

    public void start(Stage stage) {
        Patient patient = Session.getLoggedInPatient();

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #f8f8f8;");

        // Title
        Text heading1 = new Text("Book an Appointment");
        heading1.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heading1.setFill(Color.BLUE); // ← your requested color
        heading1.setTextAlignment(TextAlignment.LEFT);
        VBox.setMargin(heading1, new Insets(0, 0, 20, 0));

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
        TextField nameFieldAppointment = new TextField(patient.getName());
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

        Doctor d1 = Doctor.createMockDoctor("John billy");
        Doctor d2 = Doctor.createMockDoctor("Emily bbilly");
        Doctor d3 = Doctor.createMockDoctor("Khan billy");

        if (d1 != null) doctorComboBox.getItems().add(d1);
        if (d2 != null) doctorComboBox.getItems().add(d2);
        if (d3 != null) doctorComboBox.getItems().add(d3);

        doctorComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                setText(empty || doctor == null ? "" : doctor.getName());
                setStyle("-fx-font-size: 14px;");
            }
        });

        doctorComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                setText(empty || doctor == null ? "" : doctor.getName());
                setStyle("-fx-font-size: 14px;");
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
                appointmentManager.requestAppointment(dateTime, selectedDoctor, patient);
                statusLabel.setText("Appointment booked successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
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

        Scene scene = new Scene(mainContainer, 1080, 720);
        stage.setScene(scene);
        stage.setTitle("Book Appointment");
        stage.show();
    }
}







