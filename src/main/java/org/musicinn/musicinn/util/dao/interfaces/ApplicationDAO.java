package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.List;
import java.util.Map;

public interface ApplicationDAO {
    void save(Application application, Announcement announcement) throws PersistenceException;
    Map<Application, String> findByAnnouncementId(int announcementId) throws PersistenceException;
    void updateApplicationState(Application app) throws PersistenceException;
    Application findAcceptedByAnnouncement(int id) throws PersistenceException;
    List<Application> findAcceptedByArtist(String artistUsername) throws PersistenceException;
}
