package org.musicinn.musicinn.util.bean.login_bean;

import org.musicinn.musicinn.util.enumerations.TypeVenue;

public class ManagerRegistrationBean {
    private String nameVenue;
    private String cityVenue;
    private String addressVenue;
    private TypeVenue typeVenue;

    public ManagerRegistrationBean(String nameVenue, String cityVenue, String addressVenue, TypeVenue typeVenue) {
        this.nameVenue = nameVenue;
        this.cityVenue = cityVenue;
        this.addressVenue = addressVenue;
        this.typeVenue = typeVenue;
    }

    public String getNameVenue() {
        return nameVenue;
    }

    public void setNameVenue(String nameVenue) {
        this.nameVenue = nameVenue;
    }

    public String getCityVenue() {
        return cityVenue;
    }

    public void setCityVenue(String cityVenue) {
        this.cityVenue = cityVenue;
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
