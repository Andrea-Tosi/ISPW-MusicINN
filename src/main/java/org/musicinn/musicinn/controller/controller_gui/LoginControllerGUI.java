package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.bean.login_bean.CredentialsBean;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginControllerGUI implements Initializable {
    @FXML
    private TextField identityTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField clearPasswordField;

    @FXML
    private ToggleButton visibilityToggleButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Hyperlink forgottenPasswordLink;

    @FXML
    private Hyperlink signupLink;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nascondi inizialmente il campo di testo normale
        clearPasswordField.setVisible(false);

        // Assicura che i campi siano sempre sincronizzati
        // Quando si scrive in passwordField, copiamo il testo in clearPasswordField
        passwordField.textProperty().addListener((obs, oldText, newText) -> clearPasswordField.setText(newText));

        // Quando si scrive in passwordTextField, copiamo il testo in passwordField
        clearPasswordField.textProperty().addListener((obs, oldText, newText) -> passwordField.setText(newText));
    }

    @FXML
    protected void changePasswordVisibility(ActionEvent event) {
        boolean nextStateIsHidden = !passwordField.isVisible();
        passwordField.setVisible(nextStateIsHidden);
        clearPasswordField.setVisible(!nextStateIsHidden);
        String buttonText = nextStateIsHidden ? "Mostra" : "Nascondi";
        visibilityToggleButton.setText(buttonText);
    }

    @FXML
    protected void onActionSignupLink(ActionEvent event){
        String fxmlPath = "";

        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();

        fxmlPath = FxmlPathLoader.getPath("fxml.registration_user.view");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento del file FXML: " + fxmlPath);
        }
    }

    @FXML
    protected void onActionUsername(ActionEvent event) {
        passwordField.requestFocus();
    }

    @FXML
    protected void onActionPassword(ActionEvent event) {
        confirmButton.fire();
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

        CredentialsBean userBean = new CredentialsBean(username, password);
        User loggedUser = LoginController.getSingletonInstance().login(userBean);
        if (loggedUser != null) {
            //controlla il tipo di utente e mostra la schermata adeguata
            handleSuccessfulLogin(loggedUser);
        } else {
            statusLabel.setText("Credenziali non valide.");
        }
    }

    private void handleSuccessfulLogin(User user){
        String nextFxmlPath = "";

        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();

        if (user instanceof Manager) {
            nextFxmlPath = FxmlPathLoader.getPath("fxml.manager.home");
        } else if (user instanceof Artist) {
            nextFxmlPath = FxmlPathLoader.getPath("fxml.artist.home");
        }
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }
}
