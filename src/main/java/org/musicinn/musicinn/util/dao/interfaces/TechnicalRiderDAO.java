package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.TechnicalRider;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

public interface TechnicalRiderDAO {
    void create(TechnicalRider rider) throws PersistenceException;
    TechnicalRider read(String username, Session.UserRole role) throws PersistenceException;
}
