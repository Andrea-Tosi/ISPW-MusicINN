package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserDAO {
    User findByIdentifier(String identifier);
    void insertBaseUser(User user, Connection conn) throws SQLException;
}
