package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Payment;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.PaymentDAO;
import org.musicinn.musicinn.util.enumerations.EscrowState;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAODatabase implements PaymentDAO {
    @Override
    public void save(int applicationId, int daysOfDeadline) throws DatabaseException {
        // 1. Definiamo la query
        String sql = "INSERT INTO payments (applications_id, escrow_state, payment_deadline) VALUES (?, 'WAITING_BOTH', ?)";

        // 2. Calcoliamo la deadline (5 giorni da adesso)
        LocalDateTime deadline = LocalDateTime.now().plusDays(daysOfDeadline);

        // 3. Recuperiamo la connessione Singleton
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Impostiamo l'ID dell'applicazione (o agreement)
            ps.setInt(1, applicationId);

            ps.setObject(2, deadline);

            // Esecuzione
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il salvataggio del pagamento.");
        }
    }

    @Override
    public Payment findByApplicationId(int applicationId) throws DatabaseException {
        Payment payment = null;
        String sql = "SELECT * FROM payments WHERE applications_id = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payment = new Payment();
                    payment.setPaymentDeadline(rs.getTimestamp("payment_deadline").toLocalDateTime());
                    payment.setState(EscrowState.valueOf(rs.getString("escrow_state")));
                    payment.setManagerPaymentIntentId(rs.getString("venue_payment_intent_id"));
                    payment.setArtistPaymentIntentId(rs.getString("artist_payment_intent_id"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero del record di pagamento.");
        }
        return payment;
    }

    @Override
    public void updatePaymentState(int applicationId, Session.UserRole role, String transactionId) throws DatabaseException {
        // Determiniamo quale colonna aggiornare in base a chi sta pagando
        String column = (role.equals(Session.UserRole.ARTIST)) ? "artist_payment_intent_id" : "venue_payment_intent_id";

        // Query 1: Aggiorna il "bollino" del pagamento per l'utente corrente
        String updateSql = "UPDATE payments SET " + column + " = ? WHERE applications_id = ?";

        // Query 2: Controlla se entrambi hanno pagato e, in caso, chiudi l'operazione
        String finalizeSql = "UPDATE payments SET escrow_state = CASE " +
                "  WHEN artist_payment_intent_id IS NOT NULL AND venue_payment_intent_id IS NOT NULL THEN 'SECURED' " +
                "  WHEN artist_payment_intent_id IS NOT NULL OR venue_payment_intent_id IS NOT NULL THEN 'PARTIAL' " +
                "  ELSE 'WAITING_BOTH' " +
                "END " +
                "WHERE applications_id = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try {
            // Usiamo una transazione per essere sicuri che entrambi gli update vadano a buon fine
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, transactionId);
                psUpdate.setInt(2, applicationId);
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psFinalize = conn.prepareStatement(finalizeSql)) {
                psFinalize.setInt(1, applicationId);
                psFinalize.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* log */ }
            throw new DatabaseException("Errore nel salvataggio del pagamento.");
        }
    }

    public List<String> markAsRefunded(int applicationId) throws PersistenceException {
        List<String> peopleToRefund = new ArrayList<>();

        // 1. Recuperiamo gli ID transazione prima di resettare o cambiare stato
        String selectSql = "SELECT artist_payment_intent_id, venue_payment_intent_id FROM payments " +
                "WHERE applications_id = ? AND escrow_state IN ('WAITING_BOTH', 'PARTIAL') " +
                "AND payment_deadline < NOW()";

        // 2. Aggiorniamo lo stato
        String updateSql = "UPDATE payments SET escrow_state = 'REFUNDED' WHERE applications_id = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement psSel = conn.prepareStatement(selectSql);
             PreparedStatement psUp = conn.prepareStatement(updateSql)) {
            conn.setAutoCommit(false);

            psSel.setInt(1, applicationId);
            try (ResultSet rs = psSel.executeQuery()){
                if (rs.next()) {
                    if (rs.getString(1) != null) peopleToRefund.add(rs.getString(1));
                    if (rs.getString(2) != null) peopleToRefund.add(rs.getString(2));
                } else {
                    conn.rollback();
                    return peopleToRefund;
                }
            }

            psUp.setInt(1, applicationId);
            psUp.executeUpdate();

            conn.commit();

            return peopleToRefund;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
            throw new DatabaseException("Errore durante l'aggiornamento dello stato del pagamento.");
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e){
                System.err.println(e.getMessage());;
            }
        }
    }
}