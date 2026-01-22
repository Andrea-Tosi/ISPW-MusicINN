package org.musicinn.musicinn.util;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

public class StripeService implements PaymentService {
    private static final String API_KEY = Dotenv.load().get("STRIPE_API_KEY");

    /**
     * Crea un account connesso di tipo Express su Stripe e restituisce l'ID.
     */
    @Override
    public String createPaymentAccount(String email) throws StripeException {
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
    }
}
