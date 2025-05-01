package User;

// importing all the mandatory Classes
import Appointment.Appointment;
import Appointment.AppointmentManager;
import D_P_Interaction.Feedback;
import Exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.ArrayList;


public class Doctor extends User{
    private static final String PMDC_PATTERN = "^[a-zA-Z]{2,4}-[0-9]{4,5}"; // According to what Google said to me, I am not accountable if is not the official
    // declaring all the attributes as private
    private final String doctorID = User.randomIdGenerator();
    private String PMDC_NO;
    private String availabilityHours;
    private double salary;
    private String qualification;
    private String speciality;
    private int yearsOfExperience;
    private ArrayList<Patient> patients = new ArrayList<>();
    private final AppointmentManager appointmentManager;

    // defining all the constructors
    public Doctor( AppointmentManager manager, String name, LocalDate dob, String gender, String address, String phone, String email, String password, String PMDC_NO, String availabilityHours, double salary, String qualification, String speciality, int experience)
            throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException{
        super(name, dob, gender, address, phone, email, password );
        setPMDC_NO(PMDC_NO);
        setAvailabilityHours(availabilityHours);
        setSalary(salary);
        setQualification(qualification);
        setSpeciality(speciality);
        setExperience(experience);
        this.appointmentManager = manager;
    }

    // a very demure copy constructor
    public Doctor(Doctor doctor) throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException{
        super(doctor.getName(), doctor.getDateOfBirth(), doctor.getGender(), doctor.getAddress(), doctor.getPhone(), doctor.getEmail(), doctor.getPassword());
        this.appointmentManager = doctor.appointmentManager;
        setPMDC_NO(doctor.getPMDC_NO());
        setAvailabilityHours(doctor.getAvailabilityHours());
        setSalary(doctor.getSalary());
        setQualification(doctor.getQualification());
        setSpeciality(doctor.getSpeciality());
        setExperience(doctor.getExperience());
        this.patients = new ArrayList<Patient>(doctor.patients); // creating the new arraylist so that the copy object doesnt affect the original object
    }

    // defining the setters with validations
    public void setPMDC_NO(String PMDC_NO) {
        if(!isValidPMDC(PMDC_NO)) {
            System.out.println("Invalid PMDC");
            this.PMDC_NO = null;
            return;
        }
        this.PMDC_NO = PMDC_NO;
    }

    public void setAvailabilityHours(String availabilityHours) {
        this.availabilityHours = availabilityHours;
    }

    public void setSalary(double salary) {
        if(salary < 0.0) {
            System.out.println("Invalid salary");
            this.salary = 0.0;
            return;
        }
        this.salary = salary;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setExperience(int experience) {
        if(experience < 0) {
            System.out.println("Invalid experience");
            this.yearsOfExperience = 0;
            return;
        }
        this.yearsOfExperience = experience;
    }

    // defining all the getters
    public String getDoctorID() {
        return doctorID;
    }

    public String getPMDC_NO() {
        return PMDC_NO;
    }

    public String getAvailabilityHours() {
        return availabilityHours;
    }

    public double getSalary() {
        return salary;
    }

    public String getQualification() {
        return qualification;
    }

    public String getSpeciality() {
        return speciality;
    }

    public int getExperience() {
        return yearsOfExperience;
    }

    // validation methods
    public static boolean isValidPMDC(String PMDC_NO) {
        return Pattern.matches(PMDC_PATTERN, PMDC_NO);
    }

    // defining class methods
    public void diagnosePatient(Patient patient, String diagnose) {
        if(!patients.contains(patient)) { // updating the doctor's independent patient list to keep the record
            patients.add(patient);
        }
        patient.addDiagnosis(diagnose);
    }

    public double calculateConsultation() {
        if (yearsOfExperience < 5) {
            return 50;
        } else if (yearsOfExperience <= 10) {
            return 100;
        } else {
            return 200;
        }
    }

    public void addFeedback(Patient patient, Feedback feedback) {
        if(!patients.contains(patient)) {
            patients.add(patient);
        }
        patient.addFeedback(feedback);
    }

    public String getPatientDetails(Patient patient) { // displaying only necessary patient details to doctor
        return String.format("Name: %s\nAge: %s\nGender: %s\nDiagnosis: %s\n", patient.getName(), patient.getAge(), patient.getGender(), patient.getDiagnosis());
    }

    // consistent appointment scheduler and cancel methods
    @Override
    public void scheduleAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime) {
        try {
            // Parameter checks
            if (patient == null) {
                throw new InvalidAppointmentException("Patient cannot be null.");
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
            appointmentManager.requestAppointment(dateTime, this, patient);
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
        return String.format("\nDetails:\nName: %s\tAge: %ss\tGender: %s\tAddress: %s\tPhone: %s\tEmail: %s\tPassword: %s\nProfessional Details:\nDoctor ID: %s\tPMDC No: %s\tAvailability Hours: %s\tSalary: %.3f\tQualification: %s\tSpeciality: %s\tExperience: %d years\t",getName(), getAge(), getGender(), getAddress(), getPhone(), getEmail(), getPassword(), doctorID, PMDC_NO, availabilityHours, salary, qualification, speciality, yearsOfExperience);
    }
}

