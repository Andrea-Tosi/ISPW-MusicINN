package org.musicinn.musicinn.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class FxmlPathLoader {
    private FxmlPathLoader() {}

    private static final Properties props = new Properties();
    private static final Logger LOGGER = Logger.getLogger(FxmlPathLoader.class.getName());

    static {
        // Carica il file app-paths.properties
        try (InputStream input = FxmlPathLoader.class.getResourceAsStream("/app-paths.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (Exception ex) {
            LOGGER.fine(ex.getMessage());
        }
    }

    public static String getPath(String key) {
        return props.getProperty(key);
    }
}