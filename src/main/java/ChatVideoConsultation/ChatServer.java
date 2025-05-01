package ChatVideoConsultation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private List<String> chatHistory = new ArrayList<>();

    public void receiveMessage(String message) {
        chatHistory.add(message);
        System.out.println("📥 " + message);
    }

    public void showChatHistory() {
        System.out.println("\n=== Chat History ===");
        for (String msg : chatHistory) {
            System.out.println(msg);
        }
    }

    public void saveChatToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String msg : chatHistory) {
                writer.write(msg + "\n");
            }
            System.out.println("\n💾 Chat saved to " + filename);
        } catch (IOException e) {
            System.out.println("❌ Failed to save chat: " + e.getMessage());
        }
    }
}
