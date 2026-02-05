package org.musicinn.musicinn.model;

import org.musicinn.musicinn.model.observer_pattern.Observer;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;

import java.time.LocalDateTime;
import java.util.List;

public class Application implements Observer {
    private int id;
    private LocalDateTime soundcheckTime;
    private ApplicationState state;
    private Double score;
    private Payment payment;
    private String usernameArtist;

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

    public void calculateAndSetScore(List<MusicalGenre> artistGenres, List<MusicalGenre> requestedGenres) {
        if (requestedGenres == null || requestedGenres.isEmpty()) {
            this.score = 0.0;
            return;
        }
        // Conta i generi dell'artista presenti nella lista dei richiesti
        long inCommon = requestedGenres.stream()
                .filter(artistGenres::contains)
                .count();

        // Calcola la percentuale
        this.score = (inCommon * 100.0) / requestedGenres.size();
    }
}
