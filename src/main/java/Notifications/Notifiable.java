package Notifications;
import User.User;

public interface Notifiable {
    void sendNotification(String message, String recipientEmail);


}
