package org.musicinn.musicinn.model;

import java.util.List;

public class Manager extends User{
    private Venue activeVenue;
    private List<Venue> venueList;

    public Manager(String username, String email,String password, Venue activeVenue) {
        super(username, email, password);
        this.activeVenue = activeVenue;
    }

    public Venue getActiveVenue() {
        return activeVenue;
    }

    public void setActiveVenue(Venue activeVenue) {
        this.activeVenue = activeVenue;
    }

    public List<Venue> getVenueList() {
        return venueList;
    }

    public void setVenueList(List<Venue> venueList) {
        this.venueList = venueList;
    }
}
