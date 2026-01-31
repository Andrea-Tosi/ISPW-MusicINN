package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.TypeVenue;

import java.util.ArrayList;
import java.util.List;

public class Venue {
    private int id;
    private String name;
    private String city;
    private String address;
    private TypeVenue typeVenue;
    private boolean isActive;
    private Calendar calendar;
    private ManagerRider rider;
    private List<Announcement> announcements;

    public Venue(String name, String city, String address, TypeVenue typeVenue) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.typeVenue = typeVenue;
        this.isActive = true;
    }

    public Venue() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nameVenue) {
        this.name = nameVenue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addressVenue) {
        this.address = addressVenue;
    }

    public TypeVenue getTypeVenue() {
        return typeVenue;
    }

    public void setTypeVenue(TypeVenue typeVenue) {
        this.typeVenue = typeVenue;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public ManagerRider getRider() {
        return rider;
    }

    public void setRider(ManagerRider rider) {
        this.rider = rider;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public void addAnnouncement(Announcement announcement) {
        if (getAnnouncements() == null) {
            setAnnouncements(new ArrayList<>());
        }
        getAnnouncements().add(announcement);
    }
}
