package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.Application;
import org.musicinn.musicinn.model.observer_pattern.Observer;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.ApplicationDAO;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApplicationDAOMemory implements ApplicationDAO {
    private static final List<Application> applications = new ArrayList<>();
    private static int idCounter = 1;

    static {
        da creare
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
