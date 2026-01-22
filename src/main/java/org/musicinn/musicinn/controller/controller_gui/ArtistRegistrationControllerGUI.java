package org.musicinn.musicinn.controller.controller_gui;

import com.stripe.exception.StripeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.bean.login_bean.ArtistRegistrationBean;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ArtistRegistrationControllerGUI implements Initializable {
    @FXML
    private TextField stageNameField;

    @FXML
    private ComboBox<TypeArtist> typeArtistBox;

    @FXML
    private CheckBox doesUnreleasedCheck;

    @FXML
    private TextField cityField;

    @FXML
    private TextField addressField;

    @FXML
    private ToggleButton rockToggle;

    @FXML
    private ToggleButton popToggle;

    @FXML
    private ToggleButton jazzToggle;

    @FXML
    private ToggleButton rapToggle;

    @FXML
    private ToggleButton trapToggle;

    @FXML
    private ToggleButton reggaeToggle;

    @FXML
    private ToggleButton classicalToggle;

    @FXML
    private ToggleButton metalToggle;

    @FXML
    private ToggleButton indieToggle;

    @FXML
    private ToggleButton soulToggle;

    @FXML
    private ToggleButton rbToggle;

    @FXML
    private ToggleButton funkToggle;

    @FXML
    private ToggleButton discoToggle;

    @FXML
    private ToggleButton technoToggle;

    @FXML
    private ToggleButton electronicToggle;

    @FXML
    private ToggleButton ambientToggle;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button completeRegistrationButton;

    private List<ToggleButton> buttons;
    private static final int MIN_SELECTIONS = 1;
    private static final int MAX_SELECTIONS = 4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Popola la ComboBox
        ObservableList<TypeArtist> items = FXCollections.observableArrayList(
                TypeArtist.SINGER,
                TypeArtist.BAND,
                TypeArtist.GROUP,
                TypeArtist.DJ,
                TypeArtist.MUSICIAN
        );
        typeArtistBox.setItems(items);

        // Collega ogni pulsante alla sua costante Enum
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
                 rbToggle,
                funkToggle,
                discoToggle,
                technoToggle,
                electronicToggle,
                ambientToggle
        );
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
        rbToggle.setUserData(MusicalGenre.R_B);
        funkToggle.setUserData(MusicalGenre.FUNK);
        discoToggle.setUserData(MusicalGenre.DISCO);
        technoToggle.setUserData(MusicalGenre.TECHNO);
        electronicToggle.setUserData(MusicalGenre.ELECTRONIC);
        ambientToggle.setUserData(MusicalGenre.AMBIENT);

        for (ToggleButton button : buttons) {
            button.selectedProperty().addListener((obs, oldVal, newVal) ->
                updateButtonsState()
            );
        }
    }

    private void updateButtonsState() {
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
        String fxmlPath = FxmlPathLoader.getPath("fxml.choose_account_type.view");

        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    private List<MusicalGenre> getSelectedGenres() {
        return buttons.stream().filter(ToggleButton::isSelected).map(btn -> (MusicalGenre) btn.getUserData()).toList();
    }

    @FXML
    public void handleConfirmButton() {
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

        try {
            LoginController loginController = LoginController.getSingletonInstance();
            loginController.completeSignup(artistRegistrationBean);

            Scene currentScene = statusLabel.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            String fxmlPath = FxmlPathLoader.getPath("fxml.artist.home");

            NavigationGUI.navigateToPath(stage, fxmlPath);
        } catch (StripeException e) {
            e.printStackTrace();
            statusLabel.setText("C'è stato un problema con il servizio di pagamento per la creazione dell'account");
        }
    }
}