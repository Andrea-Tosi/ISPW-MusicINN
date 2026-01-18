package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.database.*;
import org.musicinn.musicinn.util.dao.interfaces.*;
import org.musicinn.musicinn.util.dao.memory.*;

public class DAOFactory {
    // Metodo privato per capire se siamo in modalità Demo
    private static boolean isDemo() {
        return Session.getSingletonInstance().getPersistenceType().equals(Session.PersistenceType.MEMORY);
    }

    public static UserDAO getUserDAO() {
        if (isDemo()) return new UserDAOMemory();
        return new UserDAODatabase();
    }

    public static ArtistDAO getArtistDAO() {
        if (isDemo()) return new ArtistDAOMemory();
        return new ArtistDAODatabase();
    }

    public static ManagerDAO getManagerDAO() {
        if (isDemo()) return new ManagerDAOMemory();
        return new ManagerDAODatabase();
    }

    public static VenueDAO getVenueDAO() {
        if (isDemo()) return new VenueDAOMemory();
        return new VenueDAODatabase();
    }

    public static TechnicalRiderDAO getTechnicalRiderDAO() {
        if (isDemo()) return new TechnicalRiderDAOMemory();
        return new TechnicalRiderDAODatabase();
    }

    public static AnnouncementDAO getAnnouncementDAO() {
        if (isDemo()) return new AnnouncementDAOMemory();
        return new AnnouncementDAODatabase();
    }

    public static ApplicationDAO getApplicationDAO() {
        if (isDemo()) return new ApplicationDAOMemory();
        return new ApplicationDAODatabase();
    }
}
