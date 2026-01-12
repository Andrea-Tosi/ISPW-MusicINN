package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.ApplyController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.bean.EventBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplyViewChooseEventControllerGUI {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TilePane eventCardContainer;

    @FXML
    private Button backButton;

    private List<EventBean> loadedEvents = new ArrayList<>();
    private int currentPage = 0;
    private boolean isLoading = false; // Per evitare chiamate multiple simultanee

    @FXML
    public void initialize() {
        // 1. Caricamento iniziale dei primi 10
        loadEvents();

        // 2. Listener per lo scroll infinito
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            // Se il valore è > 0.9 (90% dello scroll) e non stiamo già caricando
            if (newValue.doubleValue() > 0.9 && !isLoading) {
                loadEvents();
            }
        });
    }

    private void loadEvents() {
        isLoading = true;

        ApplyController controller = new ApplyController();
        EventCardControllerGUI cardControllerGUI = new EventCardControllerGUI();

        // Chiamata al controller applicativo per la pagina corrente
        List<EventBean> newEvents = controller.getCompatibleEvents(currentPage);

        if (!newEvents.isEmpty()) {
            for (EventBean bean : newEvents) {
                addEventCard(bean);
            }
            loadedEvents.addAll(newEvents);
            currentPage++; // Incrementiamo la pagina per la prossima chiamata
        }

        isLoading = false;
    }

    private void addEventCard(EventBean bean) {
        try {
            // 1. Carichiamo il file FXML della carta
            String fxmlPath = FxmlPathLoader.getPath("fxml.event.card");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent cardRoot = loader.load();

            // 2. Otteniamo il controller specifico della carta appena caricata
            EventCardControllerGUI cardController = loader.getController();

            // 3. Invochiamo il tuo metodo setup per popolare la carta con il bean
            cardController.setupEventCard(bean);

            // 4. Aggiungiamo la carta al FlowPane principale
            eventCardContainer.getChildren().add(cardRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        Scene currentScene = backButton.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        String fxmlPath = FxmlPathLoader.getPath("fxml.apply_rider_revision.view");
        NavigationGUI.navigateToPath(stage, fxmlPath);
    }
}
