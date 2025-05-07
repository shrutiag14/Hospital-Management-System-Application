package com.hamza6dev.oopsieeee;

import Exceptions.*;
import User.Doctor;
import User.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataFetcher {

    public static Doctor getDoctorData(String accountType, String accountID) throws SQLException {
        // Validate accountType to prevent SQL injection
        if (!"doctor".equals(accountType) && !"patient".equals(accountType)) {
            throw new IllegalArgumentException("Invalid account type: " + accountType);
        }

        String query = "SELECT * FROM " + accountType + " t " +
                "INNER JOIN user u ON u.user_id = t.user_id " +
                "WHERE t.user_id = ?;";

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

        String query = "SELECT * FROM " + accountType + " t " +
                "INNER JOIN user u ON u.user_id = t.user_id " +
                "WHERE t.user_id = ?;";

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

}