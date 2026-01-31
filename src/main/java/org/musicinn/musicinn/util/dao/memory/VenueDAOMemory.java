package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.List;

public class VenueDAOMemory implements VenueDAO {
    protected static final List<Venue> venues = new ArrayList<>();
    private static int idCounter = 1;

    static {
        da creare
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
