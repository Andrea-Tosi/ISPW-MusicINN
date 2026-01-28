package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class HomepageArtistControllerGUI implements Initializable {
    @FXML
    private Button applyButton;

    @FXML
    private Button cancelBookingButton;

    @FXML
    private Button managePaymentsButton;

    @FXML
    private Button reviewButton;

    @FXML
    private HeaderControllerGUI headerController;

    private static final String DESCRIPTION_PAGE = "HomePage";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUser().getUsername());
    }

    @FXML
    private void handleApplyButton(ActionEvent event) {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.apply_rider_revision.view");
        Scene currentScene = applyButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }

    @FXML
    public void handleManagePaymentsButton() {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.payments.view");
        Scene currentScene = managePaymentsButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }
}
//TODO prima di consentire il click su qualsiasi bottone, far compilare all'utente il rider tecnico