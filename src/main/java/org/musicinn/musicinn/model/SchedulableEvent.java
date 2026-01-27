package org.musicinn.musicinn.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public abstract class SchedulableEvent {
    private int id;
    private LocalDate startEventDay;
    private LocalDate endEventDay;
    private LocalTime startEventTime;
    private LocalTime endEventTime;
    private Duration duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartEventDay() {
        return startEventDay;
    }

    public void setStartEventDay(LocalDate startEventDay) {
        this.startEventDay = startEventDay;
    }

    public LocalDate getEndEventDay() {
        return endEventDay;
    }

    public void setEndEventDay(LocalDate endEventDay) {
        this.endEventDay = endEventDay;
    }

    public LocalTime getStartEventTime() {
        return startEventTime;
    }

    public void setStartEventTime(LocalTime startEventTime) {
        this.startEventTime = startEventTime;
    }

    public LocalTime getEndEventTime() {
        return endEventTime;
    }

    public void setEndEventTime(LocalTime endEventTime) {
        this.endEventTime = endEventTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
