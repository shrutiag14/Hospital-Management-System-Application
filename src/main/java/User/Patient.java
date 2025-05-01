package User;

// importing all the required Classes
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import Appointment.Appointment;
import Appointment.AppointmentManager;
import Exceptions.*;
import HealthData.VitalDatabase;
import D_P_Interaction.Feedback;
import D_P_Interaction.MedicalDatabase;

public class Patient extends User {
    // defining all the private attributes for security
    private final String patientID = User.randomIdGenerator();
    private ArrayList<LocalDate> checkupHistory;
    private boolean isAdmit;
    private double pendingFee;
    private ArrayList<String> diagnosis;
    private ArrayList<Feedback> feedbacks;
    private final AppointmentManager appointmentManager;

    // defining the constructor
    public Patient( AppointmentManager manager, String name, LocalDate dob, String gender, String address, String phone, String email, String password, ArrayList<LocalDate> checkupHistory, boolean isAdmit, double pendingFee, ArrayList<String> diagnosis, ArrayList<Feedback> feedback)
            throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException  {
        super( name, dob, gender, address, phone, email, password);
        this.checkupHistory = checkupHistory;
        this.isAdmit = isAdmit;
        this.pendingFee = pendingFee;
        this.diagnosis = diagnosis;
        this.feedbacks = feedback;
        this.appointmentManager = manager;
    }

    // copy constructor
    public Patient(Patient patient) throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException {
        super(patient.getName(), patient.getDateOfBirth(), patient.getGender(), patient.getAddress(), patient.getPhone(), patient.getEmail(), patient.getPassword());
        this.isAdmit = patient.isAdmit;
        this.pendingFee = patient.pendingFee;
        this.diagnosis.addAll(patient.diagnosis);
        this.feedbacks = new ArrayList<>(patient.feedbacks); // creating the new arraylist so that the copy object doesnt affect the original object
        this.appointmentManager = patient.appointmentManager;
        this.checkupHistory = new ArrayList<>(patient.checkupHistory); // creating the new arraylist so that the copy object doesnt affect the original object
    }

    // getters
    public String getPatientID() {
        return patientID;
    }

    public boolean isAdmit() {
        return isAdmit;
    }

    public ArrayList<LocalDate> getCheckupHistory() {
        return checkupHistory;
    }

    public ArrayList<String> getDiagnosis() {
        return diagnosis;
    }

    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public double getPendingFee() {
        return pendingFee;
    }

    public LocalDate getLastVisit() {
        return checkupHistory.getLast();
    }

    // setters
    public void setAdmit(boolean admit) {
        this.isAdmit = admit;
    }

    public void addCheckup(LocalDate checkup) {
        this.checkupHistory.add(checkup);
    }

    public void addDiagnosis(String diagnosis) {
        this.diagnosis.add(diagnosis);
    }

    public void addFeedback(Feedback feedback) {
        this.feedbacks.add(feedback);
    }

    public void setPendingFee(double pendingFee) {
        if(pendingFee < 0) {
            System.out.println("Invalid amount.");
            pendingFee = -1; // -1 to indicate that the fee was never initialized, also to differ it from 0 which will
            // mean that the fee has been paid completely
        }
        this.pendingFee = pendingFee;
    }

    // patient methods
    public void payFee(double payAmount) {
        if (payAmount < 0) {
            System.out.println("Invalid amount.");
            payAmount = 0;
        }
        pendingFee -= payAmount;
        System.out.println("Payment successful. Remaining Amount: " + payAmount);
    }

    public void generateReport() {
        VitalDatabase.displayPatientVitals(this); // accessing the central database for vitals for displaying
    }

    public void generateMedicalReport() {
        MedicalDatabase.displayPatientMedicalHistory(this); // accessing the central medical database
    }

    // consistent appointment methods for users
    @Override
    public void scheduleAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime) {
        try {
            // Parameter checks
            if (doctor == null) {
                throw new InvalidAppointmentException("Doctor cannot be null.");
            }
            if (dateTime == null) {
                throw new InvalidAppointmentException("Appointment date and time cannot be null.");
            }
            if (dateTime.isBefore(LocalDateTime.now())) {
                throw new InvalidAppointmentException("Appointment date and time cannot be in the past.");
            }

            if(appointmentManager.isDuplicateAppointment(dateTime, doctor, patient)) {
                throw new DuplicateAppointmentException("Duplicate appointment exists.");
            }

            // Request a new appointment
            appointmentManager.requestAppointment(dateTime, doctor, this);
        } catch (InvalidAppointmentException | DuplicateAppointmentException e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public void cancelAppointment(Appointment appointment) {
        try {
            // Parameter check
            if (appointment == null) {
                throw new InvalidAppointmentException("Appointment cannot be null.");
            }

            if(!appointmentManager.contains(appointment)) {
                throw new AppointmentNotFoundException("Appointment not found in the system.");
            }

            // Cancel the specified appointment
            appointmentManager.cancelAppointment(appointment);
        } catch (InvalidAppointmentException | AppointmentNotFoundException e) {
            System.out.println("Error canceling appointment: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void rescheduleAppointment(Appointment appointment, LocalDateTime newDateTime)
            throws InvalidAppointmentException, AppointmentNotFoundException, DuplicateAppointmentException {
        if (appointment == null || newDateTime == null) {
            throw new InvalidAppointmentException("Cannot reschedule. Appointment or new date & time cannot be null.");
        }

        if (!appointmentManager.contains(appointment)) {
            throw new AppointmentNotFoundException("Cannot reschedule. Appointment not found in the system.");
        }

        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Cannot reschedule. The new appointment date & time cannot be in the past.");
        }

        if (appointmentManager.isDuplicateAppointment(newDateTime, appointment.getDoctor(), appointment.getPatient())) {
            throw new DuplicateAppointmentException("Cannot reschedule. A similar appointment already exists at the chosen time.");
        }

        appointment.updateDateTime(newDateTime);
        System.out.println("Appointment rescheduled successfully to: " + newDateTime);
    }

    @Override
    public String toString() {
        return String.format("\\nDetails:Name: %s\\tAge: %s\\tGender: %s\\tAddress:" +
                " %s\\tPhone: %s\\tEmail: %s\\tPassword: %s\\Patient Details:\\Patient ID: %s\\tCheckup" +
                " History: %s\\tAdmit: %b\\tPending Fee:  %f\\tDiagnosis:" +
                " %s\\t", getName(), getAge(), getGender(), getAddress(), getPhone(), getEmail(),
                getPassword(), patientID, checkupHistory, isAdmit, pendingFee, diagnosis);
    }
}

