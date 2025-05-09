package com.hamza6dev.oopsieeee;

import javafx.scene.control.Alert;

    public class AlertDialogueBox {
        public static void showAlert(Alert.AlertType alertType, String title, String message) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
    }
}
