package org.musicinn.musicinn.controller.controller_gui;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import org.musicinn.musicinn.util.technical_rider_bean.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    private final static String DESCRIPTION_PAGE = "Gestisci Rider Tecnico";

    @FXML
    private HeaderControllerGUI headerController;

    private Button fohRemoveButton;
    private VBox fohVBox = new VBox(5);
    private VBox stageVBox = new VBox(5);
    private VBox sbVBox = new VBox(5);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());
        Session.UserRole role = Session.getSingletonInstance().getRole();
        if (role.equals(Session.UserRole.ARTIST)) setupArtistView();
    }

    private void setupArtistView() {
        setupArtistDefaultRows();
        setupLimitListeners();
    }

    private void setupArtistDefaultRows() {
        // Creazione delle righe predefinite per l'artista
        HBox fohRow = new HBox(5, new Label("FOH Mixer: "), fohVBox);
        fohRow.setAlignment(Pos.CENTER_LEFT);
        HBox stageRow = new HBox(5, new Label("Stage Mixer: "), stageVBox);
        stageRow.setAlignment(Pos.CENTER_LEFT);
        HBox sbRow = new HBox(5, new Label("Stage Box: "), sbVBox);
        sbRow.setAlignment(Pos.CENTER_LEFT);

        // Aggiunta ai rispettivi contenitori
        mixersVBox.getChildren().addAll(fohRow, stageRow);
        stageBoxesVBox.getChildren().add(sbRow);
    }

    private void setupLimitListeners() {
        stageVBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            boolean hasStageMixer = !stageVBox.getChildren().isEmpty();
            // Se c'è lo Stage Mixer, disabilita il tasto "Aggiungi"
            addMixerButton.setDisable(hasStageMixer);
            // Se c'è lo Stage Mixer, blocca la rimozione del FOH
            if (fohRemoveButton != null) {
                fohRemoveButton.setDisable(hasStageMixer);
            }
        });

        // Monitoraggio Stage Box (Limite 1)
        sbVBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            boolean hasStageBox = !sbVBox.getChildren().isEmpty();
            // Se c'è già una Stage Box, disabilita il tasto "Aggiungi Stage Box"
            addStageBox.setDisable(hasStageBox);
        });
    }

    private <C, B> void handleEquipmentLogic(
            String fxmlKey, String title, VBox container,
            java.util.function.Function<C, B> beanRetriever,
            java.util.function.BiPredicate<Object, Object> similarityChecker,
            java.util.function.Function<Object, String> formatter
    ) {
        handlePopupLogic(fxmlKey, title, (C controller) -> {
            B newBean = beanRetriever.apply(controller);
            if (newBean == null) return;

            Optional<Node> duplicate = container.getChildren().stream()
                    .filter(n -> n.getUserData() != null)
                    .filter(n -> n.getUserData().getClass().equals(newBean.getClass())) // Protezione ClassCastException
                    .filter(n -> similarityChecker.test(n.getUserData(), newBean))
                    .findFirst();

            if (duplicate.isPresent()) {
                Object existing = duplicate.get().getUserData();
                updateQuantity(existing, getAttr(newBean, "getQuantity", 0));
                ((Label) ((HBox) duplicate.get()).getChildren().get(0)).setText(formatter.apply(existing));
            } else {
                HBox newRow = createManagedRow(container, formatter.apply(newBean), false);
                newRow.setUserData(newBean);
                container.getChildren().add(newRow);
            }
        });
    }

    private <T> T getAttr(Object obj, String methodName, T defaultValue) {
        if (obj == null) return defaultValue;
        try {
            Object result = obj.getClass().getMethod(methodName).invoke(obj);
            if (result == null) return defaultValue;

            // Se ci aspettiamo una Stringa ma l'oggetto è un Enum o altro, usiamo toString()
            if (defaultValue instanceof String && !(result instanceof String)) {
                return (T) result.toString();
            }

            return (T) result;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void setAttr(Object obj, String methodName, Object value) {
        if (obj == null || value == null) return;

        try {
            Method method = findMethod(obj.getClass(), methodName, value);
            if (method != null) {
                method.invoke(obj, value);
            }
        } catch (Exception e) {
            // Logga l'eccezione in modo appropriato
            e.printStackTrace();
        }
    }

    private Method findMethod(Class<?> clazz, String methodName, Object value) {
        // Determina il tipo primitivo se il valore è un Integer
        Class<?> primitiveType = (value instanceof Integer) ? int.class : value.getClass();

        try {
            // Tentativo 1: Tipo primitivo (es. setQuantity(int))
            return clazz.getMethod(methodName, primitiveType);
        } catch (NoSuchMethodException e) {
            try {
                // Tentativo 2: Tipo Wrapper (es. setQuantity(Integer))
                return clazz.getMethod(methodName, value.getClass());
            } catch (NoSuchMethodException ex) {
                return null; // Metodo non trovato in nessuna forma
            }
        }
    }
    //TODO in realtà non penso serva un controllo sul tipo Integer dato che i setAttr sono tutti int

    private void updateQuantity(Object obj, int extraQty) {
        int currentQty = getAttr(obj, "getQuantity", 0);
        setAttr(obj, "setQuantity", currentQty + extraQty);
    }

    private <C> void handlePopupLogic(String fxmlKey, String title, java.util.function.Consumer<C> resultHandler) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlPathLoader.getPath(fxmlKey)));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // Passiamo il controller al gestore del risultato
            C controller = loader.getController();
            resultHandler.accept(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HBox createManagedRow(VBox container, String description, boolean isFOH) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        Button removeButton = new Button("Rimuovi");
        if (isFOH) fohRemoveButton = removeButton; // Salva il riferimento per i listener

        removeButton.setOnAction(e -> {
            if (removeButton == fohRemoveButton) fohRemoveButton = null;
            container.getChildren().remove(row);
        });

        row.getChildren().addAll(new Label(description), removeButton);
        return row;
    }

    @FXML
    private void handleAddMixer(ActionEvent event) {
        handlePopupLogic("fxml.mixer_features.modal", "Configura Mixer", (MixerPopupControllerGUI c) -> {
            Object m = c.getCreatedMixer(); // Riceviamo come Object per coerenza
            if (m == null) return;

            // Estrazione dati tramite getAttr
            int ch = getAttr(m, "getInputChannels", 0);
            int aux = getAttr(m, "getAuxSends", 0);
            boolean isDig = getAttr(m, "getDigital", false);
            boolean ph = getAttr(m, "getHasPhantomPower", false);

            String desc = String.format("%d ch, %d AUX, %s, %sconsente il phantom power", ch, aux, isDig ? "Digitale" : "Analogico", ph ? "" : "non ");

            Session.UserRole role = Session.getSingletonInstance().getRole();
            if (role.equals(Session.UserRole.ARTIST)) {
                if (fohVBox.getChildren().isEmpty()) {
                    setAttr(m, "setFOH", true); // Imposta come mixer principale
                    HBox row = createManagedRow(fohVBox, desc, true);
                    row.setUserData(m);
                    fohVBox.getChildren().add(row);
                } else {
                    setAttr(m, "setFOH", false); // Imposta come mixer di palco
                    HBox row = createManagedRow(stageVBox, desc, false);
                    row.setUserData(m);
                    stageVBox.getChildren().add(row);
                }
            } else {
                HBox row = createManagedRow(mixersVBox, desc, false);
                row.setUserData(m);
                mixersVBox.getChildren().add(row);
            }
        });
    }

    @FXML
    private void handleAddStageBox(ActionEvent event) {
        handlePopupLogic("fxml.stage_box_features.modal", "Configura Stage Box", (StageBoxPopupControllerGUI c) -> {
            Object sb = c.getCreatedStageBox();
            if (sb == null) return;

            // Estrazione dati tramite getAttr
            int ch = getAttr(sb, "getInputChannels", 0);
            boolean isDig = getAttr(sb, "getDigital", false);

            String desc = String.format("%d in, %s", ch, isDig ? "Digitale" : "Analogica");

            Session.UserRole role = Session.getSingletonInstance().getRole();
            if (role.equals(Session.UserRole.ARTIST)) {
                HBox row = createManagedRow(sbVBox, desc, false);
                row.setUserData(sb);
                sbVBox.getChildren().add(row);
            } else {
                HBox row = createManagedRow(stageBoxesVBox, desc, false);
                row.setUserData(sb);
                stageBoxesVBox.getChildren().add(row);
            }
        });
    }

    @FXML
    private void handleAddMicrophone(ActionEvent event) {
        handleEquipmentLogic("fxml.microphone_features.modal", "Microfoni", inputEquipmentsVBox,
                c -> ((MicrophoneSetPopupControllerGUI) c).getCreatedMicrophoneSet(),
                (o, n) -> getAttr(o, "getNeedsPhantomPower", false).equals(getAttr(n, "getNeedsPhantomPower", false)),
                obj -> String.format("%s phantom, (qt: %d)", (boolean) getAttr(obj, "getNeedsPhantomPower", false) ? "Con" : "Senza", getAttr(obj, "getQuantity", 0))
        );
    }

    @FXML
    private void handleAddDIBox(ActionEvent event) {
        handleEquipmentLogic("fxml.di_box_features.modal", "DI Box", inputEquipmentsVBox,
                c -> ((DIBoxSetPopupControllerGUI) c).getCreatedDIBoxSet(),
                (o, n) -> getAttr(o, "getActive", false).equals(getAttr(n, "getActive", false)),
                obj -> String.format("DI Box %s, (qt: %d)", (boolean) getAttr(obj, "getActive", false) ? "Attiva" : "Passiva", getAttr(obj, "getQuantity", 0))
        );
    }

    @FXML
    private void handleAddMonitor(ActionEvent event) {
        handleEquipmentLogic("fxml.monitor_features.modal", "Monitor", outputEquipmentsVBox,
                c -> ((MonitorSetPopupControllerGUI) c).getCreatedMonitorSet(),
                (o, n) -> getAttr(o, "getPowered", false).equals(getAttr(n, "getPowered", false)),
                obj -> String.format("Monitor %s, (qt: %d)", (boolean) getAttr(obj, "getPowered", false) ? "Attivo" : "Passivo", getAttr(obj, "getQuantity", 0))
        );
    }

    @FXML
    private void handleAddMicStand(ActionEvent event) {
        handleEquipmentLogic("fxml.mic_stand_features.modal", "Aste", otherEquipmentsVBox,
                c -> ((MicStandSetPopupControllerGUI) c).getCreatedMicStandSet(),
                (o, n) -> getAttr(o, "getTall", false).equals(getAttr(n, "getTall", false)),
                obj -> String.format("Asta %s, (qt: %d)", (boolean) getAttr(obj, "getTall", false) ? "Alta" : "Bassa", getAttr(obj, "getQuantity", 0))
        );
    }

    @FXML
    private void handleAddCable(ActionEvent event) {
        handleEquipmentLogic("fxml.cable_features.modal", "Cavi", otherEquipmentsVBox,
                c -> ((CableSetPopupControllerGUI) c).getCreatedCableSet(),
                (o, n) -> getAttr(o, "getFunction", "").equals(getAttr(n, "getFunction", "")),
                obj -> String.format("Cavo %s, (qt: %d)", getAttr(obj, "getFunction", ""), getAttr(obj, "getQuantity", 0))
        );
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        String nextFxmlPath = "";
        Session.UserRole role = Session.getSingletonInstance().getRole();
        if (role.equals(Session.UserRole.MANAGER)) {
            nextFxmlPath = FxmlPathLoader.getPath("fxml.manager.home");
        } else if (role.equals(Session.UserRole.ARTIST)) {
            nextFxmlPath = FxmlPathLoader.getPath("fxml.artist.home");
        }

        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();

        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }

    @FXML
    private void handleSaveChangesButton(ActionEvent event) {
        // Liste perfettamente tipizzate
        List<MixerBean> mixers = new ArrayList<>();
        Session.UserRole role = Session.getSingletonInstance().getRole();
        if (role.equals(Session.UserRole.ARTIST)) {
            mixers.addAll(collectTypedBeans(fohVBox, MixerBean.class));
            mixers.addAll(collectTypedBeans(stageVBox, MixerBean.class));
        } else {
            mixers.addAll(collectTypedBeans(mixersVBox, MixerBean.class));
        }

        List<StageBoxBean> stageBoxes = null;
        if (role.equals(Session.UserRole.ARTIST)) {
            stageBoxes = collectTypedBeans(sbVBox, StageBoxBean.class);
        } else {
            collectTypedBeans(stageBoxesVBox, StageBoxBean.class);
        }

        List<MicrophoneSetBean> mics = collectTypedBeans(inputEquipmentsVBox, MicrophoneSetBean.class);
        List<DIBoxSetBean> diBoxes = collectTypedBeans(inputEquipmentsVBox, DIBoxSetBean.class);

        List<MonitorSetBean> monitors = collectTypedBeans(outputEquipmentsVBox, MonitorSetBean.class);

        List<MicStandSetBean> micStands = collectTypedBeans(otherEquipmentsVBox, MicStandSetBean.class);
        List<CableSetBean>  cables = collectTypedBeans(otherEquipmentsVBox, CableSetBean.class);

        // Invio al controller applicativo con tipi forti
        ManagementTechnicalRiderController applicationController = new ManagementTechnicalRiderController();
        applicationController.saveRiderData(mixers, stageBoxes, mics, diBoxes, monitors, micStands, cables);

        statusLabel.setText("Rider tecnico salvato correttamente");
    }//TODO modellare l'eccezione lanciata dall'interno del sistema per cui validate fallisce e settare il testo nello statusLabel (solo nel caso in cui l'utente è di tipo Artist)

    // Questo metodo ora accetta un tipo T e filtra automaticamente solo gli oggetti di quel tipo
    private <T> List<T> collectTypedBeans(VBox container, Class<T> type) {
        return container.getChildren().stream()
                .map(Node::getUserData)
                .filter(type::isInstance) // Prende solo se è del tipo giusto (es. MixerBean)
                .map(type::cast)          // Converte in modo sicuro
                .toList();
    }
}
