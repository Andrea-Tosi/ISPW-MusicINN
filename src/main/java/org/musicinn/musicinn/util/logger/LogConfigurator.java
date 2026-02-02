package org.musicinn.musicinn.util.logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LogConfigurator {
    public static void setup() {
        try {
            // Crea cartella logs se manca
            File logDir = new File("logs");
            if (!logDir.exists()) logDir.mkdir();

            // Carica configurazione
            try (InputStream is = LogConfigurator.class.getClassLoader()
                    .getResourceAsStream("logging.properties")) {
                if (is != null) LogManager.getLogManager().readConfiguration(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
