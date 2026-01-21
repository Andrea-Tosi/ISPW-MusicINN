package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.util.dao.interfaces.ApplicationDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.List;
import java.util.Map;

public class ApplicationDAOMemory implements ApplicationDAO {
    public void save(Application application, Announcement announcement) {
        System.out.println("candidatura che ha come data di soundcheck '" + application + "' salvata");
    }

    @Override
    public Map<Application, String> findByAnnouncementId(int announcementId) throws DatabaseException {
        return null;
    }

    @Override
    public void updateApplicationState(Application app) throws DatabaseException {

    }
}
