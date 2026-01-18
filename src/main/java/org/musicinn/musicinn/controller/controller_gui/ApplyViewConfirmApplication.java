package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.ApplyController;
import org.musicinn.musicinn.util.bean.AnnouncementBean;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ApplyViewConfirmApplication implements Initializable {
    @FXML
    private ComboBox<String> hoursComboBox;

    @FXML
    private ComboBox<String> minutesComboBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Button deleteButton;

    private AnnouncementBean announcementBean;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTimeComboBox();
    }

    private void setupTimeComboBox() {
        // Popola le Ore (00-23)
        for (int i = 0; i < 24; i++) {
            // String.format("%02d", i) aggiunge uno zero davanti se il numero è a una cifra
            hoursComboBox.getItems().add(String.format("%02d", i));
        }

        // Popola i Minuti (00-59)
        for (int i = 0; i < 60; i++) {
            minutesComboBox.getItems().add(String.format("%02d", i));
        }
    }

    public AnnouncementBean getAnnouncementBean() {
        return announcementBean;
    }

    public void setAnnouncementBean(AnnouncementBean announcementBean) {
        this.announcementBean = announcementBean;
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        ((Stage) deleteButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleConfirmButton(ActionEvent event) {
        if (hoursComboBox.getValue() == null || minutesComboBox.getValue() == null) {
            statusLabel.setText("Riempi tutti i campi");
            return;
        }

        int hours = Integer.parseInt(hoursComboBox.getValue());
        int minutes = Integer.parseInt(minutesComboBox.getValue());
        Duration duration = Duration.ofMinutes(minutes).plusHours(hours);
        LocalDateTime soundcheckTime = LocalDateTime.of(announcementBean.getStartingDate(), announcementBean.getStartingTime());
        soundcheckTime = soundcheckTime.minus(duration);
        announcementBean.setSoundcheckTime(soundcheckTime);

        ((Stage) confirmButton.getScene().getWindow()).close();
    }
}
