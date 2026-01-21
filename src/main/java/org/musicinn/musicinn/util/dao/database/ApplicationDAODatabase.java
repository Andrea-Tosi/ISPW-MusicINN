package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.ApplicationDAO;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ApplicationDAODatabase implements ApplicationDAO {
    @Override
    public void save(Application application, Announcement announcement) throws DatabaseException {
        String sql = "INSERT INTO applications (soundcheck_time, state, artists_username, announcements_id, score) " +
                "VALUES (?, 'PENDING', ?, ?, ?)";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, announcement.getSoundcheckTime());
            pstmt.setString(2, Session.getSingletonInstance().getUsername());
            pstmt.setInt(3, announcement.getId());
            pstmt.setDouble(4, application.getScore());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1062) {
                throw new DatabaseException("Ti sei già candidato a questo annuncio!");
            }
            throw new DatabaseException("Errore nel salvataggio: " + e.getMessage());
        }
    }

    @Override
    public Map<Application, String> findByAnnouncementId(int announcementId) throws DatabaseException {
        Map<Application, String> results = new LinkedHashMap<>(); // Linked per mantenere l'ordine SQL
        String query = "SELECT * FROM applications WHERE announcements_id = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, announcementId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Application app = new Application();
                    app.setId(rs.getInt("id"));
                    app.setScore(rs.getDouble("score"));
                    app.setSoundcheckTime(rs.getObject("soundcheck_time", LocalDateTime.class));
                    app.setState(ApplicationState.valueOf(rs.getString("state")));
                    String artistUser = rs.getString("artists_username");
                    results.put(app, artistUser);
                }
            }
        } catch (SQLException e) {

            throw new DatabaseException("");
        }
        return results;
    }

    @Override
    public void updateApplicationState(Application app) throws DatabaseException {
        String query = "UPDATE applications SET state = ? WHERE id = ? AND state = 'PENDING'";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, app.getState().toString());
            pstmt.setInt(2, app.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Impossibile accettare la candidatura: ID non trovato o già accettato/rifiutato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Errore durante l'aggiornamento dello stato dell'annuncio");
        }
    }
}
