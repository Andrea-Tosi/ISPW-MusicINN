package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.ApplyController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ApplyViewTechnicalRiderRevisionControllerGUI implements Initializable {
    @FXML
    private Label stageDimensionsLabel;

    @FXML
    private VBox requestedEquipmentsBox;

    @FXML
    private Label riderHeaderLabel;

    @FXML
    private Label riderLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button modifyRiderButton;

    @FXML
    private Button continueButton;

    @FXML
    private HeaderControllerGUI headerController;

    private static final String DESCRIPTION_PAGE = "Candidati";
    private static final Session.UserRole ROLE  = Session.getSingletonInstance().getRole();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());

        TechnicalRiderBean trBean = new TechnicalRiderBean();
        ApplyController controller = new ApplyController();
        if (ROLE.equals(Session.UserRole.ARTIST)) riderHeaderLabel.setText("Attrezzatura richiesta");
        else if (ROLE.equals(Session.UserRole.MANAGER)) riderHeaderLabel.setText("Attrezzatura a disposizione");
        controller.getEquipmentBeans(trBean);
        setupStageDimensionsLabel(trBean);
        riderLabel.setText(TechnicalRiderFormatter.format(trBean, ROLE));
    }

    private void setupStageDimensionsLabel(TechnicalRiderBean trBean) {
        int length = trBean.getMinLengthStage();
        int width = trBean.getMinWidthStage();
        String stageDimensions = String.format("%dm x %dm", length, width);
        stageDimensionsLabel.setText(stageDimensions);
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.artist.home");
        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handleModifyRiderButton(ActionEvent event) {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.management_technical_rider.view");
        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handleContinueButton(ActionEvent event) {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.apply_event_choice.view");
        Scene currentScene = continueButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }
}
