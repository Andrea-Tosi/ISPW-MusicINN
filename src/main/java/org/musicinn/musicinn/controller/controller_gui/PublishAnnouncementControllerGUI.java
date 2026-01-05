package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PublishAnnouncementControllerGUI implements Initializable {
    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private ComboBox<Integer> hoursStartTime;

    @FXML
    private ComboBox<Integer> minutesStartTime;

    @FXML
    private ComboBox<Integer> hoursDuration;

    @FXML
    private ComboBox<Integer> minutesDuration;

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
    private Button backButton;

    @FXML
    private Button publishAnnouncementButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGenresList();
        setupTypesArtistList();
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
        return containerGenres.getChildren().stream()
                // Filtriamo solo i nodi che sono ToggleButton
                .filter(node -> node instanceof ToggleButton)
                .map(node -> (ToggleButton) node)
                // Prendiamo solo quelli selezionati dall'utente
                .filter(ToggleButton::isSelected)
                // Estraiamo l'oggetto Enum che abbiamo salvato nel userData
                .map(btn -> (MusicalGenre) btn.getUserData())
                .toList(); // Restituisce la lista di Enum
    }

    public void setupTypesArtistList() {
        for (TypeArtist typeArtist : TypeArtist.values()) {
            ToggleButton btn = new ToggleButton(typeArtist.toString());

            // Collega l'oggetto Enum al bottone
            btn.setUserData(typeArtist);

            // Applichiamo stile (opzionale, puoi usare CSS)
//            btn.setMinWidth(80);

            containerGenres.getChildren().add(btn);
        }
    }

    public List<TypeArtist> getSelectedTypesArtist() {
        return containerGenres.getChildren().stream()
                // Filtriamo solo i nodi che sono ToggleButton
                .filter(node -> node instanceof ToggleButton)
                .map(node -> (ToggleButton) node)
                // Prendiamo solo quelli selezionati dall'utente
                .filter(ToggleButton::isSelected)
                // Estraiamo l'oggetto Enum che abbiamo salvato nel userData
                .map(btn -> (TypeArtist) btn.getUserData())
                .toList(); // Restituisce la lista di Enum
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

    }
}
