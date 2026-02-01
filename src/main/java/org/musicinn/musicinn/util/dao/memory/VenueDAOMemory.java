package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.enumerations.TypeVenue.*;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;

public class VenueDAOMemory implements VenueDAO {
    protected static final List<Venue> venues = new ArrayList<>();
    private static int idCounter = 1;

    static {
        initVenue("The Rock Club", "Roma", "Via Ostiense 50", TypeVenue.CLUB, "the_rock_club");
        initVenue("Blue Bar", "Milano", "Via Brera 12", TypeVenue.BAR, "blue_bar_mgr");
        initVenue("Rock Cave", "Roma", "Via A", TypeVenue.CLUB, "mgr1");
        initVenue("Jazz House", "Milano", "Via B", TypeVenue.BAR, "mgr2");
        initVenue("Club 99", "Napoli", "Via C", TypeVenue.CLUB, "mgr3");
        initVenue("Bar Central", "Torino", "Via D", TypeVenue.BAR, "mgr4");
        initVenue("Arena Blu", "Roma", "Via E", TypeVenue.PUB, "mgr5");
        initVenue("Underground", "Bologna", "Via F", TypeVenue.CLUB, "mgr6");
        initVenue("Teatro Verdi", "Firenze", "Via G", TypeVenue.PUB, "mgr7");
        initVenue("Pub 12", "Palermo", "Via H", TypeVenue.BAR, "mgr8");
        initVenue("Disco Lux", "Milano", "Via I", TypeVenue.CLUB, "mgr9");
        initVenue("Roof Garden", "Napoli", "Via L", TypeVenue.PUB, "mgr10");
    }

    private static void initVenue(String name, String city, String addr, TypeVenue type, String mgrUsername) {
        try {
            Venue venue = new Venue(name, city, addr, type);
            venue.setId(++idCounter);
            venues.add(venue);

            // Colleghiamo al manager (Piano di sopra)
            Manager manager = (Manager) DAOFactory.getUserDAO().findByIdentifier(mgrUsername);

            if (manager != null) {
                manager.getVenueList().add(venue);
                manager.setActiveVenue(venue);
            }
        } catch (PersistenceException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void create(Venue venue, Manager manager) {
        venue.setId(++idCounter);
        venue.setActive(true);
        venues.add(venue);
    }

    @Override
    public int getActiveVenueIdByManager(String managerUsername) throws DatabaseException {
        Manager manager = (Manager) UserDAOMemory.getUsers().stream()
                .filter(u -> u.getUsername().equals(managerUsername))
                .findFirst().orElse(null);

        if (manager == null) throw new DatabaseException("Errore nella ricerca in cache");
        Venue venue = manager.getActiveVenue();
        if (venue == null) throw new DatabaseException("Errore nella ricerca in cache");
        return venue.getId();
    }

    @Override
    public Venue read(String usernameManager) throws DatabaseException {
        Manager manager = (Manager) UserDAOMemory.getUsers().stream()
                .filter(u -> u.getUsername().equals(usernameManager))
                .findFirst().orElse(null);

        if (manager == null) throw new DatabaseException("Errore nella ricerca in cache");
        Venue venue = manager.getActiveVenue();
        if (venue == null) throw new DatabaseException("Errore nella ricerca in cache");
        return venue;
    }

    @Override
    public Venue findByApplicationId(int applicationId) throws DatabaseException {
        Announcement announcement = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(ann -> ann.getApplicationList().stream()
                        .anyMatch(obs -> obs.getId() == applicationId))
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Nessun annuncio trovato per questa candidatura."));

        return announcement.getVenue();
    }

    @Override
    public String findVenueNameByAnnouncementId(int announcementId) throws DatabaseException {
        Venue venue = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(ann -> ann.getId() == announcementId) // Nota: qui usi l'id dell'annuncio
                .map(Announcement::getVenue)
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Locale non trovato per l'annuncio indicato."));

        return venue.getName();
    }
}
