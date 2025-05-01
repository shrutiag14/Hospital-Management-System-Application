package Appointment;
import User.*;
import Exceptions.*;

import java.util.ArrayList;
import java.time.LocalDateTime;


// Appointment manager is the only class that stores all the appointments, update the appointment list, remove anything, or approve/reject everything
// is controlled by this class
public class AppointmentManager {
    private final ArrayList<Appointment> appointments; // This arraylist is the global appointment store, no other store should
    // exist that contradicts its elements

    public AppointmentManager() {
        this.appointments = new ArrayList<>();
    }

    // Create a new appointment
    public void requestAppointment(LocalDateTime dateTime, Doctor doctor, Patient patient)
            throws InvalidAppointmentException, DuplicateAppointmentException {
        if (dateTime == null || doctor == null || patient == null) {
            throw new InvalidAppointmentException("Appointment request failed. Date & Time, doctor, or patient cannot be null.");
        }

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Appointment request failed. Appointment date & time cannot be in the past.");
        }

        if (isDuplicateAppointment(dateTime, doctor, patient)) {
            throw new DuplicateAppointmentException("Appointment request failed. A similar appointment already exists.");
        }

        appointments.add(new Appointment(dateTime, doctor, patient, Appointment.AppointmentStatus.PENDING));
        System.out.println("Appointment successfully requested for: " + dateTime);
    }


    public void approveAppointment(Appointment appointment)
            throws InvalidAppointmentException, AppointmentNotFoundException {
        if (appointment == null) {
            throw new InvalidAppointmentException("Cannot approve. Appointment cannot be null.");
        }

        if (!appointments.contains(appointment)) {
            throw new AppointmentNotFoundException("Cannot approve. Appointment not found in the system.");
        }

        appointment.updateStatus(Appointment.AppointmentStatus.APPROVED);
        System.out.println("Appointment approved successfully.");
    }


    public void cancelAppointment(Appointment appointment)
            throws InvalidAppointmentException, AppointmentNotFoundException {
        if (appointment == null) {
            throw new InvalidAppointmentException("Cannot cancel. Appointment cannot be null.");
        }

        if (!appointments.remove(appointment)) {
            throw new AppointmentNotFoundException("Cannot cancel. Appointment not found in the system.");
        }

        appointment.updateStatus(Appointment.AppointmentStatus.CANCELED);
        System.out.println("Appointment cancelled successfully.");
    }

    public void rescheduleAppointment(Appointment appointment, LocalDateTime newDateTime)
            throws InvalidAppointmentException, AppointmentNotFoundException, DuplicateAppointmentException {
        if (appointment == null || newDateTime == null) {
            throw new InvalidAppointmentException("Cannot reschedule. Appointment or new date & time cannot be null.");
        }

        if (!appointments.contains(appointment)) {
            throw new AppointmentNotFoundException("Cannot reschedule. Appointment not found in the system.");
        }

        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Cannot reschedule. The new appointment date & time cannot be in the past.");
        }

        if (isDuplicateAppointment(newDateTime, appointment.getDoctor(), appointment.getPatient())) {
            throw new DuplicateAppointmentException("Cannot reschedule. A similar appointment already exists at the chosen time.");
        }

        appointment.updateDateTime(newDateTime);
        System.out.println("Appointment rescheduled successfully to: " + newDateTime);
    }



    public void updateAppointmentStatus(Appointment appointment, Appointment.AppointmentStatus newStatus)
            throws InvalidAppointmentException, AppointmentNotFoundException, DuplicateAppointmentException {

        if (appointment == null || newStatus == null) {
            throw new InvalidAppointmentException("Cannot update the status. Appointment or new date & time cannot be null.");
        }

        if (!appointments.contains(appointment)) {
            throw new AppointmentNotFoundException("Cannot update status. Appointment not found in the system.");
        }

        appointment.updateStatus(newStatus);
        System.out.println("Appointment status updated successfully to" + newStatus + " .");
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public ArrayList<Appointment> getPendingAppointments() {
        ArrayList<Appointment> pendingAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.PENDING) {
                pendingAppointments.add(appointment);
            }
        }
        return pendingAppointments;
    }

    public ArrayList<Appointment> getApprovedAppointments() {
        ArrayList<Appointment> approvedAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.APPROVED) {
                approvedAppointments.add(appointment);
            }
        }
        return approvedAppointments;
    }

    public ArrayList<Appointment> getCancelledAppointments() {
        ArrayList<Appointment> cancelledAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELED) {
                cancelledAppointments.add(appointment);
            }
        }
        return cancelledAppointments;
    }

    public ArrayList<Appointment> getNoShowAppointments() {
        ArrayList<Appointment> noShowAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
                noShowAppointments.add(appointment);
            }
        }
        return noShowAppointments;
    }

    public ArrayList<Appointment> getRejectedAppointments() {
        ArrayList<Appointment> rejectedAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.REJECTED) {
                rejectedAppointments.add(appointment);
            }
        }
        return rejectedAppointments;
    }

    public ArrayList<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        ArrayList<Appointment> doctorsAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor() == doctor) {
                doctorsAppointments.add(appointment);
            }
        }
        return doctorsAppointments;
    }

    public ArrayList<Appointment> getCompletedAppointments() {
        ArrayList<Appointment> completedAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
                completedAppointments.add(appointment);
            }
        }
        return completedAppointments;
    }

    public ArrayList<Appointment> getAppointmentsByPatient(Patient patient) {
        ArrayList<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getPatient() == patient) {
                patientAppointments.add(appointment);
            }
        }
        return patientAppointments;
    }

    public void generateReport() {
        System.out.println("Generating Record...");
        System.out.println();
        System.out.println("Total number of appointments: " + appointments.size());
        System.out.println();
        System.out.println("Total number of appointments approved: " + getApprovedAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments cancelled: " + getCancelledAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments no-show: " + getNoShowAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments rejected: " + getRejectedAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments approved: " + getApprovedAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments cancelled: " + getCancelledAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments no-show: " + getNoShowAppointments().size());
        System.out.println();
        System.out.println("Total number of appointments rejected: " + getRejectedAppointments().size());
        System.out.println();
    }

    public boolean isDuplicateAppointment(LocalDateTime dateTime, Doctor doctor, Patient patient) {
        for (Appointment appointment : appointments) {
            if (appointment.getDateTime().equals(dateTime) &&
                    appointment.getDoctor().equals(doctor) &&
                    appointment.getPatient().equals(patient)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Appointment appointment) {
        return appointments.contains(appointment);
    }

    public void viewAppointments() {
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
            System.out.println();
        }
    }

//    public boolean isDuplicateAppointment(LocalDate date, Doctor doctor, User.Patient patient) {
//
//    }
}
