package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class HomepageManagerControllerGUI implements Initializable {
    @FXML
    private ImageView iconUser;

    @FXML
    private Button publishAnnouncementButton;

    @FXML
    private Button acceptApplicationButton;

    @FXML
    private Button managePaymentsButton;

    @FXML
    private Button reviewButton;

    private static final String DESCRIPTION_PAGE = "HomePage";

    @FXML
    private CalendarControllerGUI calendarControllerGUI;

    @FXML
    private HeaderControllerGUI headerController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUser().getUsername());
    }

    @FXML
    public void handlePublishAnnouncementButton() {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.publish_announcement.view");
        Scene currentScene = publishAnnouncementButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }

    @FXML
    public void handleAcceptApplicationButton() {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.accept_application_announcement_selection.view");
        Scene currentScene = publishAnnouncementButton.getScene();
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
