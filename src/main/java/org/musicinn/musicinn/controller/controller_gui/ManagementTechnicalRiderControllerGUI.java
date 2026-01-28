package org.musicinn.musicinn.controller.controller_gui;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.ManagementTechnicalRiderController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ManagementTechnicalRiderControllerGUI implements Initializable {
    @FXML
    private VBox mixersVBox;

    @FXML
    private Button addMixerButton;

    @FXML
    private VBox stageBoxesVBox;

    @FXML
    private Button addStageBox;

    @FXML
    private VBox inputEquipmentsVBox;

    @FXML
    private Button addMicrophone;

    @FXML
    private Button addDIBox;

    @FXML
    private VBox outputEquipmentsVBox;

    @FXML
    private Button addMonitor;

    @FXML
    private VBox otherEquipmentsVBox;

    @FXML
    private Button addCable;

    @FXML
    private Button addMicStand;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button saveChangesButton;

    private static final String DESCRIPTION_PAGE = "Gestisci Rider Tecnico";

    @FXML
    private HeaderControllerGUI headerController;

    private Button fohRemoveButton;
    private final VBox fohVBox = new VBox(5);
    private final VBox stageVBox = new VBox(5);
    private final VBox sbVBox = new VBox(5);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());
        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) setupArtistView();
        loadExistingData();
    }

    private void loadExistingData() {
        try {
            ManagementTechnicalRiderController appController = new ManagementTechnicalRiderController();
            TechnicalRiderBean trBean = appController.loadRiderData();

            // Per il caricamento iniziale non serve il check duplicati perché il DB è già aggregato
            if (trBean.getMixers() != null) trBean.getMixers().forEach(this::displayMixer);
            if (trBean.getStageBoxes() != null) trBean.getStageBoxes().forEach(this::displayStageBox);
            if (trBean.getMics() != null) trBean.getMics().forEach(this::displayMicrophone);
            if (trBean.getDiBoxes() != null) trBean.getDiBoxes().forEach(this::displayDIBox);
            if (trBean.getMonitors() != null) trBean.getMonitors().forEach(this::displayMonitor);
            if (trBean.getMicStands() != null) trBean.getMicStands().forEach(this::displayMicStand);
            if (trBean.getCables() != null) trBean.getCables().forEach(this::displayCable);
        } catch (DatabaseException e) {
            statusLabel.setText("Errore del database: Nessun rider precedente trovato.");
        }
    }

    // --- LOGICA DISPLAY CON GESTIONE DUPLICATI ---

    private void displayMicrophone(MicrophoneSetBean newBean) {
        for (Node node : inputEquipmentsVBox.getChildren()) {
            if (node.getUserData() instanceof MicrophoneSetBean existing &&
                    existing.getNeedsPhantomPower() == newBean.getNeedsPhantomPower()) {
                existing.setQuantity(existing.getQuantity() + newBean.getQuantity());
                updateRowLabel(node, formatMic(existing));
                return;
            }
        }
        addNewRow(inputEquipmentsVBox, newBean, formatMic(newBean));
    }

    private void displayDIBox(DIBoxSetBean newBean) {
        for (Node node : inputEquipmentsVBox.getChildren()) {
            if (node.getUserData() instanceof DIBoxSetBean existing &&
                    Objects.equals(existing.getActive(), newBean.getActive())) {
                existing.setQuantity(existing.getQuantity() + newBean.getQuantity());
                updateRowLabel(node, formatDI(existing));
                return;
            }
        }
        addNewRow(inputEquipmentsVBox, newBean, formatDI(newBean));
    }

    private void displayMonitor(MonitorSetBean newBean) {
        for (Node node : outputEquipmentsVBox.getChildren()) {
            if (node.getUserData() instanceof MonitorSetBean existing &&
                    Objects.equals(existing.getPowered(), newBean.getPowered())) {
                existing.setQuantity(existing.getQuantity() + newBean.getQuantity());
                updateRowLabel(node, formatMonitor(existing));
                return;
            }
        }
        addNewRow(outputEquipmentsVBox, newBean, formatMonitor(newBean));
    }

    private void displayMicStand(MicStandSetBean newBean) {
        for (Node node : otherEquipmentsVBox.getChildren()) {
            if (node.getUserData() instanceof MicStandSetBean existing &&
                    existing.getTall() == newBean.getTall()) {
                existing.setQuantity(existing.getQuantity() + newBean.getQuantity());
                updateRowLabel(node, formatStand(existing));
                return;
            }
        }
        addNewRow(otherEquipmentsVBox, newBean, formatStand(newBean));
    }

    private void displayCable(CableSetBean newBean) {
        for (Node node : otherEquipmentsVBox.getChildren()) {
            if (node.getUserData() instanceof CableSetBean existing &&
                    existing.getPurpose().equals(newBean.getPurpose())) {
                existing.setQuantity(existing.getQuantity() + newBean.getQuantity());
                updateRowLabel(node, formatCable(existing));
                return;
            }
        }
        addNewRow(otherEquipmentsVBox, newBean, formatCable(newBean));
    }

    // Mixer e StageBox solitamente non si sommano (hanno caratteristiche uniche)
    private void displayMixer(MixerBean bean) {
        StringBuilder sb = new StringBuilder();
        buildString(bean.getInputChannels(), bean.getAuxSends(), bean.getDigital(), bean.getHasPhantomPower(), sb);
        String desc = sb.toString().trim().replaceAll(",$", "");

        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
            // Se il FOH è vuoto, il primo va lì
            if (fohVBox.getChildren().isEmpty()) {
                bean.setFOH(true);
                HBox row = createManagedRow(fohVBox, desc, true);
                row.setUserData(bean);
                fohVBox.getChildren().add(row);
            } else {
                // Altrimenti va nello Stage
                bean.setFOH(false);
                HBox row = createManagedRow(stageVBox, desc, false);
                row.setUserData(bean);
                stageVBox.getChildren().add(row);
            }
        } else {
            // Logica Manager (lista generica)
            HBox row = createManagedRow(mixersVBox, desc, false);
            row.setUserData(bean);
            mixersVBox.getChildren().add(row);
        }
    }

    private void displayStageBox(StageBoxBean bean) {
        String desc = bean.getInputChannels() + " in, " + (bean.getDigital() ? "Digitale" : "Analogica");
        VBox target = Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST) ? sbVBox : stageBoxesVBox;
        addNewRow(target, bean, desc);
    }

    // --- HELPER FORMATTAZIONE ---
    private String formatMic(MicrophoneSetBean b) { return String.format("Microfono:%s (qt: %d)", formatStatus(b.getNeedsPhantomPower(), "con phantom", "senza phantom"), b.getQuantity()); }
    private String formatDI(DIBoxSetBean b) { return String.format("DI Box:%s (qt: %d)", formatStatus(b.getActive(), "Attiva", "Passiva"), b.getQuantity()); }
    private String formatMonitor(MonitorSetBean b) { return String.format("Monitor:%s (qt: %d)", formatStatus(b.getPowered(), "Attivo", "Passivo"), b.getQuantity()); }
    private String formatStand(MicStandSetBean b) { return String.format("Asta:%s (qt: %d)", formatStatus(b.getTall(), "Alta", "Bassa"), b.getQuantity()); }
    private String formatCable(CableSetBean b) { return String.format("Cavo: %s (qt: %d)", b.getPurpose(), b.getQuantity()); }

    // --- GESTIONE ROW ---
    private void addNewRow(VBox container, Object bean, String desc) { addNewRow(container, bean, desc, false); }
    private void addNewRow(VBox container, Object bean, String desc, boolean isFOH) {
        HBox row = createManagedRow(container, desc, isFOH);
        row.setUserData(bean);
        container.getChildren().add(row);
    }
    private void updateRowLabel(Node node, String newText) {
        if (node instanceof HBox row && !row.getChildren().isEmpty() && row.getChildren().get(0) instanceof Label label) {
            label.setText(newText);
        }
    }

    // --- HANDLERS ---
    @FXML private void handleAddMixer(ActionEvent e) { handlePopupLogic("fxml.mixer_features.modal", "Mixer", (MixerPopupControllerGUI c) -> { if(c.getCreatedMixer()!=null) displayMixer(c.getCreatedMixer()); }); }
    @FXML private void handleAddStageBox(ActionEvent e) { handlePopupLogic("fxml.stage_box_features.modal", "Stage Box", (StageBoxPopupControllerGUI c) -> { if(c.getCreatedStageBox()!=null) displayStageBox(c.getCreatedStageBox()); }); }
    @FXML private void handleAddMicrophone(ActionEvent e) { handlePopupLogic("fxml.microphone_features.modal", "Microfoni", (MicrophoneSetPopupControllerGUI c) -> { if(c.getCreatedMicrophoneSet()!=null) displayMicrophone(c.getCreatedMicrophoneSet()); }); }
    @FXML private void handleAddDIBox(ActionEvent e) { handlePopupLogic("fxml.di_box_features.modal", "DI Box", (DIBoxSetPopupControllerGUI c) -> { if(c.getCreatedDIBoxSet()!=null) displayDIBox(c.getCreatedDIBoxSet()); }); }
    @FXML private void handleAddMonitor(ActionEvent e) { handlePopupLogic("fxml.monitor_features.modal", "Monitor", (MonitorSetPopupControllerGUI c) -> { if(c.getCreatedMonitorSet()!=null) displayMonitor(c.getCreatedMonitorSet()); }); }
    @FXML private void handleAddMicStand(ActionEvent e) { handlePopupLogic("fxml.mic_stand_features.modal", "Aste", (MicStandSetPopupControllerGUI c) -> { if(c.getCreatedMicStandSet()!=null) displayMicStand(c.getCreatedMicStandSet()); }); }
    @FXML private void handleAddCable(ActionEvent e) { handlePopupLogic("fxml.cable_features.modal", "Cavi", (CableSetPopupControllerGUI c) -> { if(c.getCreatedCableSet()!=null) displayCable(c.getCreatedCableSet()); }); }

    @FXML
    private void handleSaveChangesButton(ActionEvent event) {
        try {
            ManagementTechnicalRiderController controller = new ManagementTechnicalRiderController();
            Session.UserRole role = Session.getSingletonInstance().getRole();
            controller.saveRiderData(
                    role == Session.UserRole.ARTIST ? combine(fohVBox, stageVBox) : collectTypedBeans(mixersVBox, MixerBean.class),
                    role == Session.UserRole.ARTIST ? collectTypedBeans(sbVBox, StageBoxBean.class) : collectTypedBeans(stageBoxesVBox, StageBoxBean.class),
                    collectTypedBeans(inputEquipmentsVBox, MicrophoneSetBean.class),
                    collectTypedBeans(inputEquipmentsVBox, DIBoxSetBean.class),
                    collectTypedBeans(outputEquipmentsVBox, MonitorSetBean.class),
                    collectTypedBeans(otherEquipmentsVBox, MicStandSetBean.class),
                    collectTypedBeans(otherEquipmentsVBox, CableSetBean.class)
            );
            statusLabel.setText("Rider salvato con successo!");
        } catch (Exception e) { e.printStackTrace(); statusLabel.setText("Errore: " + e.getMessage()); }
    }

    // --- UTILS ---
    private List<MixerBean> combine(VBox v1, VBox v2) {
        List<MixerBean> list = new ArrayList<>(collectTypedBeans(v1, MixerBean.class));
        list.addAll(collectTypedBeans(v2, MixerBean.class));
        return list;
    }

    private <T> List<T> collectTypedBeans(VBox c, Class<T> t) { return c.getChildren().stream().map(Node::getUserData).filter(t::isInstance).map(t::cast).toList(); }

    // Metodo universale per i booleani
    private String formatStatus(Boolean value, String trueLabel, String falseLabel) {
        if (value == null) return ""; // Non scrive nulla se indifferente
        return " " + (value ? trueLabel : falseLabel);
    }

    // Metodo per il Mixer e la StageBox (gestione virgole pulita)
    private void buildString(int ch, int aux, Boolean digital, Boolean phantom, StringBuilder sb) {
        sb.append(ch).append(" ch, ").append(aux).append(" AUX");
        if (digital != null) sb.append(", ").append(digital ? "Digitale" : "Analogico");
        if (phantom != null) sb.append(", ").append(phantom ? "consente phantom" : "no phantom");
    }

    private HBox createManagedRow(VBox container, String description, boolean isFOH) {
        HBox row = new HBox(15); row.setAlignment(Pos.CENTER_LEFT);
        Button removeButton = new Button("Rimuovi");
        if (isFOH) fohRemoveButton = removeButton;
        removeButton.setOnAction(e -> { if (removeButton == fohRemoveButton) fohRemoveButton = null; container.getChildren().remove(row); });
        row.getChildren().addAll(new Label(description), removeButton);
        return row;
    }

    private void setupArtistView() {
        // Creiamo dei contenitori orizzontali con allineamento centrale verticale
        HBox fohRow = new HBox(10, new Label("FOH:  "), fohVBox);
        fohRow.setAlignment(Pos.CENTER_LEFT);

        HBox stageRow = new HBox(10, new Label("Stage:"), stageVBox);
        stageRow.setAlignment(Pos.CENTER_LEFT);

        HBox sbRow = new HBox(10, new Label("SB:   "), sbVBox);
        sbRow.setAlignment(Pos.CENTER_LEFT);

        // Applichiamo un po' di spaziatura interna per staccarli dal bordo sinistro
        fohRow.setStyle("-fx-padding: 0 0 0 5;");
        stageRow.setStyle("-fx-padding: 0 0 0 5;");
        sbRow.setStyle("-fx-padding: 0 0 0 5;");

        mixersVBox.getChildren().addAll(fohRow, stageRow);
        stageBoxesVBox.getChildren().add(sbRow);

        setupLimitListeners();
    }

    private void setupLimitListeners() {
        stageVBox.getChildren().addListener((ListChangeListener<Node>) c -> { addMixerButton.setDisable(!stageVBox.getChildren().isEmpty()); if(fohRemoveButton!=null) fohRemoveButton.setDisable(!stageVBox.getChildren().isEmpty()); });
        sbVBox.getChildren().addListener((ListChangeListener<Node>) c -> addStageBox.setDisable(!sbVBox.getChildren().isEmpty()));
    }

    private <C> void handlePopupLogic(String fxml, String title, java.util.function.Consumer<C> handler) {
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource(FxmlPathLoader.getPath(fxml)));
            Stage s = new Stage(); s.setScene(new Scene(l.load())); s.initModality(Modality.APPLICATION_MODAL); s.showAndWait();
            handler.accept(l.getController());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleBackButton(ActionEvent e) { NavigationGUI.navigateToPath((Stage)backButton.getScene().getWindow(), FxmlPathLoader.getPath(Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER) ? "fxml.manager.home" : "fxml.artist.home")); }
}
