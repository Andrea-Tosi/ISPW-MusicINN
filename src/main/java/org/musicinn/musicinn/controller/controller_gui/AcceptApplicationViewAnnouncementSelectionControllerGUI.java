package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.AcceptApplicationController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AcceptApplicationViewAnnouncementSelectionControllerGUI implements Initializable {
    @FXML
    private TilePane announcementCardsContainer;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private HeaderControllerGUI headerController;

    private static final String DESCRIPTION_PAGE = "Accetta Candidatura";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());

        loadAllAnnouncements();
    }

    private void loadAllAnnouncements() {
        AcceptApplicationController controller = new AcceptApplicationController();

        try {
            // Chiamata al controller applicativo senza paginazione
            List<AnnouncementBean> myAnnouncements = controller.getAllManagerAnnouncements();

            if (myAnnouncements == null || myAnnouncements.isEmpty()) {
                statusLabel.setText("Non hai ancora pubblicato nessun annuncio.");
                return;
            }

            // Puliamo il container e aggiungiamo tutte le card
            announcementCardsContainer.getChildren().clear();
            for (AnnouncementBean bean : myAnnouncements) {
                addAnnouncementCard(bean);
            }

            statusLabel.setText("Hai pubblicato " + myAnnouncements.size() + " annunci.");

        } catch (DatabaseException e) {
            statusLabel.setText("Errore critico nel recupero degli annunci.");
            e.printStackTrace();
        }
    }

    private void addAnnouncementCard(AnnouncementBean bean) {
        try {
            String fxmlPath = FxmlPathLoader.getPath("fxml.announcement.card");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent cardRoot = loader.load();

            AnnouncementCardControllerGUI cardController = loader.getController();
            cardController.setupCard(bean);

            announcementCardsContainer.getChildren().add(cardRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        NavigationGUI.navigateToPath(stage, FxmlPathLoader.getPath("fxml.manager.home"));
    }
}
