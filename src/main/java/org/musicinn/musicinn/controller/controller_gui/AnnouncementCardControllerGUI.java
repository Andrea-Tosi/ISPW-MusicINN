package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;

import java.io.IOException;
import java.util.List;

public class AnnouncementCardControllerGUI {
    @FXML
    private Label numOfApplicationLabel;

    @FXML
    private Label eventDateLabel;

    @FXML
    private Label cachetLabel;

    @FXML
    private FlowPane genresList;

    private AnnouncementBean announcementBean;

    public void setupCard(AnnouncementBean bean) {
        this.announcementBean = bean;

        // Data e Ora dell'evento
        eventDateLabel.setText(bean.getStartingDate().toString() + " alle " + bean.getStartingTime().toString());

        // Aspetto economico
        cachetLabel.setText(String.format("%.2f €", bean.getCachet()));

        // Conteggio candidature (fondamentale per il Manager)
        int count = bean.getNumOfApplications();
        numOfApplicationLabel.setText(count + (count == 1 ? " candidatura" : " candidature"));

        // Generi musicali richiesti
        setupGenresLabels(bean.getRequestedGenres());
    }

    private void setupGenresLabels(List<MusicalGenre> genres) {
        genresList.getChildren().clear();
        if (genres == null) return;

        for (MusicalGenre genre : genres) {
            Label label = new Label(genre.toString());
            label.setStyle("-fx-border-color: #555555; -fx-background-radius: 10; " +
                    "-fx-padding: 2 8; -fx-border-radius: 10; -fx-font-size: 10;");
            genresList.getChildren().add(label);
        }
    }

    @FXML
    private void handleCardClick() {
        // Navigazione verso la lista delle candidature per questo annuncio
        try {
            String fxmlPath = FxmlPathLoader.getPath("fxml.accept_application_selection.view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Passaggio del bean al controller successivo (Dettaglio candidature)
            AcceptApplicationSelectionControllerGUI nextController = loader.getController();
            nextController.setAnnouncement(announcementBean);

            Stage stage = (Stage) eventDateLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
