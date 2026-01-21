package org.musicinn.musicinn.util.bean;

import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementBean {
    private int id;
    private LocalDate startingDate;
    private LocalTime startingTime;
    private Duration duration;
    private Double cachet;
    private Double deposit;
    private LocalDateTime soundcheckTime;
    private List<MusicalGenre> requestedGenres;
    private List<TypeArtist> requestedTypesArtist;
    private Boolean doesUnreleased;
    private String description;
    private int numOfApplications;

    public AnnouncementBean() {
        this.requestedGenres = new ArrayList<>();
        this.requestedTypesArtist = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
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

    public int getNumOfApplications() {
        return numOfApplications;
    }

    public void setNumOfApplications(int numOfApplications) {
        this.numOfApplications = numOfApplications;
    }
}
