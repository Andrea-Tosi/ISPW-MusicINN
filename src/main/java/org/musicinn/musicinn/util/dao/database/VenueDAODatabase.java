package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.ManagerRider;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VenueDAODatabase implements VenueDAO {
    @Override
    public void create(Venue venue, String managerUsername) {
        String sql = "INSERT INTO venues (name, city, address, type_venue, manager_username) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        // Non uso transazioni qui perché il commit "grosso"
        // è già gestito nel ManagerDAO (o viene chiamato subito dopo)
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getCity());
            ps.setString(3, venue.getAddress());
            ps.setString(4, venue.getTypeVenue().toString());
            ps.setString(5, managerUsername);

            ps.executeUpdate();
            System.out.println("✅ Locale '" + venue.getName() + "' registrato nel DB!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                    throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
                }
            }
        } catch (SQLException e) {
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
                    ManagerRider rider = (ManagerRider) riderDAO.read(Session.UserRole.MANAGER); // se servisse potrei mettere role come ulteriore parametro di read()
                    venue.setRider(rider);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }

        return venue;
    }
}
