package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.AcceptApplicationController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.ApplicationBean;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.io.IOException;
import java.util.List;

public class AcceptApplicationSelectionControllerGUI {
    @FXML
    private VBox mainBox;

    @FXML
    private Label eventDateLabel;

    @FXML
    private Label numOfApplicationLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private HeaderControllerGUI headerController;

    private static final String DESCRIPTION_PAGE = "Accetta Candidatura";

    private AnnouncementBean selectedAnnouncement;

    public void setAnnouncement(AnnouncementBean bean) {
        this.selectedAnnouncement = bean;
        initData();
    }

    private void initData() {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());

        eventDateLabel.setText("Evento del " + selectedAnnouncement.getStartingDate() + " " + selectedAnnouncement.getStartingTime());

        AcceptApplicationController controller = AcceptApplicationController.getSingletonInstance();
        try {
            List<ApplicationBean> apps = controller.getApplicationsForAnnouncement(selectedAnnouncement);
            numOfApplicationLabel.setText(apps.size() + " artisti hanno risposto al tuo annuncio...");

            for (ApplicationBean app : apps) {
                addApplicationCard(app);
            }
        } catch (DatabaseException e) {
            statusLabel.setText("Errore nel caricamento delle candidature.");
        }
    }

    private void addApplicationCard(ApplicationBean appBean) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlPathLoader.getPath("fxml.application.card")));
            Parent card = loader.load();
            ApplicationCardControllerGUI controller = loader.getController();
            controller.setAnnouncementBean(selectedAnnouncement);
            controller.setupCard(appBean);
            mainBox.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        NavigationGUI.navigateToPath((Stage)backButton.getScene().getWindow(), FxmlPathLoader.getPath("fxml.accept_application_announcement_selection.view"));
    }
}
