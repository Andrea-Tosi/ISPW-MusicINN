package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;

public class ChooseAccountTypeControllerGUI {
    @FXML
    public ToggleButton artistToggleButton;

    @FXML
    public ToggleButton managerToggleButton;

    @FXML
    public ToggleGroup IconChoice;

    @FXML
    public Label statusLabel;

    @FXML
    public Button backButton;

    @FXML
    public Button confirmButton;

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

        Navigation.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handleConfirmButton(ActionEvent event) {
        statusLabel.setText("");
        Toggle currentSelection = IconChoice.getSelectedToggle();
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
            Navigation.navigateToPath(stage, fxmlPath);
        }
    }
}
