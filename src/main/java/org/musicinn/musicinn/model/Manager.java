package org.musicinn.musicinn.model;

import java.util.ArrayList;
import java.util.List;

public class Manager extends User{
    private Venue activeVenue;
    private List<Venue> venueList;

    public Manager(String username, String email,String password, String paymentServiceAccountId) {
        super(username, email, password, paymentServiceAccountId);
        venueList = new ArrayList<>();
    }

    public Manager(String username, String email, String password) {
        super(username, email, password);
        venueList = new ArrayList<>();
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
