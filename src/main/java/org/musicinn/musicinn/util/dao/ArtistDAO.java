package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.SocialAccount;

public class ArtistDAO {
    public void create(Artist artist) {
        System.out.println("artista " + artist.getStageName() + " creato");

        if (artist.getSocialAccounts() != null) {
            SocialAccountDAO socialAccountDAO = new SocialAccountDAO();
            for (SocialAccount social : artist.getSocialAccounts()) {
                socialAccountDAO.save(social, artist.getUsername()); // Username dell'artista passato come chiave esterna
            }
        }
    }
}
