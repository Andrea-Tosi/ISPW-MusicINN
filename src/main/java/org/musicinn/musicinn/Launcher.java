package org.musicinn.musicinn;

import javafx.application.Application;
import org.musicinn.musicinn.controller.controller_cli.CLIManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.logger.LogConfigurator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {
    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
            LogConfigurator.setup();
        try {
            if (args.length > 2) {
                throw new IllegalArgumentException("Troppi argomenti! Massimo due consentiti.");
            }

            parseArguments(args);

            if (Session.getSingletonInstance().getInterfaceType().equals(Session.InterfaceType.GUI)) {
                Application.launch(App.class, args);
            } else {
                startCLIVersion();
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void parseArguments(String[] args) {
        Session session = Session.getSingletonInstance();
        // Imposta i valori di default
        session.setPersistenceType(Session.PersistenceType.DATABASE);
        session.setInterfaceType(Session.InterfaceType.GUI);

        boolean pDefined = false;
        boolean iDefined = false;

        for (String arg : args) {
            if (isPersistenceArg(arg)) {
                if (pDefined) throw new IllegalArgumentException("Persistenza già definita!");
                applyPersistence(arg, session);
                pDefined = true;
            } else if (isInterfaceArg(arg)) {
                if (iDefined) throw new IllegalArgumentException("Interfaccia già definita!");
                applyInterface(arg, session);
                iDefined = true;
            } else {
                throw new IllegalArgumentException("Parametro sconosciuto: " + arg);
            }
        }
    }

    private static boolean isPersistenceArg(String arg) {
        return arg.equals("--demo") || arg.equals("--fs") || arg.equals("--db");
    }

    private static boolean isInterfaceArg(String arg) {
        return arg.equals("--cli") || arg.equals("--gui");
    }

    private static void applyPersistence(String arg, Session s) {
        switch (arg) {
            case "--demo" -> s.setPersistenceType(Session.PersistenceType.MEMORY);
            case "--fs"   -> s.setPersistenceType(Session.PersistenceType.FILE);
            default       -> s.setPersistenceType(Session.PersistenceType.DATABASE);
        }
    }

    private static void applyInterface(String arg, Session s) {
        if (arg.equals("--cli")) {
            s.setInterfaceType(Session.InterfaceType.CLI);
        } else {
            s.setInterfaceType(Session.InterfaceType.GUI);
        }
    }

    private static void startCLIVersion() {
        LOGGER.info("Avvio MusicINN in modalità CLI...");
        CLIManager cliManager = new CLIManager();
        cliManager.start();
    }
}
