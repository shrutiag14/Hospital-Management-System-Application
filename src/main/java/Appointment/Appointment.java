package Appointment;

import User.Doctor;
import User.Patient;
import User.User;
import Exceptions.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// A central class for Appointment storage
public class Appointment {
    // Enum for status
    public enum AppointmentStatus {
        PENDING,   // Waiting for approval
        APPROVED,  // Approved by admin or doctor
        CANCELED,  // Canceled by patient or admin
        COMPLETED, // Successfully completed appointment
        NO_SHOW,   // Patient didn't attend the appointment
        REJECTED   // Rejected by the doctor/admin
    }

    // Private class attributes
    private String appointmentID;
    private LocalDateTime dateTime; // Date and time of the appointment
    private final Doctor doctor;
    private final Patient patient;
    private AppointmentStatus status;

    // Constructor
    public Appointment(LocalDateTime dateTime, Doctor doctor, Patient patient, AppointmentStatus status) throws InvalidAppointmentException {
        if (dateTime == null) {
            throw new InvalidAppointmentException("Appointment date and time cannot be null.");
        }
        if (doctor == null) {
            throw new InvalidAppointmentException("Doctor cannot be null.");
        }
        if (patient == null) {
            throw new InvalidAppointmentException("Patient cannot be null.");
        }
        this.appointmentID = User.randomIdGenerator();
        this.dateTime = dateTime;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
    }

    public Appointment(String appointmentId, LocalDateTime dateTime, Doctor doctor, Patient patient, AppointmentStatus status) throws InvalidAppointmentException {
        if (dateTime == null) {
            throw new InvalidAppointmentException("Appointment date and time cannot be null.");
        }
        if (doctor == null) {
            throw new InvalidAppointmentException("Doctor cannot be null.");
        }
        if (patient == null) {
            throw new InvalidAppointmentException("Patient cannot be null.");
        }
        this.appointmentID = appointmentId;
        this.dateTime = dateTime;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
    }

    // Getters
    public String getAppointmentID() {
        return appointmentID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDoctorName() {
        return doctor.getName();
    }

    public String getPatientName() {
        return patient.getName();
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public String getFormattedDate() {
        // Optional: Return a formatted date string (e.g., "Nov 10, 2023")
        return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public String getTime() {
        // Format the time portion of the LocalDateTime
        return dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    // Update status
    public void updateStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void updateDateTime(LocalDateTime newDateTime) {
        this.dateTime = newDateTime;
    }

    @Override
    public boolean equals(Object obj) {
        // Check if the object is the same instance
        if (this == obj) return true;

        // Check if the object is null or they don't belong to the same class
        if (obj == null || getClass() != obj.getClass()) return false;

        // Cast to Appointment and compare all fields
        Appointment other = (Appointment) obj;

        return appointmentID.equals(other.appointmentID) && // Compare appointment ID
                dateTime.equals(other.dateTime) && // Compare dateTime
                doctor.equals(other.doctor) && // Compare doctor object
                patient.equals(other.patient) && // Compare patient object
                status == other.status; // Compare status enum
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID + ", Appointment Date and Time: " + dateTime + ", Doctor: " + doctor.getName() + ", Patient: " + patient.getName() + ", Status: " + status;
    }
}