package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Payment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.PaymentDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.List;

public class PaymentDAOMemory implements PaymentDAO {
    @Override
    public void save(int applicationId, int daysOfDeadline) throws DatabaseException {
        System.out.println("payment id salvato: " + applicationId);
    }

    @Override
    public Payment findByApplicationId(int applicationId) {
        return null;
    }

    @Override
    public void updatePaymentState(int applicationId, Session.UserRole role, String transactionId) throws DatabaseException {

    }

    @Override
    public List<String> markAsRefunded(int applicationId) throws DatabaseException {
        return null;
    }
}
