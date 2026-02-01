package org.musicinn.musicinn.util;

import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.exceptions.PaymentServiceException;

public interface PaymentService {
    String createPaymentAccount(String email) throws PaymentServiceException;
    String getCheckoutSessionUrl(PaymentBean bean) throws PaymentServiceException;
    String getPaymentIntentFromSession(String sessionId) throws PaymentServiceException;
    void issueRefund(String paymentIntentId) throws PaymentServiceException;
}
