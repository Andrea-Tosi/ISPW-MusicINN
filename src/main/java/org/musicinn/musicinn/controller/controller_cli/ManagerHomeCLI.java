package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.util.Session;

import java.util.Scanner;
import java.util.logging.Logger;

public class ManagerHomeCLI {
    private static final Logger LOGGER = Logger.getLogger(ManagerHomeCLI.class.getName());
    private final Scanner scanner;

    public ManagerHomeCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        String username = Session.getSingletonInstance().getUser().getUsername();
        LOGGER.info("\n--- HOME GESTORE (" + username + ") ---");
        LOGGER.info("1. Pubblica un nuovo Annuncio");
        LOGGER.info("2. Accetta una Candidatura");
        LOGGER.info("3. Gestisci Pagamenti e Accordi");
        LOGGER.info("4. Gestisci Rider Tecnico");
        LOGGER.info("5. Logout");
        LOGGER.info("Scelta: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.PUBLISH_ANNOUNCEMENT);
            case "2" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.ACCEPT_APPLICATION);
            case "3" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGE_PAYMENTS);
            case "4" -> Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGE_RIDER);
            case "5" -> {
                Session.getSingletonInstance().setUser(null);
                Session.getSingletonInstance().setRole(null);
                Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.LOGIN);
                LOGGER.info("Logout effettuato.");
            }
            default -> LOGGER.info("Scelta non valida.");
        }
    }
}
