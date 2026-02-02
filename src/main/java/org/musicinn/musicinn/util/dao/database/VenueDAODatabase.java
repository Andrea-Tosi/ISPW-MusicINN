package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.ManagerRider;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.ManagerDAO;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.*;
import java.util.logging.Logger;

public class VenueDAODatabase implements VenueDAO {
    private static final Logger LOGGER = Logger.getLogger(VenueDAODatabase.class.getName());

    @Override
    public void create(Venue venue, Manager manager) {
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            // Crea User e Manager (active_venue è NULL per ora)
            ManagerDAO managerDAO = DAOFactory.getManagerDAO();
            managerDAO.create(manager);

            // Inserisce Venue e recupera l'ID generato
            int venueId = insertVenueAndGetId(venue, manager.getUsername());

            // Aggiorna il Manager con l'ID della Venue attiva
            updateManagerActiveVenue(manager.getUsername(), venueId, conn);

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.fine(ex.getMessage());
            }
            LOGGER.fine(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.fine(e.getMessage());
            }
        }
    }

    private int insertVenueAndGetId(Venue venue, String managerUsername) throws SQLException {
        String sql = "INSERT INTO venues (name, city, address, type_venue, manager_username, is_active) VALUES (?, ?, ?, ?, ?, 1)";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getCity());
            ps.setString(3, venue.getAddress());
            ps.setString(4, venue.getTypeVenue().toString());
            ps.setString(5, managerUsername);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Restituisce l'ID generato da MySQL
                } else {
                    throw new SQLException("Errore: ID Venue non generato.");
                }
            }
        }
    }

private void updateManagerActiveVenue(String username, int venueId, Connection conn) throws SQLException {
    String sql = "UPDATE managers SET active_venue = ? WHERE username = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, venueId);
        ps.setString(2, username);

        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new SQLException("Aggiornamento manager fallito: manager non trovato.");
        }
    }
}

    @Override
    public int getActiveVenueIdByManager(String managerUsername) throws DatabaseException {
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();
        // Cerchiamo l'ID basandoci sullo username del manager e sullo stato del locale
        String sql = "SELECT id FROM venues WHERE manager_username = ? AND is_active = 1 LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerUsername);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    // Se un manager non ha una venue attiva, non può avere un rider
                    throw new DatabaseException("Errore: Il gestore non ha locali attivi.");
                }
            }
        } catch (SQLException _) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
    }

    @Override
    public Venue read(String usernameManager) throws DatabaseException {
        Venue venue = null;
        String query = "SELECT name, city, address, type_venue FROM venues WHERE manager_username = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, usernameManager);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    venue = new Venue();
                    venue.setName(rs.getString("name"));
                    venue.setCity(rs.getString("city"));
                    venue.setAddress(rs.getString("address"));
                    venue.setTypeVenue(TypeVenue.valueOf(rs.getString("type_venue")));

                    TechnicalRiderDAODatabase riderDAO = new TechnicalRiderDAODatabase();
                    ManagerRider rider = (ManagerRider) riderDAO.read(usernameManager, Session.UserRole.MANAGER);
                    venue.setRider(rider);
                }
            }
        } catch (SQLException _) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }

        return venue;
    }

    public Venue findByApplicationId(int applicationId) throws DatabaseException {
        Venue venue = null;

        // Query con JOIN per risalire al locale partendo dall'ID dell'annuncio
        String sql = "SELECT v.id, v.name " +
                "FROM venues v " +
                "JOIN announcements a ON v.id = a.venues_id " +
                "WHERE a.id = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    venue = new Venue();
                    venue.setName(rs.getString("name"));
                }
            }
        } catch (SQLException _) {
            throw new DatabaseException("Errore nel recupero del locale.");
        }

        return venue;
    }

    @Override
    public String findVenueNameByAnnouncementId(int announcementId) throws DatabaseException {
        String sql = "SELECT v.name FROM venues v " +
                "JOIN announcements a ON v.id = a.venues_id " +
                "WHERE a.id = ?";

        String venueName = null;
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, announcementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    venueName = rs.getString("name");
                } else {
                    // Opzionale: gestire il caso in cui l'annuncio non esista
                    throw new DatabaseException("Nessun locale trovato per l'annuncio.");
                }
            }
            return venueName;
        } catch (SQLException _) {
            throw new DatabaseException("Errore nel recupero del nome del locale.");
        }
    }
}
//TODO correggere nel modello relazionale del database il fatto che nella relazione tra managers e venues, la chiave esterna è active_venue che coincide con l'id del locale attivo