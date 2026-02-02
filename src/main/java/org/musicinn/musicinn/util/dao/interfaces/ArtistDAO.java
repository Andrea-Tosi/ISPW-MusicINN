package org.musicinn.musicinn.util.dao.interfaces;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.List;

public interface ArtistDAO {
    void create(Artist artist);
    Artist read(String username) throws PersistenceException;
    List<MusicalGenre> loadArtistGenres(String username) throws PersistenceException;
    String findStageNameByAnnouncementId(int announcementId) throws PersistenceException;
}
