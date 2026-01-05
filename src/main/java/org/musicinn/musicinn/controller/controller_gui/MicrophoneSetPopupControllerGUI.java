package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.technical_rider_bean.MicrophoneSetBean;

import java.net.URL;
import java.util.ResourceBundle;

public class MicrophoneSetPopupControllerGUI implements Initializable {
    @FXML
    private Spinner<Integer> quantityField;

    @FXML
    private CheckBox phantomCheck;

    @FXML
    private Button addButton;

    private MicrophoneSetBean createdMicrophoneSet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Crea la fabbrica (minimo 0, massimo 100, valore iniziale 1)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);

        // Assegna la fabbrica allo spinner
        quantityField.setValueFactory(valueFactory);
    }

    @FXML
    private void handleAddMicrophoneSet(ActionEvent event) {
        int quantity = quantityField.getValue();

        // Creazione dell'oggetto basata sui dati inseriti
        createdMicrophoneSet = new MicrophoneSetBean(quantity, phantomCheck.isSelected());

        // Chiusura della finestra modale
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public MicrophoneSetBean getCreatedMicrophoneSet() {
        return createdMicrophoneSet;
    }
}
