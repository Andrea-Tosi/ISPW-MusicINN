package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.SchedulableEvent;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementDAO {
    List<SchedulableEvent> getEventsByDate(LocalDate startingDate) throws PersistenceException;
    List<SchedulableEvent> getConfirmedEventsByDate(LocalDate startingDate) throws PersistenceException;
    void save(Announcement announcement) throws PersistenceException;
    List<Announcement> findOpenAnnouncements(int page, int pageSize) throws PersistenceException;
    List<Announcement> findByManager(String managerUsername) throws PersistenceException;
    void updateAnnouncementState(Announcement ann) throws PersistenceException;
    List<Announcement> findClosedByIdVenue(int venueId) throws PersistenceException;
    Announcement findByApplicationId(int id) throws PersistenceException;
}
