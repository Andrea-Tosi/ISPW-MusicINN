package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.enumerations.CableFunction;
import org.musicinn.musicinn.util.bean.technical_rider_bean.CableSetBean;

import java.net.URL;
import java.util.ResourceBundle;

public class CableSetPopupControllerGUI implements Initializable {
    @FXML
    private Spinner<Integer> quantityField;

    @FXML
    private ComboBox<CableFunction> functionComboBox;

    @FXML
    private Button addButton;

    private CableSetBean createdCableSet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Aggiunge tutti i valori dell'enum CableFunction alla ComboBox
        functionComboBox.getItems().addAll(CableFunction.values());

        // Crea la fabbrica (minimo 0, massimo 100, valore iniziale 1)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);

        // Assegna la fabbrica allo spinner
        quantityField.setValueFactory(valueFactory);
    }

    @FXML
    private void handleAddCableSet(ActionEvent event) {
        int quantity = quantityField.getValue();

        // Creazione dell'oggetto basata sui dati inseriti
        createdCableSet = new CableSetBean(quantity, functionComboBox.getValue());

        // Chiusura della finestra modale
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public CableSetBean getCreatedCableSet() {
        return createdCableSet;
    }
}
