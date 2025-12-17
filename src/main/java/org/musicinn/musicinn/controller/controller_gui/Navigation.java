package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigation {
    public static void navigateToPath(Stage currentStage, String fxmlPath){
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento del file FXML: " + fxmlPath);
        }
    }
}
