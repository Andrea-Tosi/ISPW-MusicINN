package org.musicinn.musicinn.model;

import org.musicinn.musicinn.model.observer_pattern.Observer;
import org.musicinn.musicinn.model.observer_pattern.Subject;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Announcement extends SchedulableEvent implements Subject {
    private Double cachet;
    private Double deposit;
    private LocalDateTime soundcheckTime;
    private List<MusicalGenre> requestedGenres;
    private List<TypeArtist> requestedTypesArtist;
    private Boolean doesUnreleased;
    private String description;
    private AnnouncementState state;
    private Venue venue;
    private List<Observer> applicationList;
    private int numOfApplications;

    public Announcement() {
        this.requestedGenres = new ArrayList<>();
        this.requestedTypesArtist = new ArrayList<>();
        this.applicationList = new ArrayList<>();
    }

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

    public LocalDateTime getSoundcheckTime() {
        return soundcheckTime;
    }

    public void setSoundcheckTime(LocalDateTime soundcheckTime) {
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

    public Boolean getDoesUnreleased() {
        return doesUnreleased;
    }

    public void setDoesUnreleased(Boolean doesUnreleased) {
        this.doesUnreleased = doesUnreleased;
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
        if (this.state.equals(AnnouncementState.CLOSED)) {
            notifyObservers();
        }
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List<Observer> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<Observer> applicationList) {
        this.applicationList = applicationList;
    }

    public int getNumOfApplications() {
        return numOfApplications;
    }

    public void setNumOfApplications(int numOfApplications) {
        this.numOfApplications = numOfApplications;
    }

    @Override
    public void addObserver(Observer observer) {
        applicationList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        applicationList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : applicationList) {
            observer.update(state);
        }
    }
}
