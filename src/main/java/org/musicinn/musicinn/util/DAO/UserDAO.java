package org.musicinn.musicinn.util.DAO;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.model.Venue;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static org.musicinn.musicinn.util.enumerations.TypeArtist.SINGER;

public class UserDAO {
    private static final Map<String, User> utentiRegistrati = new HashMap<>();
    static{
        Venue venueA = new Venue("The Rock Club", "Roma", "Via del Corso 10");
        Venue venueB = new Venue("Jazz Corner", "Milano", "Via Dante 5");

        Artist artist1 = new Artist(
                "artist1",
                "h@z.com",
                "password",
                "artist",
                SINGER,
                true,
                "Rome",
                "Via Roma, 67"
        );
        utentiRegistrati.put(artist1.getUsername(), artist1);

        Manager manager1 = new Manager(
                "manager1",
                "h@z.it",
                "PASSWORD",
                venueB
        );
        utentiRegistrati.put(manager1.getUsername(), manager1);
    }

    public User findByIdentifier(String identifier){
        return utentiRegistrati.get(identifier);
    }
}
