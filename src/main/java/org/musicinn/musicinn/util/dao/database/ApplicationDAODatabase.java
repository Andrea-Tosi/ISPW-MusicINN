package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.ApplicationDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
