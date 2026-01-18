package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.ApplyController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.EventBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.io.IOException;
import java.util.List;

public class EventCardControllerGUI {
    @FXML
    private Label venueNameLabel;

    @FXML
    private Label typeVenueLabel;

    @FXML
    private Label addressVenueLabel;

    @FXML
    private Label distanceLabel; //TODO si potrebbe segnarlo come hyperlink (invece che come Label) per aprire l'indirizzo su google maps in caso di click

    @FXML
    private Label dateEventLabel;

    @FXML
    private Label cachetLabel;

    @FXML
    private FlowPane genresContainer;

    @FXML
    private HBox riderCompatibilityHBox;

    @FXML
    private Label riderCompatibilityLabel;

    private EventBean eventBean;

    public EventBean getEventBean() {
        return eventBean;
    }

    public void setEventBean(EventBean eventBean) {
        this.eventBean = eventBean;
    }

    public void setupEventCard(EventBean bean) {
        this.eventBean = bean;

        AnnouncementBean ab = bean.getAnnouncementBean();
        venueNameLabel.setText(bean.getVenueName());
        typeVenueLabel.setText(bean.getTypeVenue().toString());
        addressVenueLabel.setText(bean.getVenueAddress() + ", " + bean.getVenueCity());
        String distanceString = "dista " + bean.getDistance() + " km da te";
        distanceLabel.setText(distanceString);
        dateEventLabel.setText(ab.getStartingDate().toString());
        cachetLabel.setText(ab.getCachet().toString());
        setupGenresLabels(ab.getRequestedGenres());
        setupRiderCompatibility(bean.getReport().isValid());
    }

    private void setupGenresLabels(List<MusicalGenre> genres) {
        for (MusicalGenre genre : genres) {
            Label genreLabel = createGenreLabel(genre.toString());
            genresContainer.getChildren().add(genreLabel);
        }
    }

    private Label createGenreLabel(String text) {
        Label label = new Label(text);

        // Se non usi CSS, puoi impostare lo stile inline:
        label.setStyle("-fx-border-color:  #aaaaaa; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 3 10 3 10; " +
                "-fx-border-radius: 15;");
        return label;
    }

    private void setupRiderCompatibility(boolean b) {
        if (b) {
            riderCompatibilityLabel.setText("Rider Tecnico Compatibile");
            riderCompatibilityHBox.setStyle("-fx-background-color:  #00ff11; " + "-fx-background-radius: 15;");
        } else {
            riderCompatibilityLabel.setText("Rider Tecnico non Compatibile");
            riderCompatibilityHBox.setStyle("-fx-background-color:  #ff0000; " + "-fx-background-radius: 15;");
        }
    }

    @FXML
    private void handleCardClick() {
        try {
            // 1. Caricamento del nuovo FXML per il dettaglio
            String fxmlPath = FxmlPathLoader.getPath("fxml.apply_event_details.modal");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage eventDetailsStage = new Stage();
            eventDetailsStage.setTitle("Dettagli Evento");
            eventDetailsStage.setScene(new Scene(loader.load()));

            // 2. Passaggio dei dati al controller della modale
            ApplyViewEventDetailsControllerGUI detailsController = loader.getController();
            detailsController.setEventBean(eventBean);
            detailsController.init();

            Scene currentScene = venueNameLabel.getScene();
            Stage primaryStage = (Stage) currentScene.getWindow();

            // Rendiamo la finestra modale (blocca l'interazione con quella sotto)
            eventDetailsStage.initModality(Modality.APPLICATION_MODAL);
            eventDetailsStage.initOwner(primaryStage);

            eventDetailsStage.showAndWait(); // Resta aperta finché l'utente non la chiude

            if (eventBean.getAnnouncementBean().getSoundcheckTime() != null) {
                ApplyController controller = new ApplyController();
                controller.createApplication(eventBean);

                // Feedback all'utente
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Candidatura inviata con successo!");
                alert.show();

                fxmlPath = FxmlPathLoader.getPath("fxml.artist.home");
                currentScene = venueNameLabel.getScene();
                Stage stage = (Stage) currentScene.getWindow();
                NavigationGUI.navigateToPath(stage, fxmlPath);
            }
        } catch (DatabaseException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Errore nel salvataggio della candidatura nel database. Riprovare.");
            alert.show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
