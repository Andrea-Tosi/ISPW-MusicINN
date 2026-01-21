package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.ApplicationBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApplicationCardControllerGUI {
    @FXML
    private HBox artistNameBox;

    @FXML
    private Label artistNameLabel;

    @FXML
    private Label topMatchLabel;

    @FXML
    private Label topMatchGenresLabel;

    @FXML
    private Label topPopularityLabel;

    @FXML
    private Label topReviews;

    @FXML
    private Label typeArtistLabel;

    @FXML
    private HBox reviewsBox;

    @FXML
    private Label reviewsLabel;

    @FXML
    private HBox followersBox;

    @FXML
    private Label followersLabel;

    @FXML
    private FlowPane genresList;

    @FXML
    private Label genreLabel;

    @FXML
    private Label soundcheckDateLabel;

    @FXML
    private VBox riderBox;

    @FXML
    private Label riderLabel;

    @FXML
    private Button acceptApplicationButton;

    @FXML
    private Label totalScoreLabel;

    @FXML
    private Label matchGenresPercentage;

    @FXML
    private Label popularityPercentage;

    @FXML
    private Label reviewsPercentage;

    private AnnouncementBean announcementBean;
    private ApplicationBean applicationBean;

    public void setAnnouncementBean(AnnouncementBean announcementBean) {
        this.announcementBean = announcementBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setupCard(ApplicationBean bean) {
        setApplicationBean(bean);

        artistNameLabel.setText(bean.getArtistStageName());

        // Gestione Badge: mostriamo solo "Top Match Generi" se è alto
        topMatchLabel.setVisible(false); // Sistema globale non ancora attivo
        topPopularityLabel.setVisible(false);
        topReviews.setVisible(false);

        topMatchGenresLabel.setVisible(bean.getMatchGenresPercentage() > 75);

        // Dati numerici nella parte destra
        totalScoreLabel.setText("Match: " + Math.round(bean.getTotalScore()) + "%");
        matchGenresPercentage.setText("Generi: " + Math.round(bean.getMatchGenresPercentage()) + "%");

        // Oscuriamo o mettiamo N/D per popolarità e recensioni
        popularityPercentage.setText("Popolarità: N/D");
        reviewsPercentage.setText("Recensioni: N/D");
        reviewsLabel.setText("N/D (0 recensioni)");
        followersLabel.setText("0 followers");

        // Data Soundcheck (Questo lo hai!)
        soundcheckDateLabel.setText("Data soundcheck richiesta: " +
                bean.getRequestedSoundcheck().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

        // Setup Generi (Grafica come nelle altre card)
        setupGenresLabels(bean.getArtistGenres());

        riderLabel.setText(TechnicalRiderFormatter.format(bean.getRiderBean(), Session.UserRole.ARTIST));
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
    private void handleAcceptButton() {
        try {
            String nextFxmlPath = FxmlPathLoader.getPath("fxml.confirm_acceptance.modal");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nextFxmlPath));

            Parent root = loader.load();

            AcceptApplicationConfirmAcceptanceControllerGUI controllerSecondario = loader.getController();

            controllerSecondario.setAnnouncementBean(announcementBean);
            controllerSecondario.setApplicationBean(applicationBean);
            controllerSecondario.initData();

            Stage confirmationStage = new Stage();
            confirmationStage.setTitle("Conferma Accettazione");
            confirmationStage.setScene(new Scene(root)); // Usiamo il root caricato al punto 1

            Stage primaryStage = (Stage) acceptApplicationButton.getScene().getWindow();
            confirmationStage.initModality(Modality.WINDOW_MODAL);
            confirmationStage.initOwner(primaryStage);

            confirmationStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void setupCard(ApplicationBean bean) {
//        artistNameLabel.setText(bean.getArtistStageName());
//        typeArtistLabel.setText(bean.getArtistType().toString());
//        reviewsLabel.setText(bean.getAverageStars() + " (" + bean.getNumReviews() + " recensioni)");
//        followersLabel.setText(bean.getFollowersCount() + " followers");
//        soundcheckDateLabel.setText("Data soundcheck richiesta: " + bean.getRequestedSoundcheck().toString());
//
//        // Punteggi
//        totalScoreLabel.setText("Score: " + Math.round(bean.getTotalScore()) + "/100");
//        matchGenresPercentage.setText("Match Generi: " + Math.round(bean.getMatchGenresPercentage()) + "%");
//
//        // Badge condizionali (Esempio: mostra se > 90%)
//        topMatchLabel.setVisible(bean.getTotalScore() > 90);
//        topMatchGenresLabel.setVisible(bean.getMatchGenresPercentage() > 95);
//
//        // Popola Generi
//        genresList.getChildren().clear();
//        for (MusicalGenre g : bean.getArtistGenres()) {
//            genresList.getChildren().add(new Label(g.toString())); // Aggiungi stile come negli altri controller
//        }
//
//        // Rider Tecnico (semplificato)
//        riderBox.getChildren().clear();
//        riderBox.getChildren().add(new Label("Rider Tecnico: " + bean.getRiderSummary()));
//    }
//
//    @FXML
//    private void handleAcceptApplication() {
//        // Qui andrà la logica per confermare la data e inviare la notifica all'artista
//    }
}
//TODO se c'è già un accordo con un artista, disabilita i bottoni per accettare le candidature (acceptApplicationButton)