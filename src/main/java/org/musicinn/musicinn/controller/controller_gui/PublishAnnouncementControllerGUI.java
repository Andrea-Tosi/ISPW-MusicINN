package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.PublishAnnouncementController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.technical_rider_bean.AnnouncementBean;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PublishAnnouncementControllerGUI implements Initializable {
    @FXML
    private Button profileManagementButton;

    @FXML
    private Button riderManagementButton;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private ComboBox<String> hoursStartTime;

    @FXML
    private ComboBox<String> minutesStartTime;

    @FXML
    private ComboBox<String> hoursDuration;

    @FXML
    private ComboBox<String> minutesDuration;

    @FXML
    private TextField cachetField;

    @FXML
    private TextField depositField;

    @FXML
    private FlowPane containerGenres;

//    @FXML
//    private ToggleButton rockToggle;
//
//    @FXML
//    private ToggleButton popToggle;
//
//    @FXML
//    private ToggleButton jazzToggle;
//
//    @FXML
//    private ToggleButton rapToggle;
//
//    @FXML
//    private ToggleButton trapToggle;
//
//    @FXML
//    private ToggleButton reggaeToggle;
//
//    @FXML
//    private ToggleButton classicalToggle;
//
//    @FXML
//    private ToggleButton metalToggle;
//
//    @FXML
//    private ToggleButton indieToggle;
//
//    @FXML
//    private ToggleButton soulToggle;
//
//    @FXML
//    private ToggleButton ReBToggle;
//
//    @FXML
//    private ToggleButton funkToggle;
//
//    @FXML
//    private ToggleButton discoToggle;
//
//    @FXML
//    private ToggleButton technoToggle;
//
//    @FXML
//    private ToggleButton electronicToggle;
//
//    @FXML
//    private ToggleButton ambientToggle;

    @FXML
    private FlowPane containerTypeArtist;

//    @FXML
//    private ToggleButton singerToggle;
//
//    @FXML
//    private ToggleButton bandToggle;
//
//    @FXML
//    private ToggleButton groupToggle;
//
//    @FXML
//    private ToggleButton DJToggle;

    @FXML
    private CheckBox doesUnreleasedCheck;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button publishAnnouncementButton;

    @FXML
    private HeaderControllerGUI headerController;

    private final static String DESCRIPTION_PAGE = "Pubblica Annuncio";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());

        setupTimeComboBox();
        setupGenresList();
        setupTypesArtistList();

        // Impedisce l'inserimento di caratteri non numerici nei text field di cachet e cauzione
        cachetField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([\\.,]\\d{0,2})?")) {
                cachetField.setText(oldValue);
            }
        });
        depositField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([\\.,]\\d{0,2})?")) {
                depositField.setText(oldValue);
            }
        });
    }

    private void setupTimeComboBox() {
        // Popola le Ore (00-23)
        for (int i = 0; i < 24; i++) {
            // String.format("%02d", i) aggiunge uno zero davanti se il numero è a una cifra
            hoursStartTime.getItems().add(String.format("%02d", i));
            hoursDuration.getItems().add(String.format("%02d", i));
        }

        // Popola i Minuti (00-59)
        for (int i = 0; i < 60; i++) {
            minutesStartTime.getItems().add(String.format("%02d", i));
            minutesDuration.getItems().add(String.format("%02d", i));
        }
    }

    public void setupGenresList() {
        for (MusicalGenre genre : MusicalGenre.values()) {
            ToggleButton btn = new ToggleButton(genre.toString());

            // Collega l'oggetto Enum al bottone
            btn.setUserData(genre);

            // Applichiamo stile (opzionale, puoi usare CSS)
//            btn.setMinWidth(80);

            containerGenres.getChildren().add(btn);
        }
    }

    public List<MusicalGenre> getSelectedGenres() {
        List<MusicalGenre> selected = containerGenres.getChildren().stream()
                // Filtriamo solo i nodi che sono ToggleButton
                .filter(ToggleButton.class::isInstance)
                .map(ToggleButton.class::cast)
                // Prendiamo solo quelli selezionati dall'utente
                .filter(ToggleButton::isSelected)
                // Estraiamo l'oggetto Enum che abbiamo salvato nel userData
                .map(btn -> (MusicalGenre) btn.getUserData())
                .toList(); // Restituisce la lista di Enum

        if (selected.isEmpty()) return Arrays.asList(MusicalGenre.values());
        else return selected;
    }

    public void setupTypesArtistList() {
        for (TypeArtist typeArtist : TypeArtist.values()) {
            ToggleButton btn = new ToggleButton(typeArtist.toString());

            // Collega l'oggetto Enum al bottone
            btn.setUserData(typeArtist);

            // Applichiamo stile (opzionale, puoi usare CSS)
//            btn.setMinWidth(80);

            containerTypeArtist.getChildren().add(btn);
        }
    }

    public List<TypeArtist> getSelectedTypesArtist() {
        List<TypeArtist> selected = containerTypeArtist.getChildren().stream()
                // Filtriamo solo i nodi che sono ToggleButton
                .filter(ToggleButton.class::isInstance)
                .map(ToggleButton.class::cast)
                // Prendiamo solo quelli selezionati dall'utente
                .filter(ToggleButton::isSelected)
                // Estraiamo l'oggetto Enum che abbiamo salvato nel userData
                .map(btn -> (TypeArtist) btn.getUserData())
                .toList(); // Restituisce la lista di Enum

        if (selected.isEmpty()) return Arrays.asList(TypeArtist.values());
        else return selected;
    }

    @FXML
    public void handleRiderManagementButton(ActionEvent event) {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.management_technical_rider.view");
        Scene currentScene = riderManagementButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }

    @FXML
    private void handleBackButton() {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.manager.home");

        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handlePublishAnnouncementButton() {
        // 1. Recupero dei dati semplici
        LocalDate startingDate = eventDatePicker.getValue();
        String hoursTimeString = hoursStartTime.getValue();
        String minutesTimeString = minutesStartTime.getValue();
        String hoursDurationString = hoursDuration.getValue();
        String minutesDurationString = minutesDuration.getValue();
        Double cachet = toDouble(cachetField.getText());
        Double deposit = toDouble(depositField.getText());

        // 2. Recupero dei Generi (Lista di Enum)
        List<MusicalGenre> selectedGenres = getSelectedGenres();
        List<TypeArtist> selectedTypes = getSelectedTypesArtist();

        // 3. Validazione formale (Esempio rapido)
        if (hoursTimeString == null || minutesTimeString == null || hoursDurationString == null || minutesDurationString == null) {
            statusLabel.setText("Riempi tutti i campi");
            return;
        }
        if (startingDate == null) {
            statusLabel.setText("Scegli una data");
            return;
        }
        if (startingDate.isBefore(LocalDate.now())) {
            statusLabel.setText("Non puoi inserire una data precedente a quella odierna");
            return;
        }
        if (cachet == null) {
            statusLabel.setText("Il cachet deve essere un numero valido (es. 150.00)");
            return;
        }

        int hoursTime = Integer.parseInt(hoursTimeString);
        int minutesTime = Integer.parseInt(minutesTimeString);
        int hoursDuration = Integer.parseInt(hoursDurationString);
        int minutesDuration = Integer.parseInt(minutesDurationString);
        LocalTime startingTime = LocalTime.of(hoursTime, minutesTime);
        Duration eventDuration = Duration.ofHours(hoursDuration).plusMinutes(minutesDuration);

        Boolean doesUnreleased = doesUnreleasedCheck.isIndeterminate() ? null : doesUnreleasedCheck.isSelected();
        String description = descriptionArea.getText();

        // 4. Popolamento del Bean
        AnnouncementBean bean = new AnnouncementBean();
        bean.setStartingDate(startingDate);
        bean.setStartingTime(startingTime);
        bean.setDuration(eventDuration);
        bean.setCachet(cachet);
        bean.setDeposit(deposit);
        bean.setRequestedGenres(selectedGenres);
        bean.setRequestedTypesArtist(selectedTypes);
        bean.setDoesUnreleased(doesUnreleased);
        bean.setDescription(description);

        // 5. Invio al Controller Applicativo
        try {
            PublishAnnouncementController controller = new PublishAnnouncementController();
            controller.publish(bean);
            statusLabel.setTextFill(Color.BLACK);
            statusLabel.setText("Annuncio pubblicato con successo!");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Errore durante la pubblicazione: " + e.getMessage());
        }
    }

    private Double toDouble(String input) {
        try {
            // Rimpiazza la virgola con il punto per compatibilità Double
            String formatted = input.replace(",", ".");
            double valore = Double.parseDouble(formatted);

            if (valore < 0) return null; // Non accettiamo cachet negativi

            // Arrotondamento a 2 cifre decimali
            return Math.round(valore * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            return null; // Input non valido
        }
    }
}
