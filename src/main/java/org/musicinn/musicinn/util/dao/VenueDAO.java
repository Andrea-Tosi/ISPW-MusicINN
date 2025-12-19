package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.model.Venue;

public class VenueDAO {
    public void create(Venue venue) {
        System.out.println("locale " + venue.getName() + " creato");
    }
}
