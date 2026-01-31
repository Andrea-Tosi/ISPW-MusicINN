package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.AnnouncementDAO;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnnouncementDAOMemory implements AnnouncementDAO {
    private static final List<Announcement> announcements = new ArrayList<>();
    private static int idCounter = 1;


    static {
        Venue venue = new Venue("The Rock Club", "Roma", "Via del Corso 10", TypeVenue.CLUB);
        TechnicalRiderDAOMemory dao = new TechnicalRiderDAOMemory();
        ManagerRider rider = (ManagerRider) dao.read(Session.getSingletonInstance().getUser().getUsername(), Session.UserRole.MANAGER);
        venue.setRider(rider);

        // Esempio Annuncio 1: Compatibile
        Announcement ann1 = new Announcement();
        ann1.setState(AnnouncementState.OPEN);
        ann1.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
        ann1.setStartEventDay(LocalDate.now().plusDays(7));
        ann1.setStartEventTime(LocalTime.of(21, 30));
        ann1.setDuration(Duration.ofMinutes(100));
        ann1.setCachet(200.0);
        ann1.setDeposit(100.0);
        ann1.setDescription(".");
        ann1.setVenue(venue);
        ann1.setDoesUnreleased(true);
        ann1.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
        announcements.add(ann1);

        // Esempio Annuncio 2: Chiuso (Verrà scartato)
        Announcement ann2 = new Announcement();
        ann2.setState(AnnouncementState.CLOSED);
        ann2.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
        ann2.setStartEventDay(LocalDate.now().plusDays(8));
        ann2.setStartEventTime(LocalTime.of(21, 30));
        ann2.setDuration(Duration.ofMinutes(100));
        ann2.setCachet(200.0);
        ann2.setDeposit(100.0);
        ann2.setDescription(".");
        ann2.setVenue(venue);
        ann2.setDoesUnreleased(true);
        ann2.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
        announcements.add(ann2);

        // Esempio Annuncio 3: Genere diverso (Verrà scartato se l'artista non fa JAZZ)
        Announcement ann3 = new Announcement();
        ann3.setState(AnnouncementState.OPEN);
        ann3.setRequestedGenres(List.of(MusicalGenre.JAZZ));
        ann3.setStartEventDay(LocalDate.now().plusDays(9));
        ann3.setStartEventTime(LocalTime.of(21, 30));
        ann3.setDuration(Duration.ofMinutes(100));
        ann3.setCachet(200.0);
        ann3.setDeposit(100.0);
        ann3.setDescription(".");
        ann3.setVenue(venue);
        ann3.setDoesUnreleased(true);
        ann3.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
        announcements.add(ann3);

        for (int i = 0; i < 30; i++) {
            Announcement ann = new Announcement();
            ann.setState(AnnouncementState.OPEN);
            ann.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
            ann.setStartEventDay(LocalDate.now().plusDays(10));
            ann.setStartEventTime(LocalTime.of(21, 30));
            ann.setDuration(Duration.ofMinutes(100));
            ann.setCachet(200.0);
            ann.setDeposit(100.0);
            ann.setDescription(".");
            ann.setVenue(venue);
            ann.setDoesUnreleased(true);
            ann.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
            announcements.add(ann);
        }

    }

    public static List<Announcement> getAnnouncements() {
        return announcements;
    }

    @Override
    public void save(Announcement announcement) {
        announcement.setId(++idCounter);
        announcements.add(announcement);
    }

    public List<SchedulableEvent> getEventsByDate(LocalDate date) {
        return announcements.stream()
                .filter(a -> a.getStartEventDay().equals(date))
                .map(a -> (SchedulableEvent) a).toList();
    }

    @Override
    public List<SchedulableEvent> getConfirmedEventsByDate(LocalDate startingDate) {
        if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
            return getConfirmedEventsByDateForArtist(startingDate);
        } else if (Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER)) {
            return getConfirmedEventsByDateForManager(startingDate);
        }
        return null;
    }

    public List<SchedulableEvent> getConfirmedEventsByDateForArtist(LocalDate startingDate) {
        return announcements.stream()
                .filter(ann -> ann.getStartEventDay().equals(startingDate))
                .filter(ann -> ann.getApplicationList().stream()
                        .anyMatch(obs -> {
                            Application app = (Application) obs;
                            return app.getUsernameArtist().equals(Session.getSingletonInstance().getUser().getUsername())
                                    && app.getState().toString().equals("ACCEPTED");
                        }))
                .map(ann -> (SchedulableEvent) ann)
                .toList();
    }

    public List<SchedulableEvent> getConfirmedEventsByDateForManager(LocalDate startingDate) {
        // Per il manager: scansiono i manager, trovo quello corrente, guardo le sue venue
        // e prendo gli annunci chiusi per quella data.
        Manager manager = (Manager) UserDAOMemory.users.stream()
                .filter(u -> u instanceof Manager && u.getUsername().equals(Session.getSingletonInstance().getUser().getUsername()))
                .findFirst().orElse(null);

        if (manager == null || manager.getActiveVenue() == null) return new ArrayList<>();

        return announcements.stream()
                .filter(a -> a.getVenue().getId() == manager.getActiveVenue().getId())
                .filter(a -> a.getStartEventDay().equals(startingDate))
                .filter(a -> a.getState() == AnnouncementState.CLOSED)
                .map(a -> (SchedulableEvent) a)
                .toList();
    }

    @Override
    public List<Announcement> findActiveByGenres(List<MusicalGenre> artistGenres, int page, int pageSize) {
        return announcements.stream()
                .filter(a -> a.getState() == AnnouncementState.OPEN)
                .filter(a -> !Collections.disjoint(artistGenres, a.getRequestedGenres()))
                .skip((long) page * pageSize)
                .limit(pageSize)
                .toList();
    }

    @Override
    public List<Announcement> findByManager(String managerUsername) throws DatabaseException {
        Manager manager = (Manager) UserDAOMemory.getUsers().stream()
                .filter(u -> u instanceof Manager && u.getUsername().equals(managerUsername))
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Manager non trovato"));

        // Prendo tutti gli annunci che appartengono alle venue di quel manager
        List<Venue> managerVenues = manager.getVenueList();
        return announcements.stream()
                .filter(a -> managerVenues.contains(a.getVenue()))
                .toList();
    }

    private List<Announcement> generateMockData() {
        List<Announcement> list = new ArrayList<>();


        return list;
    }

    @Override
    public void updateAnnouncementState(Announcement ann) {
        announcements.stream()
                .filter(a -> a.getId() == ann.getId())
                .findFirst()
                .ifPresent(a -> a.setState(ann.getState()));
    }

    @Override
    public List<Announcement> findClosedByIdVenue(int venueId) {
        return announcements.stream()
                .filter(a -> a.getVenue().getId() == venueId && a.getState() == AnnouncementState.CLOSED)
                .toList();
    }

    @Override
    public Announcement findByApplicationId(int id) throws DatabaseException {
        return announcements.stream()
                .filter(ann -> ann.getApplicationList().stream()
                        .anyMatch(obs -> obs.getId() == id))
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Nessun annuncio collegato a questa candidatura"));
    }
}
