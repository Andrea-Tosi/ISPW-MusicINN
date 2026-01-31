package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Payment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.PaymentDAO;
import org.musicinn.musicinn.util.enumerations.EscrowState;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAOMemory implements PaymentDAO {
    private static final Map<Integer, Payment> payments = new HashMap<>();

    static {
        da creare
    }

    @Override
    public void save(int applicationId, int daysOfDeadline) throws DatabaseException {
        Payment p = new Payment();
        p.setPaymentDeadline(LocalDateTime.now().plusDays(daysOfDeadline));
        p.setState(EscrowState.WAITING_BOTH);

        payments.put(applicationId, p);
    }

    @Override
    public Payment findByApplicationId(int applicationId) throws DatabaseException {
        Payment p = payments.get(applicationId);
        if (p == null) {
            throw new DatabaseException("Nessun record di pagamento trovato per l'applicazione: " + applicationId);
        }
        return p;
    }

    @Override
    public void updatePaymentState(int applicationId, Session.UserRole role, String transactionId) throws DatabaseException {
        Payment p = findByApplicationId(applicationId);

        // Aggiorna l'ID transazione in base a chi ha pagato
        if (role.equals(Session.UserRole.ARTIST)) {
            p.setArtistPaymentIntentId(transactionId);
        } else {
            p.setManagerPaymentIntentId(transactionId);
        }

        // Aggiorna l'EscrowState
        if (p.getArtistPaymentIntentId() != null && p.getManagerPaymentIntentId() != null) {
            p.setState(EscrowState.SECURED);
        } else if (p.getArtistPaymentIntentId() != null || p.getManagerPaymentIntentId() != null) {
            p.setState(EscrowState.PARTIAL);
        } else {
            p.setState(EscrowState.WAITING_BOTH);
        }
    }

    @Override
    public List<String> markAsRefunded(int applicationId) throws PersistenceException {
        Payment p = findByApplicationId(applicationId);
        List<String> peopleToRefund = new ArrayList<>();

        // Se il pagamento era già rimborsato o completato, non fa nulla
        if (p.getState() == EscrowState.REFUNDED || p.getState() == EscrowState.COMPLETED) {
            return peopleToRefund;
        }

        // Raccoglie gli ID transazione esistenti per restituirli al servizio Stripe
        if (p.getArtistPaymentIntentId() != null) peopleToRefund.add(p.getArtistPaymentIntentId());
        if (p.getManagerPaymentIntentId() != null) peopleToRefund.add(p.getManagerPaymentIntentId());

        p.setState(EscrowState.REFUNDED);

        return peopleToRefund;
    }
}
