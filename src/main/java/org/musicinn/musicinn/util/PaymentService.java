package org.musicinn.musicinn.util;

import com.stripe.exception.StripeException;

public interface PaymentService {
    String createPaymentAccount(String email) throws StripeException;
}
