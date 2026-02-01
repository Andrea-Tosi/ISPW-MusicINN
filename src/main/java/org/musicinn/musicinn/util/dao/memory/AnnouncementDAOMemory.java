package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.AnnouncementDAO;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnnouncementDAOMemory implements AnnouncementDAO {
    private static final List<Announcement> announcements = new ArrayList<>();
    private static int idCounter = 0;

    static {
        try {
            Venue venue = DAOFactory.getVenueDAO().read("the_rock_club");

            Announcement ann = new Announcement();
            ann.setId(++idCounter);
            ann.setState(AnnouncementState.OPEN);
            ann.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.METAL));
            ann.setStartEventDay(LocalDate.of(2026, Month.MARCH, 20));
            ann.setStartEventTime(LocalTime.of(21, 30));
            ann.setDuration(Duration.ofMinutes(120));
            ann.setCachet(300.0);
            ann.setDeposit(50.0);
            ann.setDescription("Cerchiamo band Rock per serata energica!");
            ann.setVenue(venue);
            ann.setRequestedTypesArtist(List.of(TypeArtist.BAND));
            announcements.add(ann);

            ann = new Announcement();
            ann.setId(++idCounter);
            ann.setState(AnnouncementState.CLOSED);
            ann.setRequestedGenres(List.of(MusicalGenre.ROCK));
            ann.setStartEventDay(LocalDate.of(2026, Month.MAY, 1));
            ann.setStartEventTime(LocalTime.of(21, 0));
            ann.setDuration(Duration.ofMinutes(120));
            ann.setCachet(200.0);
            ann.setDeposit(100.0);
            ann.setDescription("Rock Night");
            ann.setVenue(venue);
            ann.setRequestedTypesArtist(List.of(TypeArtist.BAND));
            announcements.add(ann);

            ann = new Announcement();
            ann.setId(++idCounter);
            ann.setState(AnnouncementState.OPEN);
            ann.setRequestedGenres(List.of(MusicalGenre.ROCK));
            ann.setStartEventDay(LocalDate.of(2026, Month.MAY, 15));
            ann.setStartEventTime(LocalTime.of(22, 0));
            ann.setDuration(Duration.ofMinutes(180));
            ann.setCachet(300.0);
            ann.setDeposit(120.0);
            ann.setDescription("Big Event");
            ann.setVenue(venue);
            ann.setRequestedTypesArtist(Arrays.asList(TypeArtist.values()));
            announcements.add(ann);

            ann = new Announcement();
            ann.setId(++idCounter);
            ann.setState(AnnouncementState.OPEN);
            ann.setRequestedGenres(Arrays.asList(MusicalGenre.POP, MusicalGenre.JAZZ));
            ann.setStartEventDay(LocalDate.of(2026, Month.FEBRUARY, 25));
            ann.setStartEventTime(LocalTime.of(21, 0));
            ann.setDuration(Duration.ofMinutes(120));
            ann.setCachet(111.0);
            ann.setDeposit(222.0);
            ann.setDescription("Evento Super!!!");
            ann.setVenue(venue);
            ann.setDoesUnreleased(false);
            ann.setRequestedTypesArtist(Arrays.asList(TypeArtist.values()));
            announcements.add(ann);

            for (int i = 0; i < 30; i++) {
                ann = new Announcement();
                ann.setId(++idCounter);
                ann.setState(AnnouncementState.OPEN);
                ann.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
                ann.setStartEventDay(LocalDate.now().plusDays(200 + i));
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
        } catch (PersistenceException e) {
            System.err.println(e.getMessage());
        }
    }

    public static List<Announcement> getAnnouncements() {
        return announcements;
    }

    @Override
    public void save(Announcement announcement) {
        announcement.setId(++idCounter);
        announcement.setVenue(((Manager) Session.getSingletonInstance().getUser()).getActiveVenue());
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
        try {
            Manager manager = (Manager) DAOFactory.getUserDAO().findByIdentifier(Session.getSingletonInstance().getUser().getUsername());

            if (manager == null || manager.getActiveVenue() == null) return new ArrayList<>();

            return announcements.stream()
                    .filter(a -> a.getVenue().getId() == manager.getActiveVenue().getId())
                    .filter(a -> a.getStartEventDay().equals(startingDate))
                    .filter(a -> a.getState() == AnnouncementState.CLOSED)
                    .map(a -> (SchedulableEvent) a)
                    .toList();
        } catch (PersistenceException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Announcement> findOpenAnnouncements(int page, int pageSize) {
        return announcements.stream()
                .filter(a -> a.getState() == AnnouncementState.OPEN)
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
                .filter(a -> a.getState().equals(AnnouncementState.OPEN) && managerVenues.contains(a.getVenue()))
                .toList();
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
