package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagePaymentsAnnouncementsViewControllerGUI implements Initializable{
    @FXML
    private TilePane paymentsContainer;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private HeaderControllerGUI headerController;

    private static final String DESCRIPTION_PAGE = "Gestisci Pagamenti";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setPageLabelText(DESCRIPTION_PAGE);
        headerController.setUsernameLabelText(Session.getSingletonInstance().getUsername());

        loadAllPaymentCards();
    }

    private void loadAllPaymentCards() {
        try {
            // 1. Chiamata al Controller Applicativo per ottenere gli accordi pendenti
            PaymentController controller = PaymentServiceFactory.getPaymentController();
            List<PaymentBean> payments = controller.getPayments();

            for (PaymentBean bean : payments) {
                // 2. Carica l'FXML della singola cardRoot
                String fxmlPath = FxmlPathLoader.getPath("fxml.payment.card");
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent cardRoot = loader.load();

                // 3. Ottieni il controller della cardRoot e passa i dati
                PaymentCardControllerGUI cardController = loader.getController();
                cardController.setPaymentBean(bean);

                // 4. Aggiungi la cardRoot al contenitore (es. paymentContainer è una VBox nel tuo FXML)
                paymentsContainer.getChildren().add(cardRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DatabaseException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        String nextFxmlPath = "";
        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
            nextFxmlPath = FxmlPathLoader.getPath("fxml.artist.home");
        } else if (Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER)) {
            nextFxmlPath = FxmlPathLoader.getPath("fxml.manager.home");
        }
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }
}
