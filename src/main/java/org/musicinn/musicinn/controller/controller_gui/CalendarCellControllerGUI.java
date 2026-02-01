package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.SchedulableEventBean;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CalendarCellControllerGUI implements Initializable {
    @FXML
    private VBox cellVBox;

    @FXML
    private Label dayLabel;

    @FXML
    private VBox eventVBox;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String TODAY_LABEL_STYLE = "today-label";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Creiamo una maschera rettangolare
        Rectangle clip = new Rectangle();

        // La maschera avrà sempre la stessa dimensione della cella
        clip.widthProperty().bind(cellVBox.widthProperty());
        clip.heightProperty().bind(cellVBox.heightProperty());

        // Tutto ciò che esce dai bordi della cella viene nascosto (troncato visivamente)
        cellVBox.setClip(clip);
    }

    /**
     * Imposta il numero del giorno (es. "15")
     */
    public void setDay(int dayToDisplay) {
        dayLabel.setText(String.valueOf(dayToDisplay));
    }

    /**
     * Pulisce completamente la cella per poterla riutilizzare.
     * Rimuove eventi, resetta il testo e toglie gli stili speciali.
     */
    public void resetCell() {
        dayLabel.setText("");
        eventVBox.getChildren().clear();

        // Rimuove classi CSS specifiche
        dayLabel.getStyleClass().remove(TODAY_LABEL_STYLE);
    }

    /**
     * Aggiunge graficamente un evento alla cella.
     */
    public void addEvent(SchedulableEventBean event) {
        String startTime = event.getStartingTime().format(TIME_FORMATTER);
        String endTime = event.getStartingTime().plus(event.getDuration()).format(TIME_FORMATTER);
        String timeRange = startTime + " - " + endTime;
        String nameToDisplay = Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST) ? event.getVenueName() : event.getArtistStageName();
        Label eventLabel = new Label(timeRange + "\n" + nameToDisplay);
        eventLabel.setTextAlignment(TextAlignment.CENTER);
        eventLabel.setAlignment(Pos.CENTER);
        eventLabel.setWrapText(true);
        eventLabel.setMinWidth(0);
        eventLabel.setPrefWidth(1);
        eventLabel.setMaxWidth(Double.MAX_VALUE); // Fa sì che la label occupi tutta la larghezza della cella
        eventLabel.setMinHeight(0);
        eventLabel.setPrefHeight(1);
        eventLabel.setMaxHeight(Double.MAX_VALUE);

        eventVBox.setFillWidth(true);
        VBox.setVgrow(eventLabel, Priority.ALWAYS);
        eventVBox.getChildren().add(eventLabel);
    }

    /**
     * Applica uno stile visivo differente per il giorno corrente.
     */
    public void setAsToday() {
        if (!dayLabel.getStyleClass().contains(TODAY_LABEL_STYLE)) {
            dayLabel.getStyleClass().add(TODAY_LABEL_STYLE);
        }
    }
}
