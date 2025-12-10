package org.musicinn.musicinn.util.LoginBean;

import org.musicinn.musicinn.util.enumerations.TypeVenue;

public class ManagerRegistrationBean extends UserRegistrationBean{
    private String nameVenue;
    private String city;
    private String addressVenue;
    private TypeVenue typeVenue;

    public ManagerRegistrationBean(String identifier, String password) {
        super(identifier, password);
    }

    public String getNameVenue() {
        return nameVenue;
    }

    public void setNameVenue(String nameVenue) {
        this.nameVenue = nameVenue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressVenue() {
        return addressVenue;
    }

    public void setAddressVenue(String addressVenue) {
        this.addressVenue = addressVenue;
    }

    public TypeVenue getTypeVenue() {
        return typeVenue;
    }

    public void setTypeVenue(TypeVenue typeVenue) {
        this.typeVenue = typeVenue;
    }
}
