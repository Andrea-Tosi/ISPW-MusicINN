package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.Map;

public interface ApplicationDAO {
    void save(Application application, Announcement announcement) throws DatabaseException;
    Map<Application, String> findByAnnouncementId(int announcementId) throws DatabaseException;
    void updateApplicationState(Application app) throws DatabaseException;
}
