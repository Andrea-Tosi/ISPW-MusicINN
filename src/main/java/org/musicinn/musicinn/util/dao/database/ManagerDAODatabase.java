package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.dao.interfaces.ManagerDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManagerDAODatabase implements ManagerDAO {
    @Override
    public void create(Manager manager) {
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();
        try {
            conn.setAutoCommit(false);

            // Delega a UserDAO
            new UserDAODatabase().insertBaseUser(manager, conn);

            // Specifica del Manager
            String sql = "INSERT INTO managers (username) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manager.getUsername());
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
