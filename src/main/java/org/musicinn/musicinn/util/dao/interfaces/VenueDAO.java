package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

public interface VenueDAO {
    void create(Venue venue, String managerUsername);
    Venue read(String usernameManager) throws DatabaseException;
}
