package org.musicinn.musicinn.model;

import org.musicinn.musicinn.model.observer_pattern.Observer;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.ApplicationState;

import java.time.LocalDateTime;

public class Application implements Observer {
    private int id;
    private LocalDateTime soundcheckTime;
    private ApplicationState state;
    private Double score;
    private Payment payment;
    private String usernameArtist;
    //TODO private List<Application> applications;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getSoundcheckTime() {
        return soundcheckTime;
    }

    public void setSoundcheckTime(LocalDateTime soundcheckTime) {
        this.soundcheckTime = soundcheckTime;
    }

    public ApplicationState getState() {
        return state;
    }

    public void setState(ApplicationState state) {
        this.state = state;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public void update(AnnouncementState announcementState) {
        if (announcementState.equals(AnnouncementState.CLOSED) && state.equals(ApplicationState.PENDING)) {
            state = ApplicationState.REJECTED;
        }
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getUsernameArtist() {
        return usernameArtist;
    }

    public void setUsernameArtist(String usernameArtist) {
        this.usernameArtist = usernameArtist;
    }
}
