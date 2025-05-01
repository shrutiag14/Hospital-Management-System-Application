package Alert;
import Appointment.Appointment;
import Notifications.EmailNotification;
import Notifications.Notifiable;
import User.*;

public class NotificationService {
    private Patient patient;
    private EmailNotification emailNotification;

    public NotificationService(Patient patient, EmailNotification emailNotification) {
        this.patient = patient;
        this.emailNotification = emailNotification;
    }

    public void sendEmailAlert(Doctor doctor) {
        String message = "URGENT: Emergency Alert\n\n"
                + "Dear Dr. " + doctor.getName() + ",\n\n"
                + "An emergency has been reported for the following patient:\n\n"
                + "Patient Name: " + patient.getName() + "\n"
                + "Age: " + patient.getAge() + "\n"
                + "Contact: " + patient.getEmail() + "\n"
                + "Condition: Critical (Emergency)\n\n"
                + "Please respond immediately.\n\n"
                + "Regards,\n"
                + "Hospital Emergency Response Team";

        emailNotification.sendNotification(message, doctor.getEmail());
    }

}
