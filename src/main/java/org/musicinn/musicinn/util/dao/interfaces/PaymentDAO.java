package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Payment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.List;

public interface PaymentDAO {
    void save(int applicationId, int daysOfDeadline) throws PersistenceException;
    Payment findByApplicationId(int applicationId) throws PersistenceException;
    void updatePaymentState(int applicationId, Session.UserRole role, String transactionId) throws PersistenceException;
    List<String> markAsRefunded(int applicationId) throws PersistenceException;
}
