package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.EventBean;
import org.musicinn.musicinn.util.bean.technical_rider_bean.TechnicalRiderBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ApplyViewEventDetailsControllerGUI {
    @FXML
    private Label venueNameLabel;

    @FXML
    private Label typeVenueLabel;

    @FXML
    private HBox riderCompatibilityHBox;

    @FXML
    private Label riderCompatibilityLabel;

    @FXML
    private VBox positionBox;

    @FXML
    private Label addressLabel;

    @FXML
    private Label distanceLabel;

    @FXML
    private VBox dateBox;

    @FXML
    private Label startDateLabel;

    @FXML
    private Label endDateLabel;

    @FXML
    private VBox moneyBox;

    @FXML
    private Label cachetLabel;

    @FXML
    private Label depositLabel;

    @FXML
    private VBox artistRequirementsBox;

    @FXML
    private VBox artistTypesBox;

    @FXML
    private FlowPane artistTypesList;

    @FXML
    private VBox musicalGenresBox;

    @FXML
    private FlowPane musicalGenresList;

    @FXML
    private VBox doesUnreleasedBox;

    @FXML
    private Label doesUnreleasedLabel;

    @FXML
    private VBox technicalRiderBox;

    @FXML
    private Label riderLabel;

    @FXML
    private VBox technicalRiderList;

    @FXML
    private VBox stageDimensionsBox;

    @FXML
    private Label stageDimensionsLabel;

    @FXML
    private VBox incompatibilityBox;

    @FXML
    private Label incompatibilityLabel;

    @FXML
    private VBox eventDescriptionBox;

    @FXML
    private Label eventDescriptionLabel;

    @FXML
    private Button applyButton;

    private EventBean eventBean;

    public EventBean getEventBean() {
        return eventBean;
    }

    public void setEventBean(EventBean eventBean) {
        this.eventBean = eventBean;
    }

    public void init() {
        venueNameLabel.setText(eventBean.getVenueName());
        typeVenueLabel.setText(eventBean.getTypeVenue().toString());
        setupRiderCompatibility(eventBean.getReport().isValid());
        applyButton.setDisable(!eventBean.getReport().isValid());
        addressLabel.setText(eventBean.getVenueAddress());
        distanceLabel.setText(eventBean.getDistance() + " km dalla tua sede");

        AnnouncementBean ab = eventBean.getAnnouncementBean();
        startDateLabel.setText(ab.getStartingDate().toString() + " alle " + ab.getStartingTime().toString());
        LocalDateTime dts = LocalDateTime.of(ab.getStartingDate(), ab.getStartingTime());
        LocalDateTime dtf = dts.plus(ab.getDuration());
        endDateLabel.setText(dtf.toLocalDate().toString() + " alle " + dtf.toLocalTime().toString());

        cachetLabel.setText(ab.getCachet() + " €");
        depositLabel.setText(ab.getDeposit() + " €");

        setupArtistTypesLabels(ab.getRequestedTypesArtist());
        setupGenresLabels(ab.getRequestedGenres());

        if (ab.getDoesUnreleased() == null) {
            doesUnreleasedBox.setVisible(false);
            doesUnreleasedBox.setManaged(false);
        } else {
            boolean doesUnreleased = ab.getDoesUnreleased();
            doesUnreleasedLabel.setText("L'artista deve fare " + (doesUnreleased ? "inediti" : "cover"));
        }

        TechnicalRiderBean trb = eventBean.getTechnicalRiderBean();
        riderLabel.setText(TechnicalRiderFormatter.format(trb, Session.UserRole.MANAGER));
        stageDimensionsLabel.setText(trb.getMinLengthStage() + "m x " + trb.getMinWidthStage() + "m");
        if (eventBean.getReport().isValid()) {
            incompatibilityBox.setManaged(false);
            incompatibilityBox.setVisible(false);
        } else {
            incompatibilityLabel.setText(eventBean.getReport().toString());
        }
        eventDescriptionLabel.setText(ab.getDescription());
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

    private void setupArtistTypesLabels(List<TypeArtist> types) {
        for (TypeArtist type : types) {
            Label genreLabel = createLabel(type.toString());
            artistTypesList.getChildren().add(genreLabel);
        }
    }

    private void setupGenresLabels(List<MusicalGenre> genres) {
        for (MusicalGenre genre : genres) {
            Label genreLabel = createLabel(genre.toString());
            musicalGenresList.getChildren().add(genreLabel);
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);

        // Se non usi CSS, puoi impostare lo stile inline:
        label.setStyle("-fx-border-color:  #aaaaaa; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 3 10 3 10; " +
                "-fx-border-radius: 15;");
        return label;
    }

    @FXML
    private void handleApplyButton(ActionEvent event) {
        try {
            // 1. Caricamento del nuovo FXML per il dettaglio
            String fxmlPath = FxmlPathLoader.getPath("fxml.apply_confirm_application.modal");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage eventDetailsStage = new Stage();
            eventDetailsStage.setTitle("Conferma Candidatura");
            eventDetailsStage.setScene(new Scene(loader.load()));

            // 2. Passaggio dei dati al controller della modale
            ApplyViewConfirmApplicationControllerGUI controller = loader.getController();
            controller.setAnnouncementBean(eventBean.getAnnouncementBean());

            Scene currentScene = venueNameLabel.getScene();
            Stage primaryStage = (Stage) currentScene.getWindow();

            // Rendiamo la finestra modale (blocca l'interazione con quella sotto)
            eventDetailsStage.initModality(Modality.APPLICATION_MODAL);
            eventDetailsStage.initOwner(primaryStage);

            eventDetailsStage.showAndWait(); // Resta aperta finché l'utente non la chiude

            if (eventBean.getAnnouncementBean().getSoundcheckTime() != null) {
                ((Stage) applyButton.getScene().getWindow()).close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
