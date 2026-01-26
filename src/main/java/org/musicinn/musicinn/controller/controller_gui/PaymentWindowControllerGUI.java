package org.musicinn.musicinn.controller.controller_gui;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PaymentWindowControllerGUI {
    public WebView webView;
    private boolean success = false;
    private String stripeSessionId;

    public void loadUrl(String url, String successUrl) {
        WebEngine engine = webView.getEngine();
        engine.load(url);

        // Monitoriamo i cambiamenti dell'URL
        engine.locationProperty().addListener((observable, oldLocation, newLocation) -> {
            if (newLocation.contains(successUrl)) {
                this.success = true;

                // Estrae l'ID reale dall'URL (molto elegante per l'esame)
                if (newLocation.contains("session_id=")) {
                    this.stripeSessionId = newLocation.split("session_id=")[1];
                }

                // Chiudiamo la finestra automaticamente al successo
                ((Stage) webView.getScene().getWindow()).close();
            }
        });
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }
}
