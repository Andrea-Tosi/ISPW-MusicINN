package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.ApplicationState;

import java.time.LocalDateTime;

public class Application {
    private LocalDateTime soundcheckTime;
    private ApplicationState state;
    private Double score;

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
}
