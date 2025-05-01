package ChatVideoConsultation;

public class VideoCall {
    public void startCall(String platform,  String link) {

        if (platform.equalsIgnoreCase("Zoom")) {
            link = "https://zoom.us/" + link;
        } else {
            link = "https://meet.google.com/" + link;
        }

        System.out.println("🔗 Starting video call using " + platform + ": " + link);
    }
}
