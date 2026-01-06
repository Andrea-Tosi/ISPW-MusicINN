package org.musicinn.musicinn.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Calendar {
    private List<SchedulableEvent> events;

    public List<SchedulableEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SchedulableEvent> events) {
        this.events = events;
    }

    public boolean isAvailable(LocalDate date, LocalTime time, Duration duration) {
        LocalDateTime startRequested = LocalDateTime.of(date, time);
        LocalDateTime endRequested = startRequested.plus(duration);

        // Esempio: Non permettere prenotazioni nel passato
        if (startRequested.isBefore(LocalDateTime.now())) {
            return false;
        }

        // Controlla sovrapposizioni con prenotazioni esistenti
        for (SchedulableEvent event : getEvents()) {
            LocalDateTime startExisting = LocalDateTime.of(event.getStartEventDay(), event.getStartEventTime());
            LocalDateTime endExisting = startExisting.plus(event.getDuration());

            // Formula della sovrapposizione: (Inizio1 < Fine2) AND (Fine1 > Inizio2)
            if (startRequested.isBefore(endExisting) && endRequested.isAfter(startExisting)) {
                return false;
            }
        }
        return true;
    }
}
