package com.hamza6dev.oopsieeee;

import D_P_Interaction.Prescription;
import Exceptions.*;
import User.Doctor;
import User.Patient;
import Appointment.Appointment;
import Appointment.Appointment.AppointmentStatus;
import User.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class DataFetcher {

    public static Doctor getDoctorData(String accountType, String accountID) throws SQLException {
        // Validate accountType to prevent SQL injection
        if (!"doctor".equals(accountType) && !"patient".equals(accountType)) {
            throw new IllegalArgumentException("Invalid account type: " + accountType);
        }

        String query = String.format("SELECT * FROM %s t " +
                "INNER JOIN user u ON u.user_id = t.%s_id " +
                "WHERE t.%s_id = ?;", accountType, accountType, accountType);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountID); // Use PreparedStatement to prevent injection attacks

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setUserID(rs.getString("user_id"));
                    doctor.setName(rs.getString("name"));
                    java.sql.Date sqlDate = rs.getDate("date_of_birth");
                    if (sqlDate != null) {
                        doctor.setDateOfBirth(sqlDate.toLocalDate());
                    }
                    doctor.setGender(rs.getString("gender"));
                    doctor.setAddress(rs.getString("address"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setEmail(rs.getString("email"));
                    doctor.setPassword(rs.getString("password"));
                    doctor.setPMDC_NO(rs.getString("PMDC_NO"));
                    doctor.setSalary(rs.getDouble("salary"));
                    doctor.setQualification(rs.getString("qualification"));
                    doctor.setSpeciality(rs.getString("speciality"));
                    doctor.setExperience(rs.getInt("years_experience"));
                    doctor.setConsultationFee(rs.getDouble("consultation_fee"));

                    java.sql.Time startTime = rs.getTime("shift_start_time");
                    java.sql.Time endTime = rs.getTime("shift_end_time");

                    if(startTime != null && endTime != null) {
                        doctor.setStartTime(startTime.toLocalTime());
                        doctor.setEndTime(endTime.toLocalTime());
                    }

                    return doctor; // Return the mapped Doctor object
                }
            }
        } catch (SQLException | InvalidPasswordException | InvalidEmailException | InvalidGenderException |
                 InvalidDateOfBirthException | InvalidNameException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();

        }
        return null; // Return null if no doctor is found
    }

    public static Patient getPatientData(String accountType, String accountID) throws SQLException {
        // Validate accountType to prevent SQL injection
        if (!"doctor".equals(accountType) && !"patient".equals(accountType)) {
            throw new IllegalArgumentException("Invalid account type: " + accountType);
        }

        String query = String.format("SELECT * FROM %s t " +
                "INNER JOIN user u ON u.user_id = t.%s_id " +
                "WHERE t.%s_id = ?;", accountType, accountType, accountType);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountID); // Use PreparedStatement to prevent injection attacks

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Patient patient = new Patient();
                    patient.setUserID(rs.getString("user_id"));
                    patient.setName(rs.getString("name"));
                    java.sql.Date sqlDate = rs.getDate("date_of_birth");
                    if (sqlDate != null) {
                        patient.setDateOfBirth(sqlDate.toLocalDate());
                    }
                    patient.setGender(rs.getString("gender"));
                    patient.setAddress(rs.getString("address"));
                    patient.setPhone(rs.getString("phone"));
                    patient.setEmail(rs.getString("email"));
                    patient.setPassword(rs.getString("password"));
                    patient.setAdmit(rs.getBoolean("is_admitted"));
                    patient.setPendingFee(rs.getDouble("pending_fee"));

                    return patient; // Return the mapped Doctor object
                }
            }
        } catch (SQLException | InvalidPasswordException | InvalidEmailException | InvalidGenderException |
                 InvalidDateOfBirthException | InvalidNameException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();

        }
        return null; // Return null if no doctor is found
    }

    public static ArrayList<Doctor> getAllDoctors() throws SQLException {
        ArrayList<Doctor> doctors = new ArrayList<>();

        String query = "SELECT * FROM user u INNER JOIN doctor d ON u.user_id = d.doctor_id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = new Doctor();
                // Map the fields from ResultSet to Doctor object
                doctor.setUserID(rs.getString("user_id"));
                doctor.setName(rs.getString("name"));

                java.sql.Date sqlDate = rs.getDate("date_of_birth");
                if (sqlDate != null) {
                    doctor.setDateOfBirth(sqlDate.toLocalDate());
                }

                doctor.setGender(rs.getString("gender"));
                doctor.setAddress(rs.getString("address"));
                doctor.setPhone(rs.getString("phone"));
                doctor.setEmail(rs.getString("email"));
                doctor.setPassword(rs.getString("password"));
                doctor.setPMDC_NO(rs.getString("PMDC_NO"));
                doctor.setSalary(rs.getDouble("salary"));
                doctor.setQualification(rs.getString("qualification"));
                doctor.setSpeciality(rs.getString("speciality"));
                doctor.setExperience(rs.getInt("years_experience"));
                doctor.setConsultationFee(rs.getDouble("consultation_fee"));

                java.sql.Time startTime = rs.getTime("shift_start_time");
                java.sql.Time endTime = rs.getTime("shift_end_time");
                if (startTime != null && endTime != null) {
                    doctor.setStartTime(startTime.toLocalTime());
                    doctor.setEndTime(endTime.toLocalTime());
                }

                // Add the Doctor object to the list
                doctors.add(doctor);
            }
        } catch (SQLException | InvalidPasswordException | InvalidEmailException | InvalidGenderException |
                 InvalidDateOfBirthException | InvalidNameException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return doctors;
    }

    public static ArrayList<Patient> getAllPatientsForDoctor(String doctorID) {
        ArrayList<Patient> patients = new ArrayList<>();

        // SQL Query to fetch all patients who had appointments with the given doctor
        String query = "SELECT DISTINCT u.user_id, u.name, u.date_of_birth, u.gender, u.address, u.phone, u.email, u.password, " +
                "p.patient_id, p.is_admitted, p.pending_fee " +
                "FROM Appointment a " +
                "JOIN Patient p ON a.patient_id = p.patient_id " +
                "JOIN User u ON p.patient_id = u.user_id " +
                "WHERE a.doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the doctor ID parameter
            stmt.setString(1, doctorID);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                // Process the result set
                while (rs.next()) {
                    Patient patient = new Patient();

                    // Map the fields from ResultSet to Patient object
                    patient.setUserID(rs.getString("user_id"));
                    patient.setName(rs.getString("name"));

                    Date sqlDate = rs.getDate("date_of_birth");
                    if (sqlDate != null) {
                        patient.setDateOfBirth(sqlDate.toLocalDate());
                    }

                    patient.setGender(rs.getString("gender"));
                    patient.setAddress(rs.getString("address"));
                    patient.setPhone(rs.getString("phone"));
                    patient.setEmail(rs.getString("email"));
                    patient.setPassword(rs.getString("password"));

                    patient.setAdmit(rs.getBoolean("is_admitted"));
                    patient.setPendingFee(rs.getDouble("pending_fee"));

                    // Create a Patient object and add to the list

                    patients.add(patient);
                    System.out.println(patient);
                }
            } catch (InvalidPasswordException | InvalidNameException | InvalidEmailException | InvalidGenderException |
                     InvalidDateOfBirthException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions (e.g., log errors, rethrow as application-specific exception, etc.)
        }

        return patients; // Return the list of patients
    }

    public static ArrayList<Doctor> getAllDoctorsForPatient(String patientID) {
        ArrayList<Doctor> doctors = new ArrayList<>();

        // SQL Query to fetch all doctors who had appointments with the given patient
        String query = "SELECT DISTINCT u.user_id, u.name, d.speciality, u.gender, u.date_of_birth, u.address, u.phone, u.email, u.password, " +
                "d.doctor_id, d.pmdc_no, d.years_experience, d.consultation_fee, d.shift_start_time, d.shift_end_time " +
                "FROM Appointment a " +
                "JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                "JOIN User u ON d.doctor_id = u.user_id " +
                "WHERE a.patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the patient ID parameter
            stmt.setString(1, patientID);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                // Process the result set
                while (rs.next()) {
                    Doctor doctor = new Doctor();

                    // Map the fields from the ResultSet to the Doctor object
                    doctor.setUserID(rs.getString("user_id"));
                    doctor.setName(rs.getString("name"));
                    doctor.setSpeciality(rs.getString("speciality"));
                    doctor.setGender(rs.getString("gender"));
                    doctor.setAddress(rs.getString("address"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setEmail(rs.getString("email"));
                    doctor.setDateOfBirth(LocalDate.parse(rs.getString("date_of_birth")));
                    doctor.setPassword(rs.getString("password"));
                    doctor.setPMDC_NO(rs.getString("pmdc_no"));
                    doctor.setExperience(rs.getInt("years_experience"));
                    doctor.setConsultationFee(rs.getDouble("consultation_fee"));
                    doctor.setStartTime(LocalTime.parse(rs.getString("shift_end_time")));
                    doctor.setEndTime(LocalTime.parse(rs.getString("shift_end_time")));

                    // Add the doctor to the list
                    doctors.add(doctor);
                    System.out.println(doctor);
                }
            } catch (InvalidPasswordException | InvalidNameException | InvalidEmailException | InvalidGenderException |
                     InvalidDateOfBirthException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions (e.g., log errors, rethrow as application-specific exception, etc.)
        }

        return doctors; // Return the list of doctors
    }

    public static ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();

        // SQL query to fetch all appointments along with doctor and patient details
        String query = "SELECT a.appointment_id, a.date_time, a.status, " +
                "d.doctor_id, d.PMDC_NO, d.speciality, d.years_experience, d.shift_start_time, d.shift_end_time, d.consultation_fee, " +
                "p.patient_id, p.is_admitted, p.pending_fee, " +
                "ud.user_id AS doctor_user_id, ud.name AS doctor_name, ud.gender AS doctor_gender, ud.address AS doctor_address, ud.phone AS doctor_phone, ud.email AS doctor_email, " +
                "up.user_id AS patient_user_id, up.name AS patient_name, up.date_of_birth AS patient_dob, up.gender AS patient_gender, up.address AS patient_address, up.phone AS patient_phone, up.email AS patient_email " +
                "FROM Appointment a " +
                "JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                "JOIN Patient p ON a.patient_id = p.patient_id " +
                "JOIN User ud ON d.doctor_id = ud.user_id " +
                "JOIN User up ON p.patient_id = up.user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Process the result set
            while (rs.next()) {
                // Create a Doctor object
                Doctor doctor = new Doctor();
                doctor.setUserID(rs.getString("doctor_user_id"));
                doctor.setName(rs.getString("doctor_name"));
                doctor.setGender(rs.getString("doctor_gender"));
                doctor.setAddress(rs.getString("doctor_address"));
                doctor.setPhone(rs.getString("doctor_phone"));
                doctor.setEmail(rs.getString("doctor_email"));
                doctor.setPMDC_NO(rs.getString("PMDC_NO"));
                doctor.setSpeciality(rs.getString("speciality"));
                doctor.setExperience(rs.getInt("years_experience"));
                doctor.setStartTime(LocalTime.parse(rs.getString("shift_start_time")));
                doctor.setEndTime(LocalTime.parse(rs.getString("shift_end_time")));
                doctor.setConsultationFee(rs.getDouble("consultation_fee"));

                // Create a Patient object
                Patient patient = new Patient();
                patient.setUserID(rs.getString("patient_user_id"));
                patient.setName(rs.getString("patient_name"));
                patient.setDateOfBirth(rs.getDate("patient_dob").toLocalDate());
                patient.setGender(rs.getString("patient_gender"));
                patient.setAddress(rs.getString("patient_address"));
                patient.setPhone(rs.getString("patient_phone"));
                patient.setEmail(rs.getString("patient_email"));
                patient.setAdmit(rs.getBoolean("is_admitted"));
                patient.setPendingFee(rs.getDouble("pending_fee"));

                // Create an Appointment object
                Appointment appointment = new Appointment(
                        rs.getString("appointment_id"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        doctor,
                        patient,
                        Appointment.AppointmentStatus.valueOf(rs.getString("status"))
                );

                // Add the Appointment object to the list
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions
        } catch (InvalidNameException | InvalidEmailException | InvalidGenderException |
                 InvalidDateOfBirthException | InvalidAppointmentException ex) {
            throw new RuntimeException(ex);
        }

        return appointments; // Return the list of all appointments
    }

    

}