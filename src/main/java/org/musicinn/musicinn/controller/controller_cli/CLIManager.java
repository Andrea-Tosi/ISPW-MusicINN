package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.util.Session;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIManager {
    private static final Logger LOGGER = Logger.getLogger(CLIManager.class.getName());
    private final Scanner scanner;
    private boolean running = true;

    public CLIManager() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (running) {
            Session.CLIView currentView = Session.getSingletonInstance().getCurrentCLIView();

            switch (currentView) {
                case LOGIN -> new LoginCLI(scanner).run();
                case ARTIST_HOME -> new ArtistHomeCLI(scanner).run();
                case MANAGER_HOME -> new ManagerHomeCLI(scanner).run();
                case MANAGE_RIDER -> new ManageTechnicalRiderCLI(scanner).run();
                case PUBLISH_ANNOUNCEMENT -> new PublishAnnouncementCLI(scanner).run();
                case APPLY_EVENT -> new ApplyCLI(scanner).run();
                case ACCEPT_APPLICATION -> new AcceptApplicationCLI(scanner).run();
                case MANAGE_PAYMENTS -> new ManagePaymentsCLI(scanner).run();
                case EXIT -> {
                    LOGGER.info("Chiusura applicazione...");
                    running = false;
                }
                default -> LOGGER.log(Level.SEVERE, "Valore della condizione: {0}", currentView);
            }
        }
        scanner.close();
    }
}
