package org.musicinn.musicinn.util;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Refund;
import com.stripe.param.AccountCreateParams;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.exceptions.PaymentServiceException;

public class StripeService implements PaymentService {
    public StripeService() {
        // Comunica alla libreria Stripe quale chiave usare per tutte le chiamate API
        Stripe.apiKey = Dotenv.load().get("STRIPE_API_KEY");
    }

    /**
     * Crea un account connesso di tipo Express su Stripe e restituisce l'ID.
     */
    @Override
    public String createPaymentAccount(String email) throws PaymentServiceException {
        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setEmail(email)
                    .setCapabilities(
                            AccountCreateParams.Capabilities.builder()
                                    .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build())
                                    .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                                    .build()
                    )
                    .build();

            Account account = Account.create(params);
            return account.getId(); // Restituisce acct_...
        } catch (StripeException e) {
            throw new PaymentServiceException(e.getMessage());
        }
    }

    @Override
    public String getCheckoutSessionUrl(PaymentBean bean) throws PaymentServiceException {
        try {
            String nameProduct = org.musicinn.musicinn.util.Session.getSingletonInstance().getRole().
                    equals(org.musicinn.musicinn.util.Session.UserRole.MANAGER) ? "Cachet per Artista" : "Cauzione per Manager";

            // Stripe vuole i prezzi in centesimi (Long)
            long amount = org.musicinn.musicinn.util.Session.getSingletonInstance().getRole().
                    equals(org.musicinn.musicinn.util.Session.UserRole.MANAGER) ? (long) (bean.getCachet() * 100) : (long) (bean.getDeposit() * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://musicinn.org/success?session_id={CHECKOUT_SESSION_ID}") // L'URL che la WebView intercetta
                    .setCancelUrl("https://musicinn.org/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(amount)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(nameProduct)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            return session.getUrl(); // Questo URL è unico e dinamico
        } catch (StripeException e) {
            throw new PaymentServiceException(e.getMessage());
        }
    }

    // Esempio di come recuperare il PI dalla Sessione
    @Override
    public String getPaymentIntentFromSession(String sessionId) throws PaymentServiceException {
        try {
            Session session = Session.retrieve(sessionId);
            return session.getPaymentIntent(); // Questo restituirà il "pi_..."
        } catch (StripeException e) {
            throw new PaymentServiceException(e.getMessage());
        }
    }

    public void issueRefund(String paymentIntentId) throws PaymentServiceException {
        try {
            // Stripe prende i soldi di questa transazione e li restituisce al cliente
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            Refund.create(params);
        } catch (StripeException e) {
            throw new PaymentServiceException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Stripe.apiKey = Dotenv.load().get("STRIPE_API_KEY");

        // Lo cancella definitivamente dai server di Stripe
        try {
            Account.retrieve("acct_1SsXzDBpc9dp6Wem").delete();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
