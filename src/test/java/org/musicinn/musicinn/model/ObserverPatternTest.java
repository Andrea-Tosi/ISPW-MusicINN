package org.musicinn.musicinn.model;

import org.junit.jupiter.api.Test;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.ApplicationState;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica l'implementazione del pattern Observer tra le classi Announcement (Soggetto) e Application (Osservatore).
 * Il test assicura che il cambiamento di stato di un annuncio si propaghi correttamente a tutte le candidature collegate, mantenendo l'integrità dei dati.
 * @author Andrea Tosi
 */
class ObserverPatternTest {
    /**
     * Il test simula lo scenario in cui un Manager accetta una candidatura specifica.
     * Viene verificato che l'invio della notifica di chiusura dell'annuncio (CLOSED) scateni l'aggiornamento automatico
     * di tutti i candidati ancora in attesa dallo stato PENDING passano allo stato REJECTED.
     */
    @Test
    void testAnnouncementClosureRejectsApplications() {
        Announcement announcement = new Announcement();
        announcement.setState(AnnouncementState.OPEN);

        Application app1 = new Application();
        app1.setState(ApplicationState.PENDING);

        Application app2 = new Application();
        app2.setState(ApplicationState.PENDING);

        announcement.addObserver(app1);
        announcement.addObserver(app2);

        // Il manager accetta un'altra persona e l'annuncio si chiude
        announcement.setState(AnnouncementState.CLOSED);

        // Verifica: Gli osservatori devono essersi aggiornati
        assertEquals(ApplicationState.REJECTED, app1.getState(), "L'applicazione 1 doveva essere rifiutata");
        assertEquals(ApplicationState.REJECTED, app2.getState(), "L'applicazione 2 doveva essere rifiutata");
    }

    /**
     * Verifica il comportamento del sistema durante la chiusura di un annuncio. Testa la logica condizionale dell'aggiornamento:
     * quando un annuncio passa a CLOSED, la candidatura scelta deve mantenere lo stato ACCEPTED,
     * mentre tutte le altre candidature ancora in PENDING devono essere automaticamente convertite in REJECTED.
     */
    @Test
    void testAnnouncementClosureAndApplicationSelection() {
        Announcement announcement = new Announcement();
        announcement.setState(AnnouncementState.OPEN);

        Application winnerApp = new Application();
        winnerApp.setState(ApplicationState.PENDING);

        Application loserApp = new Application();
        loserApp.setState(ApplicationState.PENDING);

        announcement.addObserver(winnerApp);
        announcement.addObserver(loserApp);

        // Simulazione della scelta del Manager
        winnerApp.setState(ApplicationState.ACCEPTED);

        // La chiusura dell'annuncio scatena il notifyObservers()
        announcement.setState(AnnouncementState.CLOSED);

        assertEquals(ApplicationState.ACCEPTED, winnerApp.getState(), "L'applicazione accettata non deve essere sovrascritta dalla notifica di chiusura");
        assertEquals(ApplicationState.REJECTED, loserApp.getState(), "Le altre applicazioni pendenti devono essere rifiutate automaticamente");
    }

    /**
     * Verifica la corretta gestione della lista degli osservatori,
     * assicurandosi che una candidatura rimossa dal monitoraggio dell'annuncio non riceva più notifiche di cambio stato.
     */
    @Test
    void testRemovalOfObserver() {
        // Verifica che se un osservatore viene rimosso, non riceve più aggiornamenti
        Announcement announcement = new Announcement();
        Application app = new Application();
        app.setState(ApplicationState.PENDING);

        announcement.addObserver(app);
        announcement.removeObserver(app);

        announcement.setState(AnnouncementState.CLOSED);

        assertEquals(ApplicationState.PENDING, app.getState(), "L'applicazione rimossa non dovrebbe aver ricevuto la notifica");
    }
}
