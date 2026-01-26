package org.musicinn.musicinn.util;

import com.stripe.exception.StripeException;
import org.musicinn.musicinn.util.bean.PaymentBean;

public interface PaymentService {
    String createPaymentAccount(String email) throws StripeException;
    String getCheckoutSessionUrl(PaymentBean bean) throws Exception;
    String getPaymentIntentFromSession(String sessionId) throws StripeException;
    void issueRefund(String paymentIntentId) throws StripeException;
}
