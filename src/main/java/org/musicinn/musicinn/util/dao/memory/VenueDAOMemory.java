package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

public class VenueDAOMemory implements VenueDAO {
    @Override
    public void create(Venue venue, Manager manager) {
        System.out.println("locale " + venue.getName() + ", gestito da " + manager.getUsername() + " creato");
    }

    @Override
    public int getActiveVenueIdByManager(String managerUsername) throws DatabaseException {
        return 0;
    }

    @Override
    public Venue read(String usernameManager) throws DatabaseException {
        return null;
    }

    @Override
    public Venue findByApplicationId(int applicationId) throws DatabaseException {
        return null;
    }
}
