package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.model.Artist;

public class ArtistDAO {
    public void create(Artist artist) {
        System.out.println("artista " + artist.getUsername() + " creato");
    }
}
