package org.musicinn.musicinn.controller.controller_gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.LoginController;

import java.net.URL;
import java.util.ResourceBundle;

public class VerificationModalStageGUI implements Initializable {
    @FXML
    private TextField digit1;

    @FXML
    private TextField digit2;

    @FXML
    private TextField digit3;

    @FXML
    private TextField digit4;

    @FXML
    private TextField digit5;

    @FXML
    private TextField digit6;

    @FXML
    private Label statusLabel;

    @FXML
    private Button confirmButton;

    private String email;
    private boolean check = false;
    private TextField[] fields;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fields = new TextField[]{digit1, digit2, digit3, digit4, digit5, digit6};

        for (int i = 0; i < fields.length; i++) {
            configTextField(i);
        }

        disableConfirmButtonUntilFilled();
    }

    private void configTextField(int currentIndex) {
        fields[currentIndex].textProperty().addListener((obs, oldVal, newVal) -> {
            // Permette solo un carattere
            if (newVal.length() > 1) {
                fields[currentIndex].setText(newVal.substring(0, 1));
            }

            // Auto-focus al prossimo campo se utente scrive qualcosa
            if (newVal.length() == 1 && currentIndex < fields.length - 1) {
                fields[currentIndex + 1].requestFocus();
            }

            // I campi accettano solo numeri
            if (!newVal.matches("\\d*")) {
                fields[currentIndex].setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        configOnBackspacePressed(currentIndex);
    }

    private void configOnBackspacePressed(int currentIndex) {
        // Gestione Backspace: se utente cancella, torna al campo precedente
        fields[currentIndex].setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE &&
                    fields[currentIndex].getText().isEmpty() &&
                    currentIndex > 0) {
                fields[currentIndex - 1].requestFocus();
            }
        });
    }

    public void handleLastDigit(ActionEvent event) {
        TextField lastField = (TextField) event.getSource();
        if (!lastField.getText().isEmpty()) {
            confirmButton.fire();
        }
    }

    private void disableConfirmButtonUntilFilled() {
        // Disabilita il bottone Conferma finché non vengono riempiti tutti i quadratini
        BooleanBinding inserimentoIncompleto = Bindings.createBooleanBinding(() ->
                digit1.getText().trim().isEmpty() ||
                digit2.getText().trim().isEmpty() ||
                digit3.getText().trim().isEmpty() ||
                digit4.getText().trim().isEmpty() ||
                digit5.getText().trim().isEmpty() ||
                digit6.getText().trim().isEmpty(),
                // Gli argomenti successivi dicono a JavaFX di ricalcolare tale condizione ogni volta che il testo di uno di questi campi cambia
                digit1.textProperty(),
                digit2.textProperty(),
                digit3.textProperty(),
                digit4.textProperty(),
                digit5.textProperty(),
                digit6.textProperty()
        );
        // Lega la proprietà 'disable' del bottone al binding
        confirmButton.disableProperty().bind(inserimentoIncompleto);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @FXML
    private void handleConfirm() {
        // Costruisce la stringa del codice inserito
        StringBuilder sb = new StringBuilder();
        for (TextField f : fields) {
            sb.append(f.getText());
        }
        String enteredCode = sb.toString();

        // 3. Validazione
        if (enteredCode.length() < 6) {
            statusLabel.setText("Inserisci tutte e 6 le cifre del codice di verifica.");
            return;
        }

        setCheck(LoginController.getSingletonInstance().checkEnteredCode(getEmail(), enteredCode));
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}
