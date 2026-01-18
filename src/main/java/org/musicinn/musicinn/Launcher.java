package org.musicinn.musicinn;

import javafx.application.Application;
import org.musicinn.musicinn.util.Session;

public class Launcher {
    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--demo")) {
                Session.getSingletonInstance().setPersistenceType(Session.PersistenceType.MEMORY);
            }
            if (arg.equalsIgnoreCase("--cli")) {
                Session.getSingletonInstance().setInterfaceType(Session.InterfaceType.CLI);
            }
        }
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
