package User;

// importing necessary classes
import Appointment.Appointment;
import Appointment.AppointmentManager;
import Exceptions.*;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Admin extends User {
    // private class variables
    private final String adminID = User.randomIdGenerator();
    private static ArrayList<Patient> patients = new ArrayList<>();
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private final AppointmentManager appointmentManager;

    // Constructor
    public Admin(AppointmentManager manager, String name, LocalDate dob, String gender, String address, String phone, String email, String password)
            throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException,
            InvalidEmailException, InvalidPasswordException {
        super(name, dob, gender, address, phone, email, password);
        this.appointmentManager = manager;
    }


    // Copy Constructor
    public Admin(Admin admin) throws InvalidNameException, InvalidDateOfBirthException,
            InvalidGenderException, InvalidEmailException, InvalidPasswordException {
        super(admin.getName(), admin.getDateOfBirth(), admin.getGender(), admin.getAddress(),
                admin.getPhone(), admin.getEmail(), admin.getPassword());
        this.appointmentManager = admin.appointmentManager;
    }


    // Getters
    public String getAdminID() {
        return adminID;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    // Methods for managing doctors
    public void addDoctor(Doctor doctor) {
        if (doctor == null) {
            System.out.println("Doctor cannot be null.");
            return;
        }
        if (doctors.contains(doctor)) {
            System.out.println("Doctor already exists in the system.");
            return;
        }
        doctors.add(doctor);
        System.out.println("Doctor added successfully.");
    }


    public void updateDoctor(String doctorID, Doctor newDetails) {
        if (doctorID == null || newDetails == null) {
            System.out.println("Doctor ID or details cannot be null.");
            return;
        }
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getDoctorID().equals(doctorID)) {
                doctors.set(i, newDetails);
                System.out.println("Doctor updated successfully.");
                return;
            }
        }
        System.out.println("Doctor not found in the system.");
    }

    public void deleteDoctor(String doctorID) {
        boolean found = doctors.removeIf(doctor -> doctor.getDoctorID().equals(doctorID));
        if (found) {
            System.out.println("Doctor deleted successfully.");
        } else {
            System.out.println("Doctor not found.");
        }
    }

    public void viewDoctor(String doctorID) {
        if (doctorID == null) {
            System.out.println("Doctor ID cannot be null.");
            return;
        }
        for (Doctor doctor : doctors) {
            if (doctor.getDoctorID().equals(doctorID)) {
                System.out.println(doctor);
                return;
            }
        }
        System.out.println("Doctor not found.");
    }


    // methods for managing patients
    public void addPatient(Patient patient) {
        if (patient == null) {
            System.out.println("Patient cannot be null.");
            return;
        }
        if (patients.contains(patient)) {
            System.out.println("Patient already exists in the system.");
            return;
        }
        patients.add(patient);
        System.out.println("Patient added successfully.");
    }

    public void updatePatient(String patientID, Patient newDetails) {
        if (patientID == null || newDetails == null) {
            System.out.println("Patient ID or details cannot be null.");
            return;
        }
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientID().equals(patientID)) {
                patients.set(i, newDetails);
                System.out.println("Patient updated successfully.");
                return;
            }
        }
        System.out.println("Patient not found in the system.");
    }

    public void deletePatient(String patientID) {
        boolean found = patients.removeIf(patient -> patient.getPatientID().equals(patientID));
        if (found) {
            System.out.println("Patient deleted successfully.");
        } else {
            System.out.println("Patient not found.");
        }
    }

    public void viewPatient(String patientID) {
        if (patientID == null) {
            System.out.println("Patient ID cannot be null.");
            return;
        }
        for (Patient patient : patients) {
            if (patient.getPatientID().equals(patientID)) {
                System.out.println(patient);
                return;
            }
        }
        System.out.println("Patient not found.");
    }

    // Additional Admin functionalities
    public void generateInvoice(int invoiceID, double amount, int patientID) {
        System.out.println("Invoice ID: " + invoiceID + ", Amount: $" + amount + " generated for patient ID: " + patientID);
    }

    public void sendPhoneReminder(int userID) {
        System.out.println("Phone reminder sent to user ID: " + userID);
    }

    public void sendMailReminder(int userID) {
        System.out.println("Email reminder sent to user ID: " + userID);
    }

    @Override
    public void scheduleAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime) {
        try {
            // Parameter checks
            if (doctor == null) {
                throw new InvalidAppointmentException("Doctor cannot be null.");
            }
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
            appointmentManager.requestAppointment(dateTime, doctor, patient);
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


    public void approveAppointment(Appointment appointment) {
        try {
            // Parameter check
            if (appointment == null) {
                throw new InvalidAppointmentException("Appointment cannot be null.");
            }

            if(!appointmentManager.contains(appointment)) {
                throw new AppointmentNotFoundException("Appointment not found in the system.");
            }

            // Approve the specified appointment
            appointmentManager.approveAppointment(appointment);
        } catch (InvalidAppointmentException | AppointmentNotFoundException e) {
            System.out.println("Error approving appointment: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    //  Generate Reports
    public void generateReport() {
        System.out.println("---- Admin Report ----");
        System.out.println("Total Patients: " + patients.size());
        System.out.println("Total Doctors: " + doctors.size());
        System.out.println("Total Appointments: " + appointmentManager.getAppointments().size());
        System.out.println("----------------------");
    }
}
