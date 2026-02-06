package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.AcceptApplicationController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.ApplicationBean;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcceptApplicationCLI {
    private static final Logger LOGGER = Logger.getLogger(AcceptApplicationCLI.class.getName());
    private final Scanner scanner;
    private final AcceptApplicationController controller = new AcceptApplicationController();

    public AcceptApplicationCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        boolean exitView = false;

        while (!exitView) {
            try {
                LOGGER.info("\n*** I TUOI ANNUNCI PUBBLICATI ***");
                List<AnnouncementBean> announcements = controller.getAllManagerAnnouncements();

                if (announcements.isEmpty()) {
                    LOGGER.info("Non hai ancora pubblicato nessun annuncio aperto.");
                    exitView = true;
                    continue;
                }

                displayAnnouncements(announcements);

                LOGGER.info("\nInserisci l'ID dell'annuncio per vedere le candidature (o 'b' per tornare indietro): ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("b")) {
                    exitView = true;
                } else {
                    int annIndex = Integer.parseInt(input);
                    AnnouncementBean selectedAnn = announcements.get(annIndex);

                    // handleApplicationsList restituisce true se un artista è stato accettato
                    // in quel caso usciamo completamente e torniamo in home
                    if (handleApplicationsList(selectedAnn)) {
                        exitView = true;
                    }
                    // Se handleApplicationsList restituisce false (perché l'utente ha premuto 'b'),
                    // il ciclo while ricomincia e mostra di nuovo la lista annunci.
                }

            } catch (PersistenceException e) {
                LOGGER.log(Level.WARNING, "Errore nel recupero dati: {0}", e.getMessage());
                exitView = true;
            } catch (NumberFormatException | IndexOutOfBoundsException _) {
                LOGGER.info("Selezione non valida. Riprova.");
            }
        }
        backToHome();
    }

    private void displayAnnouncements (List < AnnouncementBean > list) {
        String header = String.format("%-3s | %-12s | %-8s | %-10s | %-12s", "ID", "Data", "Ora", "Cachet", "Candidature");
        LOGGER.info(header);
        LOGGER.info("------------------------------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            AnnouncementBean b = list.get(i);
            String row = String.format("[%d] | %-12s | %-8s | %-10.2f€ | %-12d",
                    i, b.getStartingDate(), b.getStartingTime(), b.getCachet(), b.getNumOfApplications());
            LOGGER.info(row);
        }
    }

    /**
     * @return true se un'applicazione è stata accettata (vai in home),
     *         false se l'utente è tornato indietro alla lista annunci.
     */
    private boolean handleApplicationsList (AnnouncementBean annBean) throws PersistenceException {
        while (true) {
            List<ApplicationBean> apps = controller.getApplicationsForAnnouncement(annBean);

            if (apps.isEmpty()) {
                LOGGER.info("\nNessun artista si è ancora candidato per questo evento.");
                LOGGER.info("Premi INVIO per tornare indietro...");
                scanner.nextLine();
                return false;
            }

            LOGGER.log(Level.INFO, "\n*** CANDIDATURE PER L''EVENTO DEL {0} ***", annBean.getStartingDate());
            displayApplications(apps);

            LOGGER.info("\nComandi: [Indice] Dettagli Artista | [b] Torna alla lista Annunci");
            LOGGER.info("Scelta: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("b")) {
                return false; // Torna al ciclo degli annunci
            }

            try {
                int appIndex = Integer.parseInt(input);
                if (handleApplicationDecision(annBean, apps.get(appIndex))) {
                    return true; // Un artista è stato accettato, chiudi tutto e torna in home
                }
            } catch (NumberFormatException | IndexOutOfBoundsException _) {
                LOGGER.info("Indice non valido.");
            }
        }
    }

    private void displayApplications (List < ApplicationBean > apps) {
        String header = String.format("%-3s | %-20s | %-10s | %-15s", "ID", "Artista", "Match %", "Soundcheck");
        LOGGER.info(header);
        LOGGER.info("------------------------------------------------------------");
        for (int i = 0; i < apps.size(); i++) {
            ApplicationBean b = apps.get(i);
            String row = String.format("[%d] | %-20.20s | %-10.1f%% | %-15s",
                    i, b.getArtistStageName(), b.getTotalScore(), b.getRequestedSoundcheck().toLocalTime());
            LOGGER.info(row);
        }
    }

    private boolean handleApplicationDecision (AnnouncementBean ann, ApplicationBean app) throws
    PersistenceException {
        LOGGER.info("\n--- DETTAGLI CANDIDATURA ---");
        LOGGER.log(Level.INFO, "Artista: {0}", app.getArtistStageName());
        LOGGER.log(Level.INFO, "Generi:  {0}", app.getArtistGenres());
        LOGGER.log(Level.INFO, "Score:   {0}%", Math.round(app.getTotalScore()));
        LOGGER.log(Level.INFO, "Arrivo soundcheck richiesto: {0}", app.getRequestedSoundcheck());
        LOGGER.info("\nRider Tecnico dell'artista:");
        LOGGER.log(Level.INFO, "{0}", TechnicalRiderFormatter.format(app.getRiderBean(), Session.UserRole.ARTIST));

        LOGGER.info("\nVuoi accettare ufficialmente questa candidatura? (s/n)");
        LOGGER.info("(Nota: accettando, l'annuncio verrà chiuso e dovrai versare il cachet)");

        if (scanner.nextLine().equalsIgnoreCase("s")) {
            // Logica applicativa (Stessa di AcceptApplicationConfirmAcceptanceControllerGUI)
            controller.chooseApplication(ann, app);

            PaymentController paymentController = PaymentServiceFactory.getPaymentController();
            int days = paymentController.createPayment(app);

            LOGGER.log(Level.INFO, "\nCONFERMATO! Hai accettato {0}", app.getArtistStageName());
            LOGGER.log(Level.INFO, "Hai {0} giorni di tempo per versare il cachet ({1}€) tramite Stripe.", new Object[]{days, ann.getCachet()});
            return true;
        }
        return false; // Torna alla lista dei candidati
    }

    private void backToHome () {
        Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGER_HOME);
    }
}