package org.musicinn.musicinn.util.logger;

import java.util.logging.ConsoleHandler;

/**
 * Classe istanziata via Reflection dal LogManager tramite logging.properties
 */
public class LoggerConsole extends ConsoleHandler {
    public LoggerConsole() {
        super();
        setOutputStream(System.out); // Dirotta i log su System.out invece che su System.err
    }
}
