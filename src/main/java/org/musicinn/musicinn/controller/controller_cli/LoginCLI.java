package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.LoginController;
import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.login_bean.ArtistRegistrationBean;
import org.musicinn.musicinn.util.bean.login_bean.CredentialsBean;
import org.musicinn.musicinn.util.bean.login_bean.ManagerRegistrationBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.exceptions.EmailAlreadyUsedException;
import org.musicinn.musicinn.util.exceptions.PaymentServiceException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;
import org.musicinn.musicinn.util.exceptions.UsernameAlreadyUsedException;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginCLI {
    private static final Logger LOGGER = Logger.getLogger(LoginCLI.class.getName());
    private final Scanner scanner;
    private final LoginController loginController = new LoginController();

    public LoginCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        LOGGER.info("\n*** MUSICINN - LOGIN ***");
        LOGGER.info("1. Accedi");
        LOGGER.info("2. Registrati");
        LOGGER.info("3. Esci");
        LOGGER.info("Scelta: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> login();
            case "2" -> signup();
            case "3" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.EXIT);
        }
    }

    private void login() {
        LOGGER.info("Identificativo (User/Email): ");
        String id = scanner.nextLine();
        LOGGER.info("Password: ");
        String pw = scanner.nextLine();

        try {
            User user = loginController.login(new CredentialsBean(id, pw));
            if (user != null) {
                // Navigazione basata sul ruolo: aggiorna solo la Session
                if (user instanceof Artist) {
                    Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.ARTIST_HOME);
                } else if (user instanceof Manager) {
                    Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGER_HOME);
                }
            } else {
                LOGGER.info("Credenziali errate.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Errore: {0}", e.getMessage());
        }
    }

    private void signup() {
        try {
            LOGGER.info("\n--- REGISTRAZIONE UTENTE ---");
            LOGGER.info("Username: ");
            String username = scanner.nextLine();
            LOGGER.info("Email: ");
            String email = scanner.nextLine();
            LOGGER.info("Password: ");
            String password = scanner.nextLine();
            LOGGER.info("Ripeti Password: ");
            String repeatPassword = scanner.nextLine();

            if (!password.equals(repeatPassword)) {
                LOGGER.info("Le password non coincidono!");
                return;
            }

            // 1. Inizio Signup (Validazione unicità e invio mail)
            CredentialsBean credentialsBean = new CredentialsBean(username, email, password);
            loginController.startSignup(credentialsBean);
            LOGGER.info("Codice di verifica inviato all'email fornita.");

            // 2. Verifica Codice
            boolean verified = false;
            while (!verified) {
                LOGGER.info("Digita il codice a 6 cifre (o 'cancella' per uscire): ");
                String code = scanner.nextLine();
                if (code.equalsIgnoreCase("cancella")) return;

                if (loginController.checkEnteredCode(email, code)) {
                    verified = true;
                    LOGGER.info("Email verificata con successo!");
                } else {
                    LOGGER.info("Codice errato. Riprova.");
                }
            }

            // 3. Scelta tipo account
            LOGGER.info("Scegli il tipo di account:");
            LOGGER.info("1. Artista");
            LOGGER.info("2. Gestore");
            String typeChoice = scanner.nextLine();

            if (typeChoice.equals("1")) {
                completeArtistRegistration();
            } else if (typeChoice.equals("2")) {
                completeManagerRegistration();
            }

        } catch (UsernameAlreadyUsedException | EmailAlreadyUsedException e) {
            LOGGER.log(Level.INFO, "Errore: {0}", e.getMessage());
        } catch (PersistenceException e) {
            LOGGER.log(Level.SEVERE, "Errore di database: {0}", e.getMessage());
        } catch (PaymentServiceException e) {
            LOGGER.log(Level.SEVERE, "Errore servizio pagamenti: {0}", e.getMessage());
        }
    }

    private void completeArtistRegistration() throws PaymentServiceException {
        LOGGER.info("\n--- DATI ARTISTA ---");
        LOGGER.info("Nome d'arte: ");
        String stageName = scanner.nextLine();

        // Selezione TypeArtist (Enum)
        LOGGER.info("Tipo Artista (SINGER, BAND, DJ, GROUP, MUSICIAN): ");
        TypeArtist type = TypeArtist.valueOf(scanner.nextLine().toUpperCase());

        LOGGER.info("Esegui inediti? (si/no): ");
        boolean doesUnreleased = scanner.nextLine().equalsIgnoreCase("si");

        LOGGER.info("Città: ");
        String city = scanner.nextLine();
        LOGGER.info("Indirizzo: ");
        String address = scanner.nextLine();

        // Selezione Generi (Esempio semplificato di inserimento multiplo)
        LOGGER.info("Inserisci generi musicali separati da virgola tra i seguenti (ROCK, POP, JAZZ, RAP, TRAP, REGGAE, CLASSICAL, METAL, INDIE, SOUL, FUNK, R_B, DISCO, TECHNO, ELECTRONIC, AMBIENT): ");
        String genresInput = scanner.nextLine();
        List<MusicalGenre> genres = Arrays.stream(genresInput.split(","))
                .map(s -> MusicalGenre.valueOf(s.trim().toUpperCase()))
                .toList();

        ArtistRegistrationBean arb = new ArtistRegistrationBean(stageName, type, doesUnreleased, city, address);
        arb.setGenresList(genres);

        loginController.completeSignup(arb);
        LOGGER.info("Registrazione Artista completata!");
        Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.ARTIST_HOME);
    }

    private void completeManagerRegistration() throws PaymentServiceException {
        LOGGER.info("\n--- DATI GESTORE E LOCALE ---");
        LOGGER.info("Nome Locale: ");
        String venueName = scanner.nextLine();
        LOGGER.info("Città Locale: ");
        String city = scanner.nextLine();
        LOGGER.info("Indirizzo Locale: ");
        String address = scanner.nextLine();

        LOGGER.info("Tipo Locale (BAR, PUB, CLUB, RESTAURANT, DISCO, THEATRE, AUDITORIUM): ");
        TypeVenue type = TypeVenue.valueOf(scanner.nextLine().toUpperCase());

        ManagerRegistrationBean mrb = new ManagerRegistrationBean(venueName, city, address, type);

        loginController.completeSignup(mrb);
        LOGGER.info("Registrazione Gestore completata!");
        Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGER_HOME);
    }
}
