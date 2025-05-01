package HealthData;

public class VitalSign {
    private int heartRate;
    private int oxygenLevel;
    private String bloodPressure;
    private double temperature;

    // Constructor to initialize the vital signs
    public VitalSign(int heartRate, int oxygenLevel, String bloodPressure, double temperature) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
    }

    // Getters and setters for each vital sign
    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getOxygenLevel() {
        return oxygenLevel;
    }

    public void setOxygenLevel(int oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "Heart Rate: " + heartRate + " bpm\nOxygen Level: " + oxygenLevel + "\nBlood Pressure: " + bloodPressure + "\nTemperature: " + temperature + "°C";
    }
}
