package D_P_Interaction;
// importing required classes
import User.Patient;
import User.Doctor;

import java.util.ArrayList;

public class Prescription {
    // private class data members
    private Patient patient;
    private Doctor doctor;
    private ArrayList<String> medications;
    private ArrayList<String> dosages;
    private ArrayList<String> schedule;

    // Constructor
    public Prescription(Patient patient, Doctor doctor) {
        this.patient = patient;
        this.doctor = doctor;
        this.medications = new ArrayList<>();
        this.dosages = new ArrayList<>();
        this.schedule = new ArrayList<>();
    }

    // Method to add a medication
    public void addMedication(String medication, String dosage, String schedule) {
        this.medications.add(medication);
        this.dosages.add(dosage);
        this.schedule.add(schedule);
    }

    // Getters
    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public ArrayList<String> getMedications() {
        return medications;
    }

    public ArrayList<String> getDosages() {
        return dosages;
    }

    public ArrayList<String> getSchedule() {
        return schedule;
    }

    // Method to display prescription details
    public void displayPrescription() {
        for (int i = 0; i < medications.size(); i++) {
            System.out.println("- " + medications.get(i) + " | Dosage: " + dosages.get(i) + " | Schedule: " + schedule.get(i));
        }
    }
}

