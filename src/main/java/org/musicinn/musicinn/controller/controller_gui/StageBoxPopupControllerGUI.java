package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.technical_rider_bean.StageBoxBean;

import java.net.URL;
import java.util.ResourceBundle;

public class StageBoxPopupControllerGUI implements Initializable {
    @FXML
    private Spinner<Integer> inputChannelsField;

    @FXML
    private CheckBox digitalCheck;

    @FXML
    private Label statusLabel;

    @FXML
    private Button addButton;

    private StageBoxBean createdStageBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Crea la fabbrica (minimo 0, massimo 100, valore iniziale 1)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);

        // Assegna la fabbrica allo spinner
        inputChannelsField.setValueFactory(valueFactory);
    }

    @FXML
    private void handleAddStageBox(ActionEvent event) {
        int inputChannels = inputChannelsField.getValue();

        // Creazione dell'oggetto basata sui dati inseriti
        createdStageBox = new StageBoxBean(inputChannels, digitalCheck.isSelected());

        // Chiusura della finestra modale
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public StageBoxBean getCreatedStageBox() {
        return createdStageBox;
    }
}
