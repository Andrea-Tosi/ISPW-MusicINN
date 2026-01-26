package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

public interface VenueDAO {
    void create(Venue venue, Manager manager);

    int getActiveVenueIdByManager(String managerUsername) throws DatabaseException;

    Venue read(String usernameManager) throws DatabaseException;

    Venue findByApplicationId(int applicationId) throws DatabaseException;
}
