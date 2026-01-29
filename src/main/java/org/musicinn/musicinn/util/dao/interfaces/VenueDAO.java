package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

public interface VenueDAO {
    void create(Venue venue, Manager manager);

    int getActiveVenueIdByManager(String managerUsername) throws DatabaseException;

    Venue read(String usernameManager) throws PersistenceException;

    Venue findByApplicationId(int applicationId) throws PersistenceException;

    String findVenueNameByAnnouncementId(int announcementId) throws PersistenceException;
}
