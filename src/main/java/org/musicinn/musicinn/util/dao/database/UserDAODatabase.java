package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.dao.interfaces.UserDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAODatabase implements UserDAO {
    @Override
    public User findByIdentifier(String identifier) throws DatabaseException {
        // La query controlla la presenza dell'utente sia in 'artists' che in 'managers'
        String query = "SELECT u.*, a.username AS artist_check, m.username AS manager_check " +
                "FROM users u " +
                "LEFT JOIN artists a ON u.username = a.username " +
                "LEFT JOIN managers m ON u.username = m.username " +
                "WHERE u.username = ? OR u.email = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String paymentServiceAccountId = rs.getString("payment_service_account_id");

                    // Logica di distinzione
                    if (rs.getString("artist_check") != null) {
                        // È un Artista: qui dovresti recuperare anche gli altri campi
                        // (stage_name, etc.) o fare una query specifica in ArtistDAO
                        return new Artist(username, email, password, paymentServiceAccountId);
                    } else if (rs.getString("manager_check") != null) {
                        // È un Manager
                        return new Manager(username, email, password, paymentServiceAccountId);
                    }
                }
            }
        } catch (SQLException _) {
            throw new DatabaseException("Errore: impossibile trovare utente.");
        }
        return null;
    }

    /**
     * Metodo di utilità per gli altri DAO.
     * Riceve la connessione per partecipare a una transazione esterna.
     */
    public void insertBaseUser(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, payment_service_account_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashedPassword());
            ps.setString(4, user.getPaymentServiceAccountId());
            ps.executeUpdate();
        }
    }
}
