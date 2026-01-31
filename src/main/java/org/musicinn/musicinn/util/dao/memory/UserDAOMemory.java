package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.musicinn.musicinn.util.enumerations.MusicalGenre.*;
import static org.musicinn.musicinn.util.enumerations.TypeArtist.*;
import static org.musicinn.musicinn.util.enumerations.TypeArtist.SINGER;
import org.musicinn.musicinn.util.dao.interfaces.UserDAO;

public class UserDAOMemory implements UserDAO {
    protected static final List<User> users = new ArrayList<>();

    static{
        Artist artist = new Artist("mario88", "mario@email.it", "password123", "acct_1SsQr9BApDdKQIqq");
        artist.setStageName("Mario Rossi Band");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(true);
        artist.setCity("Roma");
        artist.setAddress("Via del Corso 10");
        artist.setGenresList(List.of(ROCK, POP, METAL));
        users.add(artist);
        artist = new Artist("elena_singer", "elena@email.it", "password123", "acct_1SvF91BApD9njuiG");
        artist.setStageName("Elena Jazz");
        artist.setTypeArtist(SINGER);
        artist.setDoesUnreleased(false);
        artist.setCity("Milano");
        artist.setAddress("Corso Buenos Aires 5");
        artist.setGenresList(List.of(JAZZ, POP));
        users.add(artist);
        artist = new Artist("art1", "art1@test.it", "pass", null);
        artist.setStageName("The Rockers");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(true);
        artist.setCity("Roma");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art2", "art2@test.it", "pass", null);
        artist.setStageName("Jazz Quartet");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(false);
        artist.setCity("Milano");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art3", "art3@test.it", "pass", null);
        artist.setStageName("DJ Pulse");
        artist.setTypeArtist(DJ);
        artist.setDoesUnreleased(true);
        artist.setCity("Napoli");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art4", "art4@test.it", "pass", null);
        artist.setStageName("Elena Pop");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(true);
        artist.setCity("Torino");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art5", "art5@test.it", "pass", null);
        artist.setStageName("Blues Brothers");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(false);
        artist.setCity("Roma");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art6", "art6@test.it", "pass", null);
        artist.setStageName("Metal Core");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(true);
        artist.setCity("Bologna");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art7", "art7@test.it", "pass", null);
        artist.setStageName("Indie Duo");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(true);
        artist.setCity("Firenze");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art8", "art8@test.it", "pass", null);
        artist.setStageName("Rap Star");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(true);
        artist.setCity("Palermo");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art9", "art9@test.it", "pass", null);
        artist.setStageName("Lounge Band");
        artist.setTypeArtist(BAND);
        artist.setDoesUnreleased(false);
        artist.setCity("Milano");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);
        artist = new Artist("art10", "art10@test.it", "pass", null);
        artist.setStageName("Techno King");
        artist.setTypeArtist(DJ);
        artist.setDoesUnreleased(true);
        artist.setCity("Napoli");
        artist.setAddress(null);
        artist.setGenresList(List.of());
        users.add(artist);

        users.add(new Manager("the_rock_club", "info@rockclub.it", "password123", "acct_1SsQutBApDQC16rO"));
        users.add(new Manager("blue_bar_mgr", "manager@bluebar.it", "password123", "acct_1SvFIgBApDQeHxzg"));
        users.add(new Manager("mgr1", "mgr1@test.it", "pass", null));
        users.add(new Manager("mgr2", "mgr2@test.it", "pass", null));
        users.add(new Manager("mgr3", "mgr3@test.it", "pass", null));
        users.add(new Manager("mgr4", "mgr4@test.it", "pass", null));
        users.add(new Manager("mgr5", "mgr5@test.it", "pass", null));
        users.add(new Manager("mgr6", "mgr6@test.it", "pass", null));
        users.add(new Manager("mgr7", "mgr7@test.it", "pass", null));
        users.add(new Manager("mgr8", "mgr8@test.it", "pass", null));
        users.add(new Manager("mgr9", "mgr9@test.it", "pass", null));
        users.add(new Manager("mgr10", "mgr10@test.it", "pass", null));
    }

    public static List<User> getUsers() {
        return users;
    }

    @Override
    public User findByIdentifier(String identifier) {
        return users.stream()
                .filter(u -> u.getUsername().equals(identifier) || u.getEmail().equals(identifier))
                .findFirst().orElse(null);
    }

    @Override
    public void insertBaseUser(User user, Connection conn) {
        users.add(user);
    }
}
