package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Calendar;
import org.musicinn.musicinn.model.SchedulableEvent;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.VenueBean;
import org.musicinn.musicinn.util.bean.technical_rider_bean.TechnicalRiderBean;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.AnnouncementDAO;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.bean.AnnouncementBean;

import org.musicinn.musicinn.util.exceptions.CalendarException;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.List;

public class PublishAnnouncementController {
    public void publish(AnnouncementBean ab) throws DatabaseException, CalendarException {
        // Controlla Disponibilità Temporale
        AnnouncementDAO announcementDAO = DAOFactory.getAnnouncementDAO();

        // Recuperiamo dal database solo gli eventi del locale per quel giorno specifico
        // per popolare il Calendar e verificare sovrapposizioni
        List<SchedulableEvent> eventsOnDate = announcementDAO.getEventsByDate(ab.getStartingDate());

        Calendar calendar = new Calendar();
        calendar.setEvents(eventsOnDate);

        // Invochiamo il metodo isAvailable della classe Calendar
        if (!calendar.isAvailable(ab.getStartingDate(), ab.getStartingTime(), ab.getDuration())) {
            throw new CalendarException();
        }

        // 3. Mapping dal Bean all'Entity di Dominio
        Announcement announcement = getAnnouncement(ab);

        // 4. Persistenza
        announcementDAO.save(announcement);

        // 5. Post-operazioni (Opzionale)
        // Qui potresti triggerare l'invio di notifiche o log di sistema
        System.out.println("Annuncio pubblicato correttamente per la data: " + ab.getStartingDate());
    }

    private static Announcement getAnnouncement(AnnouncementBean ab) {
        Announcement announcement = new Announcement();
        announcement.setStartEventDay(ab.getStartingDate());
        announcement.setStartEventTime(ab.getStartingTime());
        announcement.setDuration(ab.getDuration());
        announcement.setCachet(ab.getCachet());
        announcement.setCachet(ab.getCachet());
        announcement.setDeposit(ab.getDeposit());
        announcement.setRequestedGenres(ab.getRequestedGenres());
        announcement.setRequestedTypesArtist(ab.getRequestedTypesArtist());
        announcement.setDoesUnreleased(ab.getDoesUnreleased());
        announcement.setDescription(ab.getDescription().isBlank() ? "." : ab.getDescription());
        announcement.setState(AnnouncementState.OPEN);
        return announcement;
    }

    public void getVenueData(VenueBean bean) throws DatabaseException {
        VenueDAO venueDAO = DAOFactory.getVenueDAO();
        Venue venue = venueDAO.read(Session.getSingletonInstance().getUsername());

        bean.setName(venue.getName());
        bean.setCity(venue.getCity());
        bean.setAddress(venue.getAddress());
        bean.setTypeVenue(venue.getTypeVenue());

        ManagementTechnicalRiderController controller = new ManagementTechnicalRiderController();
        TechnicalRiderBean trBean = new TechnicalRiderBean();
        controller.loadRiderData(trBean);

        bean.setRider(trBean);
    }
}