package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Payment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.List;

public interface PaymentDAO {
    void save(int applicationId, int daysOfDeadline) throws DatabaseException;
    Payment findByApplicationId(int applicationId) throws DatabaseException;
    void updatePaymentState(int applicationId, Session.UserRole role, String transactionId) throws DatabaseException;
    List<String> markAsRefunded(int applicationId) throws DatabaseException;
}
