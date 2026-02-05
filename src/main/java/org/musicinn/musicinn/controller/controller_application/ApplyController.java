package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.DistanceServiceMock;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderMapper;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.EventBean;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.database.ArtistDAODatabase;
import org.musicinn.musicinn.util.dao.interfaces.*;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;

public class ApplyController {
    public TechnicalRiderBean getEquipmentBeans() throws PersistenceException {
        TechnicalRiderDAO technicalRiderDAO = DAOFactory.getTechnicalRiderDAO();
        TechnicalRider rider = technicalRiderDAO.read(Session.getSingletonInstance().getUser().getUsername(), Session.UserRole.ARTIST);
        return TechnicalRiderMapper.toBean(rider);
    }

    private final AnnouncementDAO announcementDAO = DAOFactory.getAnnouncementDAO();
    private final TechnicalRiderDAO riderDAO = DAOFactory.getTechnicalRiderDAO();
    private final DistanceServiceMock distanceService = new DistanceServiceMock();

    public List<EventBean> getCompatibleEvents(int page) throws PersistenceException {
        // 1. Recupero Artista e il suo Rider dalla Sessione
        String currentUsername = Session.getSingletonInstance().getUser().getUsername();
        Artist currentUser = DAOFactory.getArtistDAO().read(currentUsername);
        // Carichiamo l'Entity completa del rider dell'artista per fare i confronti
        ArtistRider artistRider = (ArtistRider) riderDAO.read(Session.getSingletonInstance().getUser().getUsername(), Session.UserRole.ARTIST);

        // 2. FILTRO SQL (Strategia A): Annunci OPEN + Generi compatibili
        // Il DAO restituisce Announcement che hanno già al loro interno l'oggetto Venue
        if (Session.getSingletonInstance().getPersistenceType().equals(Session.PersistenceType.DATABASE)) {
            ArtistDAODatabase artistDAO = new ArtistDAODatabase();
            currentUser.setGenresList(artistDAO.loadArtistGenres(Session.getSingletonInstance().getUser().getUsername()));
        }
        List<Announcement> announcements = announcementDAO.findOpenAnnouncements(page, 10);

        List<EventBean> results = new ArrayList<>();

        for (Announcement ann : announcements) {
            // 3. NAVIGAZIONE: Otteniamo il locale e il suo rider tecnico
            Venue venue = ann.getVenue();
            ManagerRider venueRider = venue.getRider();

            // 4. ASSEMBLAGGIO EVENTBEAN
            EventBean bean = new EventBean();

            // Dati del Locale
            bean.setVenueName(venue.getName());
            bean.setVenueAddress(venue.getAddress());
            bean.setVenueCity(venue.getCity());
            bean.setTypeVenue(venue.getTypeVenue());

            // Calcolo distanza (Logica di Business)
            int distance = distanceService.calculateDistance(currentUser.getCity(), currentUser.getAddress(), venue.getCity(), venue.getAddress());
            bean.setDistance(distance);

            // Incapsulamento Bean Interni
            bean.setAnnouncementBean(createAnnouncementBean(ann));
            bean.setTechnicalRiderBean(TechnicalRiderMapper.toBean(venueRider));
            ValidationResult vr = venueRider.contains(artistRider);
            bean.setReport(vr);

            results.add(bean);
        }
        return results;
    }

    private AnnouncementBean createAnnouncementBean(Announcement ann) {
        // Trasferisce i dati da Entity Announcement a AnnouncementBean
        AnnouncementBean bean = new AnnouncementBean();
        bean.setId(ann.getId());
        bean.setStartingDate(ann.getStartEventDay());
        bean.setStartingTime(ann.getStartEventTime());
        bean.setDuration(ann.getDuration());
        bean.setCachet(ann.getCachet());
        bean.setDeposit(ann.getDeposit());
        bean.setRequestedGenres(ann.getRequestedGenres());
        bean.setRequestedTypesArtist(ann.getRequestedTypesArtist());
        bean.setDoesUnreleased(ann.getDoesUnreleased());
        bean.setDescription(ann.getDescription());
        return bean;
    }

    public void createApplication(EventBean eventBean) throws PersistenceException {
        Application application = new Application();
        application.setState(ApplicationState.PENDING);
        application.setSoundcheckTime(eventBean.getAnnouncementBean().getSoundcheckTime());
        List<MusicalGenre> requestedGenres = eventBean.getAnnouncementBean().getRequestedGenres();

        ArtistDAO artistDAO = DAOFactory.getArtistDAO();
        List<MusicalGenre> artistGenres = artistDAO.loadArtistGenres(Session.getSingletonInstance().getUser().getUsername());

        application.calculateAndSetScore(artistGenres, requestedGenres);

        Venue venue = new Venue();
        venue.setName(eventBean.getVenueName());
        venue.setAddress(eventBean.getVenueAddress());
        venue.setCity(eventBean.getVenueCity());
        venue.setTypeVenue(eventBean.getTypeVenue());

        Announcement announcement = new Announcement();
        announcement.setId(eventBean.getAnnouncementBean().getId());
        announcement.setStartEventDay(eventBean.getAnnouncementBean().getStartingDate());
        announcement.setStartEventTime(eventBean.getAnnouncementBean().getStartingTime());
        announcement.setVenue(venue);

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        applicationDAO.save(application, announcement);
    }
}
