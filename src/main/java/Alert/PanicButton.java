package Alert;


import Notifications.EmailNotification;
import User.*;

import java.util.List;

public class PanicButton {
    private EmailNotification emailNotification;
    private List<Doctor> emergencyDoctors; // Doctors on emergency duty

    public PanicButton(EmailNotification emailNotification, List<Doctor> emergencyDoctors) {
        this.emailNotification = emailNotification;
        this.emergencyDoctors = emergencyDoctors;
    }

    public void triggerEmergency(Patient patient) {
        for (Doctor doctor : emergencyDoctors) {
            sendEmergencyAlert(doctor, patient);
        }
        System.out.println("Emergency alert sent to available doctors.");
    }

    private void sendEmergencyAlert(Doctor doctor, Patient patient) {
        String message = "URGENT: Emergency Alert\n\n"
                + "Dear Dr. " + doctor.getName() + ",\n\n"
                + "An emergency has been reported by the following patient:\n\n"
                + "Patient Name: " + patient.getName() + "\n"
                + "Age: " + patient.getAge() + "\n"
                + "Email: " + patient.getEmail() + "\n"
                + "Condition: Critical (Emergency triggered by patient)\n\n"
                + "Please respond immediately.\n\n"
                + "Regards,\n"
                + "Hospital Emergency Response System";

        emailNotification.sendNotification(message, doctor.getEmail());
    }
}

