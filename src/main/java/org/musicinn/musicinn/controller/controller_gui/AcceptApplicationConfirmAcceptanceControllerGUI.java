package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.musicinn.musicinn.controller.controller_application.AcceptApplicationController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.ApplicationBean;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

public class AcceptApplicationConfirmAcceptanceControllerGUI {
    @FXML
    private Label eventDateLabel;

    @FXML
    private Button confirmAcceptanceButton;

    @FXML
    private Label cachetLabel;

    private AnnouncementBean announcementBean;
    private ApplicationBean applicationBean;

    public void setAnnouncementBean(AnnouncementBean announcementBean) {
        this.announcementBean = announcementBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    @FXML
    private void handleConfirmButton() {
        try {
            AcceptApplicationController acceptApplicationController = AcceptApplicationController.getSingletonInstance();
            acceptApplicationController.chooseApplication(announcementBean, applicationBean);

            PaymentController paymentController = PaymentServiceFactory.getPaymentController();
            int daysOfDeadline = paymentController.createPayment(applicationBean);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hai " + daysOfDeadline + " giorni di tempo\nper versare il cachet al conto di escrow.\nSarai reindirizzato alla homepage,\nper gestire i pagamenti vai alla sezione apposita.");
            alert.showAndWait();

            Stage stage = (Stage) confirmAcceptanceButton.getScene().getWindow();
            Window window = stage.getOwner();
            NavigationGUI.navigateToPath((Stage) window, FxmlPathLoader.getPath("fxml.manager.home"));

            stage.close();
        } catch (PersistenceException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public void initData() {
        eventDateLabel.setText("Stai per accettare la candidatura di " + applicationBean.getArtistStageName() +
                " per l'evento del " + announcementBean.getStartingDate() + " " + announcementBean.getStartingTime());
        cachetLabel.setText(announcementBean.getCachet() + "€");
    }
}
