package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.util.EmailValidator;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.bean.login_bean.CredentialsBean;
import org.musicinn.musicinn.util.exceptions.EmailAlreadyUsedException;
import org.musicinn.musicinn.util.exceptions.UsernameAlreadyUsedException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserRegistrationControllerGUI implements Initializable {
    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField clearPasswordField;

    @FXML
    private ToggleButton visibilityToggleButton1;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private TextField clearRepeatPasswordField;

    @FXML
    private ToggleButton visibilityToggleButton2;

    @FXML
    private Button confirmButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nasconde inizialmente i campi di testo normali
        clearPasswordField.setVisible(false);
        clearRepeatPasswordField.setVisible(false);

        // Assicura che i campi siano sempre sincronizzati
        // Quando si scrive in passwordField, copia il testo in clearPasswordField
        passwordField.textProperty().addListener((obs, oldText, newText) -> clearPasswordField.setText(newText));
        repeatPasswordField.textProperty().addListener((obs, oldText, newText) -> clearRepeatPasswordField.setText(newText));

        // Quando si scrive in passwordTextField, copia il testo in passwordField
        clearPasswordField.textProperty().addListener((obs, oldText, newText) -> passwordField.setText(newText));
        clearRepeatPasswordField.textProperty().addListener((obs, oldText, newText) -> repeatPasswordField.setText(newText));
    }

    @FXML
    protected void changePasswordVisibility(ActionEvent event) {
        boolean nextStateIsHidden = !passwordField.isVisible();
        passwordField.setVisible(nextStateIsHidden);
        clearPasswordField.setVisible(!nextStateIsHidden);
        repeatPasswordField.setVisible(nextStateIsHidden);
        clearRepeatPasswordField.setVisible(!nextStateIsHidden);
        String buttonText = nextStateIsHidden ? "Mostra" : "Nascondi";
        visibilityToggleButton1.setText(buttonText);
        visibilityToggleButton2.setText(buttonText);
    }

    @FXML
    protected void onActionLoginLink(ActionEvent event){
        Scene currentScene = statusLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.login.view");

        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    protected void onActionUsername(ActionEvent event) {
        emailTextField.requestFocus();
    }

    @FXML
    protected void onActionEmail(ActionEvent event) {
        passwordField.requestFocus();
    }

    @FXML
    protected void onActionPassword(ActionEvent event) {
        repeatPasswordField.requestFocus();
    }

    @FXML
    protected void onActionClearPassword(ActionEvent event) {
        confirmButton.fire();
    }

    @FXML
    protected void handleRegistration() {
        statusLabel.setText("");

        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            statusLabel.setText("Riempi tutti i campi.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            statusLabel.setText("Le password immesse non coincidono.");
            return;
        }

        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.isEmailFormatValid(email)) {
            statusLabel.setText("L'email immessa non segue il formato standard");
            return;
        }

        CredentialsBean credentialsBean = new CredentialsBean(username, email, password);
        LoginController loginController = LoginController.getSingletonInstance();
        try {
            loginController.startSignup(credentialsBean);
            statusLabel.setText("Attendere l'arrivo della mail per la verifica dell'indirizzo email fornito (potrebbe volerci un po')");

            //crea una finestra modale dove l'utente deve inserire il codice a 6 cifre mandato all'email per verificarla
            showVerificationModalStage(email);
        } catch (UsernameAlreadyUsedException | EmailAlreadyUsedException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    private void showVerificationModalStage(String email) {
        try {
            String nextFxmlPath = FxmlPathLoader.getPath("fxml.verification_email.view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nextFxmlPath));
            Stage verificationStage = new Stage();
            verificationStage.setTitle("Inserisci codice di verifica");
            verificationStage.setScene(new Scene(loader.load()));

            //passaggio parametro a nuovo controller GUI
            VerificationModalStageGUI controllerSecondario = loader.getController();
            controllerSecondario.setEmail(email);

            Scene currentScene = statusLabel.getScene();
            Stage primaryStage = (Stage) currentScene.getWindow();

            // Indica che la nuova finestra blocca l'interazione con il parent
            verificationStage.initModality(Modality.WINDOW_MODAL);
            // Collega la modale allo Stage principale ( primaryStage )
            verificationStage.initOwner(primaryStage);

            verificationStage.setOnHidden(event ->
                // Se l'utente chiude la finestra, dovrà richiedere il rinvio della mail di verifica
                LoginController.getSingletonInstance().invalidateVerificationCode(email)
            );

            verificationStage.showAndWait();

            if (controllerSecondario.getCheck()) {
                nextFxmlPath = FxmlPathLoader.getPath("fxml.choose_account_type.view");
                NavigationGUI.navigateToPath(primaryStage, nextFxmlPath);
            } else {
                statusLabel.setText("Il codice fornito è errato. Email non verificata. Clicca su 'Conferma' per riprovare la verifica.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
