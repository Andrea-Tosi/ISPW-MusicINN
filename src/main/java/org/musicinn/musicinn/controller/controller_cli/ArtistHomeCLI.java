package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.util.Session;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtistHomeCLI {
    private static final Logger LOGGER = Logger.getLogger(ArtistHomeCLI.class.getName());
    private final Scanner scanner;

    public ArtistHomeCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        String username = Session.getSingletonInstance().getUser().getUsername();
        LOGGER.log(Level.INFO, "\n--- HOME ARTISTA ({0}) ---", username);
        LOGGER.info("1. Cerca Annunci e Candidati");
        LOGGER.info("2. Gestisci Pagamenti e Accordi");
        LOGGER.info("3. Gestisci Rider Tecnico");
        LOGGER.info("4. Logout");
        LOGGER.info("Scelta: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.APPLY_EVENT);
            case "2" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGE_PAYMENTS);
            case "3" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGE_RIDER);
            case "4" -> {
                Session.getSingletonInstance().setUser(null);
                Session.getSingletonInstance().setRole(null);
                Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.LOGIN);
                LOGGER.info("Logout effettuato.");
            }
            default -> LOGGER.info("Scelta non valida.");
        }
    }
}
