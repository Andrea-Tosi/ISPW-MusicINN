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
import org.musicinn.musicinn.util.bean.technical_rider_bean.MicStandSetBean;

import java.net.URL;
import java.util.ResourceBundle;

public class MicStandSetPopupControllerGUI implements Initializable {
    @FXML
    private Spinner<Integer> quantityField;

    @FXML
    private CheckBox heightCheck;

    @FXML
    private Button addButton;

    private MicStandSetBean createdMicStandSet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Crea la fabbrica (minimo 0, massimo 100, valore iniziale 1)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);

        // Assegna la fabbrica allo spinner
        quantityField.setValueFactory(valueFactory);
    }

    @FXML
    private void handleAddMicStandSet(ActionEvent event) {
        int quantity = quantityField.getValue();

        // Creazione dell'oggetto basata sui dati inseriti
        createdMicStandSet = new MicStandSetBean(quantity, heightCheck.isSelected());

        // Chiusura della finestra modale
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public MicStandSetBean getCreatedMicStandSet() {
        return createdMicStandSet;
    }
}
