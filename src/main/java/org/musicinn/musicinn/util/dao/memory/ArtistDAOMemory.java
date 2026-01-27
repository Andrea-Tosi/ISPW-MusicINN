package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.util.dao.interfaces.ArtistDAO;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.List;

public class ArtistDAOMemory implements ArtistDAO {
    @Override
    public void create(Artist artist) {
        System.out.println("artista " + artist.getUsername() + " creato");
    }

    @Override
    public Artist read(String username) throws DatabaseException {
        return null;
    }

    @Override
    public List<MusicalGenre> loadArtistGenres(String username) throws DatabaseException {
        return List.of();
    }

    @Override
    public String findStageNameByAnnouncementId(int announcementId) throws DatabaseException {
        return "";
    }
}
