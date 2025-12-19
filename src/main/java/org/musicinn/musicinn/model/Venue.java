package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.TypeVenue;

public class Venue {
    private String name;
    private String city;
    private String address;
    private TypeVenue type;
    private boolean isActive;

    public Venue(String name, String city, String address, TypeVenue typeVenue) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.type = typeVenue;
        this.isActive = true;
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

    public TypeVenue getType() {
        return type;
    }

    public void setType(TypeVenue type) {
        this.type = type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
