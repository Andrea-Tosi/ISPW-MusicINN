package org.musicinn.musicinn.util.bean;

import org.musicinn.musicinn.model.ValidationResult;
import org.musicinn.musicinn.util.bean.technical_rider_bean.TechnicalRiderBean;
import org.musicinn.musicinn.util.enumerations.TypeVenue;

public class EventBean {
    private TechnicalRiderBean technicalRiderBean;
    private AnnouncementBean announcementBean;
    private String venueName;
    private TypeVenue typeVenue;
    private String venueAddress;
    private String venueCity;
    private int distance; // distanza tra locale e artista utente (visibile durante lo UC Candidati)
    private ValidationResult report;

    public EventBean() {
        this.technicalRiderBean = new TechnicalRiderBean();
        this.announcementBean = new AnnouncementBean();
    }

    public TechnicalRiderBean getTechnicalRiderBean() {
        return technicalRiderBean;
    }

    public void setTechnicalRiderBean(TechnicalRiderBean technicalRiderBean) {
        this.technicalRiderBean = technicalRiderBean;
    }

    public AnnouncementBean getAnnouncementBean() {
        return announcementBean;
    }

    public void setAnnouncementBean(AnnouncementBean announcementBean) {
        this.announcementBean = announcementBean;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public TypeVenue getTypeVenue() {
        return typeVenue;
    }

    public void setTypeVenue(TypeVenue typeVenue) {
        this.typeVenue = typeVenue;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }


    public ValidationResult getReport() {
        return report;
    }

    public void setReport(ValidationResult report) {
        this.report = report;
    }
}
