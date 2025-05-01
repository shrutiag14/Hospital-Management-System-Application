package D_P_Interaction;
// importing required classes
import User.Patient;
import java.util.Map;
import java.util.HashMap; // using the hashmap class to uniquely assign each record to a unique patient

public class MedicalDatabase {
    private static Map<Patient, MedicalHistory> medicalDatabase;

    public MedicalDatabase() {
        this.medicalDatabase = new HashMap<>();
    }

    // Add the medical history for a patient
    public static void addPatientMedicalHistory(Patient patient, MedicalHistory medicalHistory) {
        medicalDatabase.put(patient, medicalHistory);
        System.out.println("Medical history for " + patient.getName() + " have been added/updated.");
    }

    // Retrieve the medical history for a specific patient
    public static MedicalHistory getPatientMedicalHistory(Patient patient) {
        return medicalDatabase.get(patient);
    }

    // Display the vital signs for a specific patient
    public static void displayPatientMedicalHistory(Patient patient) {
        MedicalHistory history = medicalDatabase.get(patient);
        if (history != null) {
            System.out.println();
            System.out.println("Displaying History for " + patient.getName() + " (Patient ID: " + patient.getPatientID() + ")");
            history.displayMedicalHistory();
        } else {
            System.out.println("No history found for " + patient.getName());
        }
    }
}
