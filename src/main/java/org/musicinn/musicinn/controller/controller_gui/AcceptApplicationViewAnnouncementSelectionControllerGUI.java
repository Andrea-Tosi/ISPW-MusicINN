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
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(AcceptApplicationViewAnnouncementSelectionControllerGUI.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUser().getUsername());

        loadAllAnnouncements();
    }

    private void loadAllAnnouncements() {
        AcceptApplicationController controller = AcceptApplicationController.getSingletonInstance();

        try {
            // Chiamata al controller applicativo senza paginazione
            List<AnnouncementBean> announcements = controller.getAllManagerAnnouncements();

            if (announcements == null || announcements.isEmpty()) {
                statusLabel.setText("Non hai ancora pubblicato nessun annuncio.");
                return;
            }

            // Puliamo il container e aggiungiamo tutte le card
            announcementCardsContainer.getChildren().clear();
            for (AnnouncementBean bean : announcements) {
                addAnnouncementCard(bean);
            }

            statusLabel.setText("Hai pubblicato " + announcements.size() + " annunci.");

        } catch (PersistenceException _) {
            statusLabel.setText("Errore critico nel recupero degli annunci.");
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
            LOGGER.fine(e.getMessage());
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        NavigationGUI.navigateToPath(stage, FxmlPathLoader.getPath("fxml.manager.home"));
    }
}
