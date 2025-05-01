package Exceptions;

public class DuplicateAppointmentException extends Exception {
    public DuplicateAppointmentException(String message) {
        super(message);
    }
}
