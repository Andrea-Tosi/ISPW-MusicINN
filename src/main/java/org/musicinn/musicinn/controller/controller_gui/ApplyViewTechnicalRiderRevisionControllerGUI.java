package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.ApplyController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ApplyViewTechnicalRiderRevisionControllerGUI implements Initializable {
    @FXML
    private Label stageDimensionsLabel;

    @FXML
    private VBox requestedEquipmentsBox;

    @FXML
    private Label riderHeaderLabel;

    @FXML
    private Label riderLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button ModifyRiderButton;

    @FXML
    private Button continueButton;

    @FXML
    private HeaderControllerGUI headerController;

    private static final String DESCRIPTION_PAGE = "Candidati";
    private static final Session.UserRole ROLE  = Session.getSingletonInstance().getRole();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());

        TechnicalRiderBean trBean = new TechnicalRiderBean();
        ApplyController controller = new ApplyController();
        if (ROLE.equals(Session.UserRole.ARTIST)) riderHeaderLabel.setText("Attrezzatura richiesta");
        else if (ROLE.equals(Session.UserRole.MANAGER)) riderHeaderLabel.setText("Attrezzatura a disposizione");
        controller.getEquipmentBeans(trBean);
        setupStageDimensionsLabel(trBean);
        riderLabel.setText(TechnicalRiderFormatter.format(trBean, ROLE));
    }

    private void setupStageDimensionsLabel(TechnicalRiderBean trBean) {
        int length = trBean.getMinLengthStage();
        int width = trBean.getMinWidthStage();
        String stageDimensions = String.format("%dm x %dm", length, width);
        stageDimensionsLabel.setText(stageDimensions);
    }

    public void setupTechnicalRiderLabel(TechnicalRiderBean trBean, Session.UserRole role) {
        StringBuilder riderString = new StringBuilder();
        getStringMixers(trBean, riderString, role);
        getStringStageBoxes(trBean, riderString);
        getStringMicrophones(trBean, riderString);
        getStringDIBoxes(trBean, riderString);
        getStringMonitors(trBean, riderString);
        getStringMicStands(trBean, riderString);
        getStringCables(trBean, riderString);
        riderLabel.setText(riderString.toString());
    }

    private void getStringMixers(TechnicalRiderBean trBean, StringBuilder riderString, Session.UserRole role) {
        if (role.equals(Session.UserRole.ARTIST)) {
            getStringMixersArtist(trBean, riderString);
        } else if (role.equals(Session.UserRole.MANAGER)) {
            getStringMixersManager(trBean, riderString);
        }
    }

    private void getStringMixersArtist(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (MixerBean m : trBean.getMixers()) {
            riderString.append(m.isFOH() ? "Foh" : "Stage")
                    .append(" Mixer: ")
                    .append(m.getInputChannels()).append(" canali input, ")
                    .append(m.getAuxSends()).append(" mandate aux");

            // Gestione nel caso di valori null: non verranno riportati nella stringa
            if (m.getDigital() != null) {
                String digitalStatus = m.getDigital() ? "Digitale" : "Analogico";
                riderString.append(", ").append(digitalStatus);
            }

            if (m.getHasPhantomPower() != null) {
                String phantomStatus = m.getHasPhantomPower() ? "consente phantom power" : "non consente phantom power";
                riderString.append(", ").append(phantomStatus);
            }

            riderString.append("\n");
        }
    }

    private void getStringMixersManager(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (MixerBean m : trBean.getMixers()) {
            riderString.append("Mixer: ").append(m.getInputChannels())
                    .append(" canali input, ").append(m.getAuxSends())
                    .append(" mandate aux, ")
                    .append(Boolean.TRUE.equals(m.getDigital()) ? "Digitale" : "Analogico")
                    .append(", ")
                    .append(Boolean.TRUE.equals(m.getHasPhantomPower()) ? "" : "non ")
                    .append("consente phantom power\n");
        }
    }

    private void getStringStageBoxes(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (StageBoxBean sb : trBean.getStageBoxes()) {
            riderString.append("Stage Box: ").append(sb.getInputChannels()).append(" canali input");
            if (sb.getDigital() != null) {
                String digitalString = sb.getDigital() ? "Digitale" : "Analogico";
                riderString.append(", ").append(digitalString);
            }
            riderString.append("\n");
        }
    }

    private void getStringMicrophones(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (MicrophoneSetBean ms : trBean.getMics()) {
            riderString.append("Microfono: ");
            if (ms.getNeedsPhantomPower() != null) {
                riderString.append(ms.getNeedsPhantomPower() ? "richiede phantom power " : "non richiede phantom power ");
            }
            riderString.append("(x").append(ms.getQuantity()).append(")\n");
        }
    }

    private void getStringDIBoxes(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (DIBoxSetBean di : trBean.getDiBoxes()) {
            riderString.append("DI Box: ");
            if (di.getActive() != null) {
                riderString.append(di.getActive() ? "Attivo (richiede phantom power) " : "Passivo (non richiede phantom power) ");
            }
            riderString.append("(x").append(di.getQuantity()).append(")\n");
        }
    }

    private void getStringMonitors(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (MonitorSetBean ms : trBean.getMonitors()) {
            riderString.append("Monitor: ");
            if (ms.getPowered() != null) {
                riderString.append(ms.getPowered() ? "Attivo (non richiede amplificatore esterno) " : "Passivo (richiede amplificatore esterno) ");
            }
            riderString.append("(x").append(ms.getQuantity()).append(")\n");
        }
    }

    private void getStringMicStands(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (MicStandSetBean mss : trBean.getMicStands()) {
            riderString.append("Asta del microfono: ");
            if (mss.getTall() != null) {
                riderString.append(mss.getTall() ? "Alta " : "Bassa ");
            }
            riderString.append("(x").append(mss.getQuantity()).append(")\n");
        }
    }

    private void getStringCables(TechnicalRiderBean trBean, StringBuilder riderString) {
        for (CableSetBean cs : trBean.getCables()) {
            riderString.append("Cavo: formato ").append(cs.getFunction().toString()).append(" (x").append(cs.getQuantity()).append(")\n");
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.artist.home");
        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handleModifyRiderButton(ActionEvent event) {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.management_technical_rider.view");
        NavigationGUI.navigateToPath(stage, fxmlPath);
    }

    @FXML
    private void handleContinueButton(ActionEvent event) {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.apply_event_choice.view");
        Scene currentScene = continueButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }
}
