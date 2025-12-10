package org.musicinn.musicinn.controller.controllerGUI;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controllerApplication.LoginController;
import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.util.LoginBean.UserLoginBean;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginControllerGUI implements Initializable {
    @FXML
    TextField identityTextField;

    @FXML
    PasswordField passwordField;

    @FXML
    TextField clearPasswordField;

    @FXML
    ToggleButton visibilityToggleButton;

    @FXML
    Button confermaButton;

    @FXML
    Hyperlink passwordDimenticataLink;

    @FXML
    Hyperlink registratiLink;

    @FXML
    Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nascondi inizialmente il campo di testo normale
        clearPasswordField.setVisible(false);

        // Assicura che i campi siano sempre sincronizzati
        // Quando si scrive in passwordField, copiamo il testo in clearPasswordField
        passwordField.textProperty().addListener((obs, oldText, newText) -> {
            clearPasswordField.setText(newText);
        });

        // Quando si scrive in passwordTextField, copiamo il testo in passwordField
        clearPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            passwordField.setText(newText);
        });
    }

    @FXML
    protected void changePasswordVisibility(ActionEvent event) {
        // Controlla quale campo è attualmente visibile
        if (passwordField.isVisible()) {
            // Se il PasswordField è visibile:
            passwordField.setVisible(false);
            clearPasswordField.setVisible(true);
            visibilityToggleButton.setText("Nascondi");
        } else {
            // Se il TextField è visibile (cioè la password è mostrata)
            passwordField.setVisible(true);
            clearPasswordField.setVisible(false);
            visibilityToggleButton.setText("Mostra");
        }
    }

    @FXML
    protected void onActionUsername(ActionEvent event) {
        passwordField.requestFocus();
    }

    @FXML
    protected void onActionPassword(ActionEvent event) {
        confermaButton.fire();
    }

    @FXML
    protected void handleLogin(ActionEvent event){
        statusLabel.setText("");

        String username = identityTextField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Inserisci username e password.");
            return;
        }

        UserLoginBean userBean = new UserLoginBean(username, password);
        LoginController loginController = new LoginController();
        User loggedUser = loginController.login(userBean);
        if (loggedUser != null) {
            //controlla il tipo di utente e mostra la schermata adeguata
            handleSuccessfulLogin(loggedUser);
        } else {
            statusLabel.setText("Credenziali non valide.");
        }
    }

    private void handleSuccessfulLogin(User user){
        String nextFxmlPath = new String();

        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();

        if (user instanceof Manager) {
            //Manager manager = (Manager) user;
            nextFxmlPath = "/org/musicinn/musicinn/2.0.2_HomePage_Manager.fxml";
        } else if (user instanceof Artist) {
            //Artist artist = (Artist) user;
            nextFxmlPath = "/org/musicinn/musicinn/2.0.1_HomePage_Artist.fxml";
        }
        navigateToHomepage(stage, nextFxmlPath);
    }

    private void navigateToHomepage(Stage currentStage, String fxmlPath){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento del file FXML: " + fxmlPath);
        }
    }
}