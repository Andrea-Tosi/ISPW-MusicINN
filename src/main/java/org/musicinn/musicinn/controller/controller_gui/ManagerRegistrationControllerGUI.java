package org.musicinn.musicinn.controller.controller_gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.login_bean.ManagerRegistrationBean;

import java.net.URL;
import java.util.ResourceBundle;

public class ManagerRegistrationControllerGUI implements Initializable {
    @FXML
    private TextField venueNameField;

    @FXML
    private TextField venueCityField;

    @FXML
    private TextField venueAddressField;

    @FXML
    private ComboBox<TypeVenue> typeVenueBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button completeRegistrationButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Popola la ComboBox
        ObservableList<TypeVenue> items = FXCollections.observableArrayList(
                TypeVenue.AUDITORIUM,
                TypeVenue.BAR,
                TypeVenue.CLUB,
                TypeVenue.DISCO,
                TypeVenue.PUB,
                TypeVenue.RESTAURANT,
                TypeVenue.THEATRE
        );
        typeVenueBox.setItems(items);
    }

    @FXML
    public void handleBackButton() {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.choose_account_type.view");

        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    public void handleConfirmButton() {
        statusLabel.setText("");

        String venueName = venueNameField.getText();
        String venueCity = venueCityField.getText();
        String venueAddress = venueAddressField.getText();
        TypeVenue typeVenue = typeVenueBox.getValue();

        if (venueName.isEmpty() || venueCity.isEmpty() || venueAddress.isEmpty() || typeVenue == null) {
            statusLabel.setText("Riempi tutti i campi.");
            return;
        }

        ManagerRegistrationBean managerRegistrationBean = new ManagerRegistrationBean(venueName, venueCity, venueAddress, typeVenue);

        LoginController loginController = LoginController.getSingletonInstance();
        loginController.completeSignup(managerRegistrationBean);

        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.manager.home");

        NavigationGUI.navigateToPath(stage, fxmlPath);
    }
}
