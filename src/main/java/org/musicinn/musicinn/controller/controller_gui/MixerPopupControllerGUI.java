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
import org.musicinn.musicinn.util.technical_rider_bean.MixerBean;

import java.net.URL;
import java.util.ResourceBundle;

public class MixerPopupControllerGUI implements Initializable {
    @FXML
    private Spinner<Integer> inputChannelsField;

    @FXML
    private Spinner<Integer> auxSendsField;

    @FXML
    private CheckBox digitalCheck;

    @FXML
    private CheckBox phantomCheck;

    @FXML
    private Button addButton;

    private MixerBean createdMixer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Crea la fabbrica (minimo 0, massimo 100, valore iniziale 1)
        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);

        // Assegna la fabbrica allo spinner
        inputChannelsField.setValueFactory(valueFactory1);
        auxSendsField.setValueFactory(valueFactory2);
    }

    @FXML
    private void handleAddMixer(ActionEvent event) {
        int inputChannels = inputChannelsField.getValue();
        int auxSends = auxSendsField.getValue();

        // Creazione dell'oggetto basata sui dati inseriti
        createdMixer = new MixerBean(inputChannels, auxSends, digitalCheck.isSelected(), phantomCheck.isSelected());

        // Chiusura della finestra modale
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public MixerBean getCreatedMixer() {
        return createdMixer;
    }
}
