package ChatVideoConsultation;

import java.util.Scanner;
import User.Doctor;
import User.Patient;

public class ChatClient {
    private Doctor doctor;
    private Patient patient;
    private ChatServer server;

    public ChatClient(Doctor doctor, Patient patient, ChatServer server) {
        this.doctor = doctor;
        this.patient = patient;
        this.server = server;
    }

    public void startConversation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("👩‍⚕️ Doctor: " + doctor.getName() + " | 🧑 Patient: " + patient.getName());
        System.out.println("Type 'exit' to end the conversation.\n");

        boolean turn = true; // true for doctor, false for patient
        while (true) {
            if (turn) {
                System.out.print( doctor.getName() + ": ");
            } else {
                System.out.print(patient.getName() + ": ");
            }

            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) break;

            String sender = turn ?  doctor.getName() : patient.getName();
            server.receiveMessage(sender + ": " + message);
            turn = !turn; // Switch turn
        }

        System.out.println("\n✅ Conversation ended.");
        server.showChatHistory();
        server.saveChatToFile("chat_history.txt");
    }
}
