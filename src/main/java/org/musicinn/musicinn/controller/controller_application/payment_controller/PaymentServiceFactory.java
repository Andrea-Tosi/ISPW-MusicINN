package org.musicinn.musicinn.controller.controller_application.payment_controller;

import org.musicinn.musicinn.util.PaymentService;
import org.musicinn.musicinn.util.StripeService;

public class PaymentServiceFactory {
    // Istanza creata una volta (simile a singleton, ma gestito esternamente)
    private static PaymentController paymentController;

    private PaymentServiceFactory() {}

    public static PaymentController getPaymentController() {
        if (paymentController == null) {
            // Crea l'adapter concreto
            PaymentService stripe = new StripeService();
            // Lo inietta nel controller
            paymentController = new PaymentController(stripe);
        }
        return paymentController;
    }
}
