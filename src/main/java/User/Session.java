package User;

public class Session {
    private static Patient loggedInPatient;

    public static void setLoggedInPatient(Patient patient) {
        loggedInPatient = patient;
    }

    public static Patient getLoggedInPatient() {
        return loggedInPatient;
    }

    public static void logout() {
        loggedInPatient = null;
    }
}
