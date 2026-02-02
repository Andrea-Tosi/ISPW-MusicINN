package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.exceptions.PaymentServiceException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class PaymentCardControllerGUI {
    @FXML
    private VBox cardRoot;

    @FXML
    private Label counterpartyName;

    @FXML
    private Label eventDateLabel;

    @FXML
    private ImageView checkImageCachet;

    @FXML
    private Label cachetLabel;

    @FXML
    private ImageView checkImageDeposit;

    @FXML
    private Label depositLabel;

    @FXML
    private Label remainingTimeLabel;

    @FXML
    private Button managePaymentButton;

    private PaymentBean paymentBean; // Il riferimento ai dati

    private static final Logger LOGGER = Logger.getLogger(PaymentCardControllerGUI.class.getName());

    public void setPaymentBean(PaymentBean paymentBean) {
        this.paymentBean = paymentBean;

        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
            counterpartyName.setText(paymentBean.getVenueName());
        } else if (Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER)) {
            counterpartyName.setText(paymentBean.getArtistStageName());
        }
        eventDateLabel.setText(LocalDateTime.of(paymentBean.getStartingDate(), paymentBean.getStartingTime()).toString());
        cachetLabel.setText(paymentBean.getCachet() + " €");
        depositLabel.setText(paymentBean.getDeposit() + " €");
        remainingTimeLabel.setText(paymentBean.getPaymentDeadlineString());

        // Gestione icone di check (se già pagato o meno)
        checkImageCachet.setVisible(paymentBean.isCachetPaid());
        checkImageDeposit.setVisible(paymentBean.isDepositPaid());

        boolean isOwnPartPaid = false;
        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
            isOwnPartPaid = paymentBean.isDepositPaid();
        } else if (Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER)) {
            isOwnPartPaid = paymentBean.isCachetPaid();
        }
        managePaymentButton.setVisible(!isOwnPartPaid);
    }

    @FXML
    public void handleManagePayment() {
        try {
            PaymentController controller = PaymentServiceFactory.getPaymentController();

            String checkoutUrl = controller.getPaymentUrl(this.paymentBean);

            String fxmlPath = FxmlPathLoader.getPath("fxml.payment_window.modal");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            PaymentWindowControllerGUI controllerGUI = loader.getController();
            controllerGUI.loadUrl(checkoutUrl, "musicinn.org/success");

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controllerGUI.isSuccess()) {
                controller.completePaymentWorkflow(this.paymentBean, controllerGUI.getStripeSessionId());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pagamento completato con successo!");
                alert.show();

                updateUI();
            }
        } catch (PaymentServiceException | PersistenceException _) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore nel pagamento. Riprovare");
            alert.show();

            hideCard();
        } catch (IOException e) {
            LOGGER.fine(e.getMessage());
        }
    }

    private void updateUI() {
        // Nasconde il pulsante e mostra un feedback visivo di successo
        managePaymentButton.setVisible(false);
        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
            checkImageDeposit.setVisible(true);
        } else if (Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER)) {
            checkImageCachet.setVisible(true);
        }
    }

    private void hideCard() {
        cardRoot.setVisible(false);
        cardRoot.setManaged(false);
    }
}
