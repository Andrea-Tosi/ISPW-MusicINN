package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.SchedulableEvent;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementDAO {
    List<SchedulableEvent> getEventsByDate(LocalDate startingDate) throws DatabaseException;
    void save(Announcement announcement) throws DatabaseException;
    List<Announcement> findActiveByGenres(List<MusicalGenre> artistGenres, int page, int pageSize) throws DatabaseException;
    void updateAnnouncementState(Announcement ann) throws DatabaseException;
    List<Announcement> findClosedByIdVenue(int venueId) throws DatabaseException;
    Announcement findByApplicationId(int id) throws DatabaseException;
}
