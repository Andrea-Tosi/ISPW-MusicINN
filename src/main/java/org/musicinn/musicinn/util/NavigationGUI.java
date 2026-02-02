package org.musicinn.musicinn.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class NavigationGUI {
    private static final Logger LOGGER = Logger.getLogger(NavigationGUI.class.getName());

    private NavigationGUI() {}

    public static void navigateToPath(Stage currentStage, String fxmlPath){
        try {
            FXMLLoader loader = new FXMLLoader(NavigationGUI.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            LOGGER.fine(e.getMessage());
        }
    }
}
