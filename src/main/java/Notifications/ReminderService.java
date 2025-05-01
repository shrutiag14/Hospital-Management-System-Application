package Notifications;
import Appointment.Appointment;
import D_P_Interaction.Prescription;

public class ReminderService {

    private final Notifiable emailNotification;

    // Constructor injection for flexibility
    public ReminderService(Notifiable emailNotification) {
        this.emailNotification = emailNotification;
    }

    // Send appointment reminder
    public void sendAppointmentReminder(Appointment appointment) {
        String message = "📅 Appointment Reminder:\n" +
                "You have an appointment with Dr. " + appointment.getDoctor().getName() + " on " + appointment.getDateTime() + " at " + "3:00 PM" + ".\n" +
                "Please be on time.";
        emailNotification.sendNotification(message, appointment.getPatient().getEmail());
    }

    // Send medication reminder
    public void sendMedicationReminder(Prescription prescription) {
        String message = "💊 Medication Reminder:\n" +
                "Please take your medication: " + prescription.getMedications() + " at " + "3:00PM" + ".\n" +
                "Stay healthy!";
        emailNotification.sendNotification(message, prescription.getPatient().getEmail());
    }

}
