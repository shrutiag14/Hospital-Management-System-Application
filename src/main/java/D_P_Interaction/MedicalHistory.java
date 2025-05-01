package D_P_Interaction;
import User.Patient;

import java.util.ArrayList;

public class MedicalHistory {
    private ArrayList<String> consultations;
    private ArrayList<Prescription> prescriptions;

    // Constructor
    public MedicalHistory() {
        this.consultations = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
    }

    // Method to add a consultation record
    public void addConsultation(String consultation) {
        consultations.add(consultation);
    }

    // Method to add a prescription record
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }

    // Method to display medical history
    public void displayMedicalHistory() {
        // Display consultations
        System.out.println("\nConsultation Records:");
        for (String consultation : consultations) {
            System.out.println("- " + consultation);
        }

        // Display prescriptions
        System.out.println("\nPrescription Records:");
        for (Prescription prescription : prescriptions) {
            prescription.displayPrescription();
        }
    }

    @Override
    public String toString() {
        return consultations + "\n" + prescriptions;
    }
}

