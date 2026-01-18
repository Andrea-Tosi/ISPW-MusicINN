package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.User;

public interface UserDAO {
    User findByIdentifier(String identifier);
}
