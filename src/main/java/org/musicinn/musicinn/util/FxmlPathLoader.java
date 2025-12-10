package org.musicinn.musicinn.util;

import java.io.InputStream;
import java.util.Properties;

public class FxmlPathLoader {
    private static final Properties props = new Properties();

    static {
        // Carica il file app-paths.properties
        try (InputStream input = FxmlPathLoader.class.getClassLoader().getResourceAsStream("app-paths.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (Exception ex) {
            System.err.println("Errore caricamento configurazione: " + ex.getMessage());
        }
    }

    public static String getPath(String key) {
        return props.getProperty(key);
    }
}
