package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.SchedulableEventBean;

import java.time.format.DateTimeFormatter;

public class CalendarCellControllerGUI {
    @FXML
    private Label dayLabel;

    @FXML
    private VBox eventVBox;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String TODAY_LABEL_STYLE = "today-label";

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
        eventLabel.setMaxWidth(Double.MAX_VALUE); // Fa sì che la label occupi tutta la larghezza della cella
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
