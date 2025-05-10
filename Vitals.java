package HealthData;

public class Vitals {
    private String recordedAt;
    private int heartRate;
    private int oxygenLevel;
    private String bloodPressure;
    private double temperature;

    public Vitals(String recordedAt, int heartRate, int oxygenLevel, String bloodPressure, double temperature) {
        this.recordedAt = recordedAt;
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getOxygenLevel() {
        return oxygenLevel;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public double getTemperature() {
        return temperature;
    }
}
