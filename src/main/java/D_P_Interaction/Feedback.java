package D_P_Interaction;
// importing required classes
import User.Doctor;
import User.Patient;

public class Feedback {
    // private fields
    private Patient patient;
    private Doctor doctor;
    private String feedbackMessage;

    // Constructor
    public Feedback(Patient patient, Doctor doctor, String feedbackMessage) {
        this.patient = patient;
        this.doctor = doctor;
        this.feedbackMessage = feedbackMessage;
    }

    // Getters
    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    // Setters
    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    // Method to display feedback
    public void displayFeedback() {
        System.out.println("Patient ID: " + patient.getPatientID());
        System.out.println("Doctor: " + doctor.getName());
        System.out.println("Feedback: " + feedbackMessage);
    }
}

