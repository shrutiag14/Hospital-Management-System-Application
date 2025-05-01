package Appointment;

import User.Doctor;
import User.Patient;
import User.User;
import Exceptions.*;

import java.time.LocalDateTime;

// a central class for Appointment storage
public class Appointment {
    // enum for status
    public enum AppointmentStatus {
        PENDING,   // Waiting for approval
        APPROVED,  // Approved by admin or doctor
        CANCELED,  // Canceled by patient or admin
        COMPLETED, // Successfully completed appointment
        NO_SHOW,   // Patient didn't attend the appointment
        REJECTED   // Rejected by the doctor/admin
    }

    // private class attributes
    private final String appointmentID = User.randomIdGenerator();
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
        this.dateTime = dateTime;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
    }


    // getters
    public String getAppointmentID() {
        return appointmentID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    // update status
    public void updateStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void updateDateTime(LocalDateTime newDateTime) {
        this.dateTime = newDateTime;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID + ", Appointment Date and Time: " + dateTime + ", Doctor: " + doctor.getName() + ", Patient: " + patient.getName() + ", Status: " + status;
    }
}
