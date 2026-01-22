package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeVenue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.musicinn.musicinn.util.enumerations.TypeArtist.SINGER;
import org.musicinn.musicinn.util.dao.interfaces.UserDAO;

public class UserDAOMemory implements UserDAO {
    private static final Map<String, User> utentiRegistrati = new HashMap<>();
    static{
        Venue venueA = new Venue("The Rock Club", "Roma", "Via del Corso 10", TypeVenue.CLUB);
        Venue venueB = new Venue("Jazz Corner", "Milano", "Via Dante 5", TypeVenue.PUB);

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
        artist1.setGenresList(List.of(MusicalGenre.FUNK, MusicalGenre.ROCK, MusicalGenre.POP, MusicalGenre.SOUL));
        utentiRegistrati.put(artist1.getUsername(), artist1);

        Manager manager1 = new Manager(
                "manager1",
                "h@z.it",
                "PASSWORD"
        );
        utentiRegistrati.put(manager1.getUsername(), manager1);
    }

    @Override
    public User findByIdentifier(String identifier){
        // Prova a cercare per username (chiave della mappa)
        User user = utentiRegistrati.get(identifier);

        // Se non lo trova, cerca tra i valori per email
        if (user == null) {
            user = utentiRegistrati.values().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(identifier))
                    .findFirst()
                    .orElse(null);
        }
        return user;
    }

    @Override
    public void insertBaseUser(User user, Connection conn) throws SQLException {

    }
}
