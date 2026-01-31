package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.ArtistDAO;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.sql.SQLException;
import java.util.List;

import static org.musicinn.musicinn.util.dao.memory.UserDAOMemory.users;

public class ArtistDAOMemory implements ArtistDAO {
    @Override
    public void create(Artist artist) {
        try {
            DAOFactory.getUserDAO().insertBaseUser(artist, null);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Artist read(String username) {
        try {
            return (Artist) DAOFactory.getUserDAO().findByIdentifier(username);
        } catch (PersistenceException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<MusicalGenre> loadArtistGenres(String username) {
        Artist a = read(username);
        return a != null ? a.getGenresList() : List.of();
    }

    @Override
    public String findStageNameByAnnouncementId(int announcementId) {
        List<Application> allApps = ApplicationDAOMemory.getApplications();
        Application acceptedApp = allApps.stream()
                .filter(app -> app.getState() == ApplicationState.ACCEPTED)
                .filter(app -> app.getId() == announcementId)
                .findFirst().orElse(null);

        if (acceptedApp == null) return null;

        String username = acceptedApp.getUsernameArtist();

        Artist artist = (Artist) users.stream()
                .filter(u -> u instanceof Artist && u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        return (artist != null) ? artist.getStageName() : null;
    }
}
