package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.ApplicationState;

import java.time.LocalDateTime;

public class Application {
    private int id;
    private LocalDateTime soundcheckTime;
    private ApplicationState state; //TODO da applicare pattern observer
    private Double score;

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
}
