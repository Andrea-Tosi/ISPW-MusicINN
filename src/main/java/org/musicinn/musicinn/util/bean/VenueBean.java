package org.musicinn.musicinn.util.bean;

import org.musicinn.musicinn.util.bean.technical_rider_bean.TechnicalRiderBean;
import org.musicinn.musicinn.util.enumerations.TypeVenue;

public class VenueBean {
    private String name;
    private String city;
    private String address;
    private TypeVenue typeVenue;
    private TechnicalRiderBean rider;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public TypeVenue getTypeVenue() {
        return typeVenue;
    }

    public void setTypeVenue(TypeVenue typeVenue) {
        this.typeVenue = typeVenue;
    }

    public TechnicalRiderBean getRider() {
        return rider;
    }

    public void setRider(TechnicalRiderBean rider) {
        this.rider = rider;
    }
}
