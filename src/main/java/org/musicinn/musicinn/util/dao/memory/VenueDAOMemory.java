package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

public class VenueDAOMemory implements VenueDAO {
    @Override
    public void create(Venue venue, String managerUsername) {
        System.out.println("locale " + venue.getName() + ", gestito da " + managerUsername + " creato");
    }

    @Override
    public Venue read(String usernameManager) throws DatabaseException {
        return null;
    }
}
