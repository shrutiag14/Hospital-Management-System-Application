package com.hamza6dev.oopsieeee;

import Exceptions.*;
import User.Doctor;
import User.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
                    doctor.setConsulationFee(rs.getDouble("consultation_fee"));

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
                doctor.setConsulationFee(rs.getDouble("consultation_fee"));

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

}