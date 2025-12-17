package org.musicinn.musicinn.controller.controller_gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.login_bean.ArtistRegistrationBean;
import org.musicinn.musicinn.util.login_bean.CredentialsBean;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ArtistRegistrationControllerGUI implements Initializable {
    @FXML
    public TextField stageNameField;

    @FXML
    public ComboBox<TypeArtist> typeArtistBox;

    @FXML
    public CheckBox doesUnreleasedCheck;

    @FXML
    public TextField cityField;

    @FXML
    public TextField addressField;

    @FXML
    public ToggleButton rockToggle;

    @FXML
    public ToggleButton popToggle;

    @FXML
    public ToggleButton jazzToggle;

    @FXML
    public ToggleButton rapToggle;

    @FXML
    public ToggleButton trapToggle;

    @FXML
    public ToggleButton reggaeToggle;

    @FXML
    public ToggleButton classicalToggle;

    @FXML
    public ToggleButton metalToggle;

    @FXML
    public ToggleButton indieToggle;

    @FXML
    public ToggleButton soulToggle;

    @FXML
    public ToggleButton r_bToggle;

    @FXML
    public ToggleButton funkToggle;

    @FXML
    public ToggleButton discoToggle;

    @FXML
    public ToggleButton technoToggle;

    @FXML
    public ToggleButton electronicToggle;

    @FXML
    public ToggleButton ambientToggle;

    @FXML
    public Label statusLabel;

    @FXML
    public Button backButton;

    @FXML
    public Button completeRegistrationButton;

    private List<ToggleButton> buttons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<TypeArtist> items = FXCollections.observableArrayList(
                TypeArtist.SINGER,
                TypeArtist.BAND,
                TypeArtist.GROUP,
                TypeArtist.DJ,
                TypeArtist.MUSICIAN
        );

        typeArtistBox.setItems(items);

         buttons = Arrays.asList(
                rockToggle,
                popToggle,
                jazzToggle,
                rapToggle,
                trapToggle,
                reggaeToggle,
                classicalToggle,
                metalToggle,
                indieToggle,
                soulToggle,
                r_bToggle,
                funkToggle,
                discoToggle,
                technoToggle,
                electronicToggle,
                ambientToggle
        );
        // Collega ogni pulsante alla sua costante Enum
        rockToggle.setUserData(MusicalGenre.ROCK);
        popToggle.setUserData(MusicalGenre.POP);
        jazzToggle.setUserData(MusicalGenre.JAZZ);
        rapToggle.setUserData(MusicalGenre.RAP);
        trapToggle.setUserData(MusicalGenre.TRAP);
        reggaeToggle.setUserData(MusicalGenre.REGGAE);
        classicalToggle.setUserData(MusicalGenre.CLASSICAL);
        metalToggle.setUserData(MusicalGenre.METAL);
        indieToggle.setUserData(MusicalGenre.INDIE);
        soulToggle.setUserData(MusicalGenre.SOUL);
        r_bToggle.setUserData(MusicalGenre.R_B);
        funkToggle.setUserData(MusicalGenre.FUNK);
        discoToggle.setUserData(MusicalGenre.DISCO);
        technoToggle.setUserData(MusicalGenre.TECHNO);
        electronicToggle.setUserData(MusicalGenre.ELECTRONIC);
        ambientToggle.setUserData(MusicalGenre.AMBIENT);

        for (ToggleButton button : buttons) {
            button.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updateButtonsState();
            });
        }
    }

    private void updateButtonsState() {
        int MAX_SELECTIONS = 4;
        long count = buttons.stream().filter(ToggleButton::isSelected).count();

        for (ToggleButton button : buttons) {
            // Se abbiamo raggiunto il massimo, disabilita i pulsanti NON ancora selezionati
            if (count >= MAX_SELECTIONS) {
                if (!button.isSelected()) {
                    button.setDisable(true);
                }
            } else {
                // Altrimenti, abilita tutto
                button.setDisable(false);
            }
        }
    }

    @FXML
    public void handleBackButton() {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.registration_user.view");

        Navigation.navigateToPath(stage, fxmlPath);
    }

    private List<MusicalGenre> getSelectedGenres() {
        return buttons.stream().filter(ToggleButton::isSelected).map(btn -> (MusicalGenre) btn.getUserData()).toList();
    }

    @FXML
    public void handleConfirmButton() {
        int MIN_SELECTIONS = 1;

        statusLabel.setText("");

        String stageName = stageNameField.getText();
        TypeArtist typeArtist = typeArtistBox.getValue();
        Boolean doesUnreleased = doesUnreleasedCheck.isSelected();
        String city = cityField.getText();
        String address = addressField.getText();
        List<MusicalGenre> genreList = getSelectedGenres();

        if (stageName.isEmpty() || typeArtist == null || city.isEmpty() || address.isEmpty()) {
            statusLabel.setText("Riempi tutti i campi.");
            return;
        }

        if (genreList.size() < MIN_SELECTIONS) {
            statusLabel.setText("Le password immesse non coincidono.");
            return;
        }

        ArtistRegistrationBean artistRegistrationBean = new ArtistRegistrationBean(stageName, typeArtist, doesUnreleased, city, address);
        artistRegistrationBean.setGenresList(genreList);

        LoginController loginController = LoginController.getSingletonInstance();
        loginController.completeSignup(artistRegistrationBean);

        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.artist.home");

        Navigation.navigateToPath(stage, fxmlPath);
    }
}
//TODO: correttezza formato https