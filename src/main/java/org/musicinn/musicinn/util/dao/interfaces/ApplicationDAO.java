package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.time.LocalDateTime;

public interface ApplicationDAO {
    void save(Application application, Announcement announcement) throws DatabaseException;
}
//TODO occorre mappare l'EventBean in altri oggetti model prima di invocare save()