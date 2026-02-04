package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.ApplyController;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.EventBean;
import org.musicinn.musicinn.util.bean.technical_rider_bean.TechnicalRiderBean;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplyCLI {
    private static final Logger LOGGER = Logger.getLogger(ApplyCLI.class.getName());
    private final Scanner scanner;
    private final ApplyController controller = new ApplyController();

    private int currentPage = 0;

    public ApplyCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        try {
            // 1. REVISIONE RIDER TECNICO (come 4.1.0_ApplyView-TechnicalRiderRevision)
            if (!handleRiderRevision()) {
                Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGE_RIDER);
                return;
            }

            // 2. LOOP DI SELEZIONE EVENTO E PAGINAZIONE
            boolean inSelection = true;
            while (inSelection) {
                List<EventBean> events = controller.getCompatibleEvents(currentPage);

                if (events.isEmpty() && currentPage > 0) {
                    LOGGER.info("\nNon ci sono altri annunci in questa direzione.");
                    currentPage--; // Torna indietro se la pagina è vuota
                    events = controller.getCompatibleEvents(currentPage);
                } else if (events.isEmpty()) {
                    LOGGER.info("\nNessun annuncio compatibile trovato al momento.");
                    inSelection = false;
                    continue;
                }

                displayEventList(events);

                LOGGER.info("\nComandi: [Indice] Dettagli | [n] Pag. Successiva | [p] Pag. Precedente | [b] Esci");
                LOGGER.info("Scelta: ");
                String input = scanner.nextLine().toLowerCase();

                switch (input) {
                    case "b" -> inSelection = false;
                    case "n" -> currentPage++;
                    case "p" -> {
                        if (currentPage > 0) currentPage--;
                        else LOGGER.info("Sei già sulla prima pagina.");
                    }
                    default -> inSelection = !handleIndexSelection(input, events);
                }
            }
        } catch (PersistenceException e) {
            LOGGER.log(Level.WARNING, "Errore caricamento dati: {0}", e.getMessage());
        } finally {
            backToHome();
        }
    }

    private boolean handleIndexSelection(String input, List<EventBean> events) throws PersistenceException {
        try {
            int index = Integer.parseInt(input);

            if (index >= 0 && index < events.size()) {
                // Se la candidatura va a buon fine, restituiamo true per chiudere il loop
                return handleEventSelection(events.get(index));
            }

            LOGGER.info("Indice non valido.");
        } catch (NumberFormatException _) {
            LOGGER.info("Comando non riconosciuto.");
        }
        return false;
    }

    private boolean handleRiderRevision() throws PersistenceException {
        LOGGER.info("\n*** REVISIONE RIDER TECNICO ***");
        TechnicalRiderBean myRider = controller.getEquipmentBeans();

        LOGGER.info("Ecco i tuoi dati tecnici attuali:");
        LOGGER.log(Level.INFO, "{0}", TechnicalRiderFormatter.format(myRider, Session.UserRole.ARTIST));
        LOGGER.log(Level.INFO, "Dimensioni minime palco: {0}m x {1}m", new Object[] {myRider.getMinLengthStage(), myRider.getMinWidthStage()});

        LOGGER.info("\nI dati sono corretti? (1. Sì / 2. No, modifica in Gestione Rider): ");
        String choice = scanner.nextLine();
        return choice.equals("1");
    }

    private void displayEventList(List<EventBean> events) {
        LOGGER.log(Level.INFO, "\n*** ANNUNCI COMPATIBILI (Pagina {0}) ***", currentPage + 1);

        String header = String.format("%-3s | %-20s | %-15s | %-10s | %-10s | %-5s",
                                      "ID", "Locale", "Città", "Data", "Distanza", "Rider");
        LOGGER.info(header);
        LOGGER.info("--------------------------------------------------------------------------------");

        for (int i = 0; i < events.size(); i++) {
            EventBean eb = events.get(i);
            String compatibility = eb.getReport().isValid() ? "OK" : "NO";

            String row = String.format("[%d] | %-20.20s | %-15.15s | %-10s | %-7d km | %-5s",
                    i,
                    eb.getVenueName(),
                    eb.getVenueCity(),
                    eb.getAnnouncementBean().getStartingDate(),
                    eb.getDistance(),
                    compatibility);
            LOGGER.info(row);
        }
    }

    /**
     * Gestisce i dettagli dell'evento.
     * @return true se l'utente ha inviato la candidatura, false se è tornato indietro.
     */
    private boolean handleEventSelection(EventBean event) throws PersistenceException {
        LOGGER.info("\n--- DETTAGLI EVENTO SELEZIONATO ---");
        LOGGER.log(Level.INFO, "Locale:    {0} ({1})", new Object[] {event.getVenueName(), event.getTypeVenue()});
        LOGGER.log(Level.INFO, "Indirizzo: {0}, {1}", new Object[] {event.getVenueAddress(), event.getVenueCity()});
        LOGGER.log(Level.INFO, "Data/Ora:  {0} alle {1}", new Object[] {event.getAnnouncementBean().getStartingDate(), event.getAnnouncementBean().getStartingTime()});
        LOGGER.log(Level.INFO, "Compenso:  Cachet {0}€ | Cauzione {1}€", new Object[] {event.getAnnouncementBean().getCachet(), event.getAnnouncementBean().getDeposit()});
        LOGGER.log(Level.INFO, "Descrizione: {0}", event.getAnnouncementBean().getDescription());

        // Visualizzazione report compatibilità (ValidationResult)
        if (!event.getReport().isValid()) {
            LOGGER.info("\nATTENZIONE: Rider non compatibile:");
            LOGGER.log(Level.INFO, "{0}", event.getReport().toString());
            LOGGER.info("\nPremi INVIO per tornare alla lista...");
            scanner.nextLine();
            return false;
        }

        LOGGER.info("\nVuoi inviare la tua candidatura? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            // Richiesta Soundcheck (come 4.4.0_ApplyView_ConfirmApplication)
            LOGGER.info("Quanti minuti PRIMA dell'evento vuoi arrivare per il soundcheck? (es. 30): ");
            try {
                int minutesBefore = Integer.parseInt(scanner.nextLine());

                // Logica di calcolo orario (stessa del controller GUI)
                LocalDateTime soundcheck = LocalDateTime.of(
                        event.getAnnouncementBean().getStartingDate(),
                        event.getAnnouncementBean().getStartingTime()
                ).minusMinutes(minutesBefore);

                event.getAnnouncementBean().setSoundcheckTime(soundcheck);

                controller.createApplication(event);
                LOGGER.info("Candidatura inviata con successo!");
                return true; // Restituisce true per chiudere il ciclo in run()
            } catch (NumberFormatException _) {
                LOGGER.info("Input non valido. Candidatura annullata.");
                return false;
            }
        }

        return false; // L'utente ha scelto 'n', torna alla tabella
    }

    private void backToHome() {
        Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.ARTIST_HOME);
    }
}
