package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.model.Artist;
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

public class ApplicationDAOMemory implements ApplicationDAO {
    private static final List<Application> applications = new ArrayList<>();
    private static int idCounter = 1;

    static {
        try {
            Announcement ann = DAOFactory.getAnnouncementDAO().findByApplicationId(1);

            initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "mario88");
            initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art1");
            initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art5");
            initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art6");
            initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "art7");

            ann = DAOFactory.getAnnouncementDAO().findByApplicationId(2);

            initApplication(ann, ann.getStartEventDay(), ann.getStartEventTime(), "mario88");
        } catch (PersistenceException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void initApplication(Announcement ann, LocalDate date, LocalTime time, String usernameArtist) {
        Application app = new Application();
        app.setId(++idCounter);
        app.setSoundcheckTime(LocalDateTime.of(date, time.minusMinutes(40)));
        app.setState(ApplicationState.PENDING);
        app.setScore(84.8);
        app.setUsernameArtist(usernameArtist);
        applications.add(app);
        ann.getApplicationList().add(app);
//TODO        ((Artist) DAOFactory.getUserDAO().findByIdentifier(usernameArtist)).getApplications().add(app);
    }

    public static List<Application> getApplications() {
        return applications;
    }

    public void save(Application application, Announcement announcement) throws DatabaseException {
        application.setId(++idCounter);
        application.setUsernameArtist(Session.getSingletonInstance().getUser().getUsername());
        applications.add(application);

        // Cerca l'annuncio e gli consegna la candidatura
        Announcement realAnn = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(a -> a.getId() == announcement.getId())
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Annuncio non trovato."));

        realAnn.addObserver(application);
    }

    @Override
    public Map<Application, String> findByAnnouncementId(int announcementId) throws DatabaseException {
        Announcement ann = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(a -> a.getId() == announcementId)
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Annuncio non trovato."));

        Map<Application, String> results = new LinkedHashMap<>();
        for (Observer obs : ann.getApplicationList()) {
            Application app = (Application) obs;
            results.put(app, app.getUsernameArtist());
        }
        return results;
    }

    // Il seguente metodo non serve perché ci sono gli Observer apposta
    @Override
    public void updateApplicationState(Application app) {
//        applications.stream()
//                .filter(a -> a.getId() == app.getId())
//                .findFirst()
//                .ifPresent(a -> a.setState(app.getState()));
    }

    @Override
    public Application findAcceptedByAnnouncement(int announcementId) throws DatabaseException {
        Announcement ann = AnnouncementDAOMemory.getAnnouncements().stream()
                .filter(a -> a.getId() == announcementId)
                .findFirst()
                .orElseThrow(() -> new DatabaseException("Annuncio non trovato."));

        return (Application) ann.getApplicationList().stream()
                .filter(obs -> ((Application) obs).getState() == ApplicationState.ACCEPTED)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Application> findAcceptedByArtist(String artistUsername) throws DatabaseException {
        return applications.stream()
                .filter(app -> app.getUsernameArtist().equals(artistUsername)
                        && app.getState() == ApplicationState.ACCEPTED)
                .toList();
    }
}
