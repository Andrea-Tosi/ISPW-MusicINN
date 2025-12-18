package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;

public class ChooseAccountTypeControllerGUI {
    @FXML
    private ToggleButton artistToggleButton;

    @FXML
    private ToggleButton managerToggleButton;

    @FXML
    private ToggleGroup iconChoice;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button confirmButton;

    @FXML
    private void handleChoiceButton(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        button.setSelected(true);
        confirmButton.fire();
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.registration_user.view");

        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handleConfirmButton(ActionEvent event) {
        statusLabel.setText("");
        Toggle currentSelection = iconChoice.getSelectedToggle();
        if (currentSelection == null) {
            statusLabel.setText("Seleziona un tipo di account.");
        } else {
            Scene currentScene = statusLabel.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            String fxmlPath = "";

            if (currentSelection == artistToggleButton) {
                fxmlPath = FxmlPathLoader.getPath("fxml.registration_artist.view");
            } else if (currentSelection == managerToggleButton) {
                fxmlPath = FxmlPathLoader.getPath("fxml.registration_manager.view");
            }
            NavigationGUI.navigateToPath(stage, fxmlPath);
        }
    }
}
