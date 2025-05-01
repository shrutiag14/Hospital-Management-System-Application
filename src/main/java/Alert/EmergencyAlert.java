package Alert;

import HealthData.VitalSign;
import Notifications.EmailNotification;
import User.*;
import java.util.ArrayList;

public class EmergencyAlert {
    private VitalSign vital;
    private Patient patient;
    private EmailNotification emailNotification;

    // Constructor that takes a Vital object and an EmailNotification object
    public EmergencyAlert(VitalSign vital, User sender, Patient patient) {
        this.vital = vital;
        this.patient = patient;
    }

    public void checkVitals() {
        ArrayList<String> alertMessage = new ArrayList<>();

        // Check each vital against normal thresholds
        if (vital.getHeartRate() < 60 || vital.getHeartRate() > 100) {
            alertMessage.add("Abnormal Heart Rate: "  + vital.getHeartRate() + " bpm. ");
        }

        if (vital.getOxygenLevel() < 90) {
            alertMessage.add("Low Oxygen Level: " + vital.getOxygenLevel() + "%. ");
        }

        if (!vital.getBloodPressure().equals("120/80")) {
            alertMessage.add("Abnormal Blood Pressure: " + vital.getBloodPressure() + ". ");
        }

        if (vital.getTemperature() > 37.5) {
            alertMessage.add("High Temperature: " + vital.getTemperature() + "°C. ");
        }

        if (alertMessage.size() > 0) {
            System.out.println("Vitals are abnormal.");
            for (String message : alertMessage) {
                System.out.println(message);
            }
        } else {
            System.out.println("Vitals are normal.");
        }
    }
}
