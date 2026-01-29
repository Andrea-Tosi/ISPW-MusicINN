package org.musicinn.musicinn;

import javafx.application.Application;
import org.musicinn.musicinn.util.Session;

public class Launcher {
    public static void main(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException("Troppi argomenti! Massimo due consentiti.");
        }

        Session.PersistenceType pType = Session.PersistenceType.DATABASE;
        Session.InterfaceType iType = Session.InterfaceType.GUI;

        boolean pDefined = false;
        boolean iDefined = false;

        for (String arg : args) {
            switch (arg) {
                case "--demo" -> {
                    if (pDefined) throw new IllegalArgumentException("Persistenza già definita!");
                    pType = Session.PersistenceType.MEMORY;
                    pDefined = true;
                }
                case "--fs" -> {
                    if (pDefined) throw new IllegalArgumentException("Persistenza già definita!");
                    pType = Session.PersistenceType.FILE;
                    pDefined = true;
                }case "--db" -> {
                    if (pDefined) throw new IllegalArgumentException("Persistenza già definita!");
                    pDefined = true;
                }
                case "--cli" -> {
                    if (iDefined) throw new IllegalArgumentException("Interfaccia già definita!");
                    iType = Session.InterfaceType.CLI;
                    iDefined = true;
                }
                case "--gui" -> {
                    if (iDefined) throw new IllegalArgumentException("Interfaccia già definita!");
                    iDefined = true;
                }
                default -> throw new IllegalArgumentException("Parametro sconosciuto: " + arg);
            }
        }

        Session.getSingletonInstance().setPersistenceType(pType);
        Session.getSingletonInstance().setInterfaceType(iType);

        if (Session.getSingletonInstance().getInterfaceType().equals(Session.InterfaceType.GUI)) {
            Application.launch(App.class, args);
        } else {
            startCLIVersion();
        }
    }

    private static void startCLIVersion() {
        System.out.println("Avvio MusicINN in modalità CLI...");
        //TODO istanziare View CLI (es. LoginViewCLI)
    }
}
