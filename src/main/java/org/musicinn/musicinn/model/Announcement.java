package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.time.LocalTime;
import java.util.List;

public class Announcement extends SchedulableEvent {
    private Double cachet;
    private Double deposit;
    private LocalTime soundcheckTime;
    private List<MusicalGenre> requestedGenres;
    private List<TypeArtist> requestedTypesArtist;
    private String description;
    private AnnouncementState state;

    public Double getCachet() {
        return cachet;
    }

    public void setCachet(Double cachet) {
        this.cachet = cachet;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public LocalTime getSoundcheckTime() {
        return soundcheckTime;
    }

    public void setSoundcheckTime(LocalTime soundcheckTime) {
        this.soundcheckTime = soundcheckTime;
    }

    public List<MusicalGenre> getRequestedGenres() {
        return requestedGenres;
    }

    public void setRequestedGenres(List<MusicalGenre> requestedGenres) {
        this.requestedGenres = requestedGenres;
    }

    public List<TypeArtist> getRequestedTypesArtist() {
        return requestedTypesArtist;
    }

    public void setRequestedTypesArtist(List<TypeArtist> requestedTypesArtist) {
        this.requestedTypesArtist = requestedTypesArtist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnnouncementState getState() {
        return state;
    }

    public void setState(AnnouncementState state) {
        this.state = state;
    }
}
