package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.exceptions.PaymentServiceException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagePaymentsCLI {
    private static final Logger LOGGER = Logger.getLogger(ManagePaymentsCLI.class.getName());
    private final Scanner scanner;
    private final PaymentController controller = PaymentServiceFactory.getPaymentController();

    public ManagePaymentsCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        boolean exitView = false;

        while (!exitView) {
            try {
                LOGGER.info("\n*** GESTIONE PAGAMENTI E ACCORDI ***");
                List<PaymentBean> payments = controller.getPayments();

                if (payments.isEmpty()) {
                    LOGGER.info("Non ci sono pagamenti pendenti o accordi attivi.");
                    exitView = true;
                    continue;
                }

                displayPaymentsTable(payments);

                LOGGER.info("\nComandi: [Indice] Dettagli e Paga | [b] Torna alla Home");
                LOGGER.info("Scelta: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("b")) {
                    exitView = true;
                } else {
                    int index = Integer.parseInt(input);
                    handlePaymentAction(payments.get(index));
                }
            } catch (PersistenceException e) {
                LOGGER.log(Level.SEVERE, "Errore nel recupero dei pagamenti: {0}", e.getMessage());
                exitView = true;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                LOGGER.info("Selezione non valida. Riprova.");
            }
        }
        backToHome();
    }

    private void displayPaymentsTable(List<PaymentBean> list) {
        Session.UserRole role = Session.getSingletonInstance().getRole();
        String counterpartyHeader = (role == Session.UserRole.ARTIST) ? "Locale" : "Artista";

        String header = String.format("%-3s | %-20s | %-12s | %-15s | %-10s",
                                      "ID", counterpartyHeader, "Data Evento", "Il Tuo Stato", "Scadenza");
        LOGGER.info(header);
        LOGGER.info("--------------------------------------------------------------------------------");

        for (int i = 0; i < list.size(); i++) {
            PaymentBean p = list.get(i);
            String counterparty = (role == Session.UserRole.ARTIST) ? p.getVenueName() : p.getArtistStageName();

            // Determiniamo lo stato basandoci sui booleani del bean (isCachetPaid/isDepositPaid)
            boolean paid = (role == Session.UserRole.ARTIST) ? p.isDepositPaid() : p.isCachetPaid();
            String status = paid ? "PAGATO" : "DA PAGARE";

            String row = String.format("[%d] | %-20.20s | %-12s | %-15s | %-10s",
                    i, counterparty, p.getStartingDate(), status, p.getPaymentDeadlineString());
            LOGGER.info(row);
        }
    }

    private void handlePaymentAction(PaymentBean bean) {
        Session.UserRole role = Session.getSingletonInstance().getRole();
        boolean alreadyPaid = (role == Session.UserRole.ARTIST) ? bean.isDepositPaid() : bean.isCachetPaid();

        LOGGER.info("\n--- DETTAGLI ACCORDO ---");
        if (role == Session.UserRole.ARTIST) {
            LOGGER.info("Locale: " + bean.getVenueName());
            LOGGER.info("Tua Cauzione (" + bean.getDeposit() + "€): " + (bean.isDepositPaid() ? "VERSATA" : "IN ATTESA"));
            LOGGER.info("Cachet del Gestore (" + bean.getCachet() + "€): " + (bean.isCachetPaid() ? "VERSATO" : "IN ATTESA"));
        } else {
            LOGGER.info("Artista: " + bean.getArtistStageName());
            LOGGER.info("Tuo Cachet (" + bean.getCachet() + "€): " + (bean.isCachetPaid() ? "VERSATO" : "IN ATTESA"));
            LOGGER.info("Cauzione Artista (" + bean.getDeposit() + "€): " + (bean.isDepositPaid() ? "VERSATA" : "IN ATTESA"));
        }
        LOGGER.info("Data Evento: " + bean.getStartingDate() + " " + bean.getStartingTime());
        LOGGER.info("Deadline Pagamento: " + bean.getPaymentDeadline());

        if (alreadyPaid) {
            LOGGER.info("\nHai già completato la tua parte di pagamento per questo accordo.");
            LOGGER.info("Premi INVIO per tornare alla lista...");
            scanner.nextLine();
        } else {
            LOGGER.info("\nVuoi procedere al pagamento ora? (s/n)");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                processStripePayment(bean);
            }
        }
    }

    private void processStripePayment(PaymentBean bean) {
        try {
            // Check validità (stessa logica del controller GUI)
            if (!controller.isPaymentStillValid(bean)) {
                LOGGER.info("Il pagamento è scaduto. L'accordo è stato annullato e i fondi (se presenti) rimborsati.");
                return;
            }

            String url = controller.getPaymentUrl(bean);
            LOGGER.info("Apertura del browser per il pagamento sicuro su Stripe...");

            // Apre il browser reale
            if (openWebpage(url)) {
                System.out.println("\n------------------------------------------------------------");
                System.out.println("1. Completa la transazione nel browser.");
                System.out.println("2. Verrai reindirizzato a una pagina (che probabilmente darà errore 404).");
                System.out.println("3. Guarda l'URL nella barra degli indirizzi.");
                System.out.println("4. Copia la stringa dopo 'session_id=' (inizia con cs_test_).");
                System.out.println("------------------------------------------------------------");

                LOGGER.info("Incolla qui il Session ID (cs_test_...): ");
                String rawInput = scanner.nextLine().trim();

                // Meccanismo di sicurezza: se l'utente incolla tutto l'URL, estraiamo l'ID
                String sessionId = extractSessionId(rawInput);

                // Validazione generica: deve iniziare con cs_
                if (sessionId.startsWith("cs_")) {
                    controller.completePaymentWorkflow(bean, sessionId);
                    LOGGER.info("\n*** PAGAMENTO REGISTRATO CON SUCCESSO! ***");
                } else {
                    LOGGER.info("Errore: L'identificativo inserito non sembra un ID di sessione valido.");
                }
            }
        } catch (PaymentServiceException | PersistenceException e) {
            LOGGER.log(Level.INFO, "Errore durante il pagamento: {0}", e.getMessage());
        }
    }

    /**
     * Metodo Robusto per l'estrazione dell'ID Sessione.
     * Pulisce l'input da:
     * - Intero URL (cerca session_id=)
     * - Parametri query successivi (delimitati da &)
     * - Frammenti anchor/hash del browser (delimitati da #)
     */
    private String extractSessionId(String input) {
        String result = input.trim();

        // 1. Se è un URL, isola la parte dopo session_id=
        if (result.contains("session_id=")) {
            result = result.split("session_id=")[1];
        }

        // 2. Rimuove eventuali parametri successivi (&payment_intent=...)
        if (result.contains("&")) {
            result = result.split("&")[0];
        }

        // 3. Rimuove frammenti hash (#fid...) generati dal checkout di Stripe
        if (result.contains("#")) {
            result = result.split("#")[0];
        }

        return result;
    }

    private boolean openWebpage(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return true;
            }
        } catch (Exception e) {
            LOGGER.fine("Errore apertura browser: " + e.getMessage());
        }
        return false;
    }

    private void backToHome() {
        if (Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST) {
            Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.ARTIST_HOME);
        } else {
            Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGER_HOME);
        }
    }
}
