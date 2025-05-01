package User;

import Appointment.Appointment;

import java.time.LocalDateTime;
import java.util.Random; // using the Random class to generate a user id
import java.util.regex.Pattern; // using the Pattern class for credentials and information matching
import java.time.Period; // using the Calendar class for age calculation
import java.time.LocalDate;
import Exceptions.*;


// custom exception classes


// base class for application users
// Identifiers (variables and method names) are chosen in a sense that they are self-explanatory
public abstract class User {
    private static final Random rand = new Random(); // instantiating the Random class object
    // patterns for authentication
    private static final String NAME_PATTERN = "^[a-zA-Z]{2,}(\\s[a-zA-Z]{2,})+$";
    private static final String MAIL_PATTERN = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]+$";
    private static final String PASS_PATTERN = "^[a-zA-Z0-9@#$*&=+!]{8,20}+$";
    private static final char[] characters = {'a', 'b', 'c', 'd', 'e', 'f','A', 'B', 'C', 'D', 'E', 'F', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    // declaring all the user attributes, keeping them private to ensure security and applying the encapsulation principle of OOP
//    private final String userID = randomIdGenerator();
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String password;

    // defining the constructor;
    User(String name, LocalDate dob, String gender, String address, String phone, String email, String password)
            throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException {
        setName(name);
        setDateOfBirth(dob);
        setGender(gender);
        setAddress(address);
        setPhone(phone);
        setEmail(email);
        setPassword(password);
    }


    // defining all the setters with validations
    public void setName(String name) throws InvalidNameException {
        if (!isValidName(name)) {
            throw new InvalidNameException("Invalid name! Name must match pattern: " + NAME_PATTERN);
        }
        this.name = name;
    }


    public void setDateOfBirth(LocalDate birthDate) throws InvalidDateOfBirthException {
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new InvalidDateOfBirthException("Invalid date of birth! Please provide a valid past date.");
        }
        this.dateOfBirth = birthDate;
    }


    public void setGender(String gender) throws InvalidGenderException {
        if (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")) {
            throw new InvalidGenderException("Invalid gender! Only 'male' or 'female' are allowed.");
        }
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address; // No specific validation required for address
    }

    public void setPhone(String phone) {
        this.phone = phone; // Assuming no validation for simplicity
    }

    public void setEmail(String email) throws InvalidEmailException {
        if (!isValidEmail(email)) {
            throw new InvalidEmailException("Invalid email! Email must match pattern: " + MAIL_PATTERN);
        }
        this.email = email;
    }

    public void setPassword(String password) throws InvalidPasswordException {
        if (!isValidPassword(password)) {
            throw new InvalidPasswordException("Invalid password! Password must match pattern: " + PASS_PATTERN);
        }
        this.password = password;
    }


    // defining all the getters
//    public String getUserID() {
//        return userID;
//    }
    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }
    public String getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public Period getAge() {
        return Period.between(dateOfBirth, LocalDate.now());
    }

    // defining user class methods
    public void changeUsername(String name) {
        if(!isValidName(name)) {
            System.out.println("Invalid name!");
            return;
        }
        this.name = name;
    }

    public void changePassword(String password) {
        if(!isValidPassword(password)) {
            System.out.println("Invalid password!");
            return;
        }
        this.password = password;
    }

    // validation and generation functions
    public static String randomIdGenerator() {
        String id = "";
        for(int i = 0; i < 8; i++) {
            id += characters[rand.nextInt(characters.length)];
        }
        return id;
    }

    // declaring abstract methods
    public abstract void scheduleAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime);

    public abstract void cancelAppointment(Appointment appointment);

    public static boolean isValidName(String name) {
        return Pattern.matches(NAME_PATTERN, name);
    }

    public static boolean isValidEmail(String email) {
        return Pattern.matches(MAIL_PATTERN, email);
    }

    public static boolean isValidPassword(String password) {
        return Pattern.matches(PASS_PATTERN, password);
    }

    // overriding the toString method to display the user
    @Override
    public String toString() {
        return String.format("Name: %s\tDate Of Birth: %s\tGender: %s\tAddress: %s\tPhone: %s\tEmail: %s\tPassword: %s", name, dateOfBirth, gender, address, phone, email, password);
    }
}