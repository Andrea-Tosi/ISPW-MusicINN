package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());
    }

    @FXML
    private void handleApplyButton(ActionEvent event) {

    }
}
//TODO prima di consentire il click su qualsiasi bottone, far compilare all'utente il rider tecnico