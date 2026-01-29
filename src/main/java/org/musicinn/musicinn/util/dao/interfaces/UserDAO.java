package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserDAO {
    User findByIdentifier(String identifier) throws PersistenceException;
    void insertBaseUser(User user, Connection conn) throws SQLException;
}
