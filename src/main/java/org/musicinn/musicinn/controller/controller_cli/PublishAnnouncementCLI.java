package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.PublishAnnouncementController;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.VenueBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.exceptions.CalendarException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublishAnnouncementCLI {
    private static final Logger LOGGER = Logger.getLogger(PublishAnnouncementCLI.class.getName());
    private final Scanner scanner;
    private final PublishAnnouncementController controller = new PublishAnnouncementController();

    public PublishAnnouncementCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        LOGGER.info("\n*** PUBBLICA ANNUNCIO ***");

        try {
            // 1. Mostra dati del locale (come nella GUI)
            VenueBean venue = new VenueBean();
            controller.getVenueData(venue);
            displayVenueSummary(venue);

            // 2. Raccolta dati Annuncio
            AnnouncementBean bean = new AnnouncementBean();

            bean.setStartingDate(askDate());
            bean.setStartingTime(askTime());
            bean.setDuration(askDuration());

            LOGGER.info("Cachet offerto (€): ");
            bean.setCachet(Double.parseDouble(scanner.nextLine()));

            LOGGER.info("Cauzione richiesta (€): ");
            bean.setDeposit(Double.parseDouble(scanner.nextLine()));

            // 3. Requisiti Artista (Multi-selezione guidata)
            bean.setRequestedGenres(selectGenres());
            bean.setRequestedTypesArtist(selectArtistTypes());

            LOGGER.info("L'artista deve eseguire inediti? (1=Sì, 2=No, 3=Indifferente): ");
            String unreleased = scanner.nextLine();
            Boolean doesUnreleased;
            if (unreleased.equals("1")) doesUnreleased = true;
            else doesUnreleased = unreleased.equals("2") ? false : null;
            bean.setDoesUnreleased(doesUnreleased);

            LOGGER.info("Descrizione evento: ");
            bean.setDescription(scanner.nextLine());

            // 4. Invocazione Controller
            controller.publish(bean);
            LOGGER.info("Annuncio pubblicato con successo!");

        } catch (PersistenceException | CalendarException e) {
            LOGGER.log(Level.INFO, "Impossibile pubblicare: {0}", e.getMessage());
        } catch (NumberFormatException _) {
            LOGGER.info("Errore: Inserisci un valore numerico valido per cachet/durata.");
        } finally {
            // Torna alla Home Gestore
            Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGER_HOME);
        }
    }

    private void displayVenueSummary(VenueBean venue) {
        LOGGER.log(Level.INFO, "Locale: {0}", venue.getName());
        LOGGER.log(Level.INFO, "Indirizzo: {0}, {1}", new Object[] {venue.getAddress(), venue.getCity()});
        LOGGER.log(Level.INFO, "Rider Tecnico attuale:\n{0}", TechnicalRiderFormatter.format(venue.getRider(), Session.UserRole.MANAGER));
        LOGGER.info("------------------------------------------------");
    }

    private LocalDate askDate() {
        while (true) {
            LOGGER.info("Data evento (AAAA-MM-GG): ");
            try {
                LocalDate date = LocalDate.parse(scanner.nextLine());
                if (date.isBefore(LocalDate.now())) {
                    LOGGER.info("La data non può essere nel passato.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException _) {
                LOGGER.info("Formato data non valido.");
            }
        }
    }

    private LocalTime askTime() {
        while (true) {
            LOGGER.info("Ora inizio (HH:MM): ");
            try {
                return LocalTime.parse(scanner.nextLine());
            } catch (DateTimeParseException _) {
                LOGGER.info("Formato ora non valido.");
            }
        }
    }

    private Duration askDuration() {
        LOGGER.info("Durata stimata (in minuti): ");
        long minutes = Integer.parseInt(scanner.nextLine());
        return Duration.ofMinutes(minutes);
    }

    private List<MusicalGenre> selectGenres() {
        LOGGER.info("Seleziona i generi (es: '1,3,5' o 'all' per tutti):");
        MusicalGenre[] genres = MusicalGenre.values();
        for (int i = 0; i < genres.length; i++) {
            LOGGER.log(Level.INFO, "{0}. {1}", new Object[] {(i + 1), genres[i]});
        }
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("all")) return List.of(genres);

        List<MusicalGenre> selected = new ArrayList<>();
        for (String s : input.split(",")) {
            selected.add(genres[Integer.parseInt(s.trim()) - 1]);
        }
        return selected;
    }

    private List<TypeArtist> selectArtistTypes() {
        LOGGER.info("Seleziona tipi di artista ammessi (es: '1,2'):");
        TypeArtist[] types = TypeArtist.values();
        for (int i = 0; i < types.length; i++) {
            LOGGER.log(Level.INFO, "{0}. {1}", new Object[] {(i + 1), types[i]});
        }
        String input = scanner.nextLine();
        List<TypeArtist> selected = new ArrayList<>();
        for (String s : input.split(",")) {
            selected.add(types[Integer.parseInt(s.trim()) - 1]);
        }
        return selected;
    }
}
