package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.model.observer_pattern.Observer;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.ApplicationDAO;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ApplicationDAOMemory implements ApplicationDAO {
    private static final List<Application> applications = new ArrayList<>();
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    private static final String ANNOUNCEMENT_NOT_FOUND = "Annuncio non trovato.";
    private static final Logger LOGGER = Logger.getLogger(ApplicationDAOMemory.class.getName());
    private static boolean isLoaded = false;

    private static synchronized void ensureDataLoaded() {
        if (isLoaded) return;

        Announcement ann = AnnouncementDAOMemory.getAnnouncements().getFirst();
        initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "mario88", ApplicationState.PENDING);
        initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art1", ApplicationState.PENDING);
        initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art5", ApplicationState.PENDING);
        initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art6", ApplicationState.PENDING);
        initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art7", ApplicationState.PENDING);
        ann.setNumOfApplications(5);

        ann = AnnouncementDAOMemory.getAnnouncements().get(1);
        initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "mario88", ApplicationState.ACCEPTED);
        ann.setNumOfApplications(1);

        isLoaded = true;
    }

    private static void initApplication(Announcement ann, LocalDate date, LocalTime time, String usernameArtist, ApplicationState state) {
        try {
            Application app = new Application();
            app.setId(ID_COUNTER.incrementAndGet());
            app.setSoundcheckTime(LocalDateTime.of(date, time.minusMinutes(40)));
            app.setState(state);
            app.setScore(84.8);
            app.setUsernameArtist(usernameArtist);
            applications.add(app);
            ann.getApplicationList().add(app);
            DAOFactory.getArtistDAO().read(usernameArtist).getApplications().add(app);
        } catch (PersistenceException e) {
            LOGGER.fine(e.getMessage());
        }
    }

    public static List<Application> getApplications() {
        return applications;
    }

    public void save(Application application, Announcement announcement) throws DatabaseException {
        ensureDataLoaded();
        application.setId(ID_COUNTER.incrementAndGet());
        application.setUsernameArtist(Session.getSingletonInstance().getUser().getUsername());
        applications.add(application);

        // Cerca l'annuncio e gli consegna la candidatura
        Announcement ann = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(a -> a.getId() == announcement.getId())
                .findFirst()
                .orElseThrow(() -> new DatabaseException(ANNOUNCEMENT_NOT_FOUND));

        ann.addObserver(application);
        ann.setNumOfApplications(ann.getNumOfApplications() + 1);
    }

    @Override
    public Map<Application, String> findByAnnouncementId(int announcementId) throws DatabaseException {
        ensureDataLoaded();
        Announcement ann = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(a -> a.getId() == announcementId)
                .findFirst()
                .orElseThrow(() -> new DatabaseException(ANNOUNCEMENT_NOT_FOUND));

        Map<Application, String> results = new LinkedHashMap<>();
        for (Observer obs : ann.getApplicationList()) {
            Application app = (Application) obs;
            results.put(app, app.getUsernameArtist());
        }
        return results;
    }

    @Override
    public void updateApplicationState(Application app) {
        // Il presente metodo non serve perché ci sono gli Observer apposta
    }

    @Override
    public Application findAcceptedByAnnouncement(int announcementId) throws DatabaseException {
        ensureDataLoaded();
        Announcement ann = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(a -> a.getId() == announcementId)
                .findFirst()
                .orElseThrow(() -> new DatabaseException(ANNOUNCEMENT_NOT_FOUND));

        return (Application) ann.getApplicationList().stream()
                .filter(obs -> ((Application) obs).getState() == ApplicationState.ACCEPTED)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Application> findAcceptedByArtist(String artistUsername) {
        ensureDataLoaded();
        return applications.stream()
                .filter(app -> app.getUsernameArtist().equals(artistUsername)
                        && app.getState() == ApplicationState.ACCEPTED)
                .toList();
    }
}
