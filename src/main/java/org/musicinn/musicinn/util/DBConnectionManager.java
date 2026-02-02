package org.musicinn.musicinn.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBConnectionManager {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger LOGGER = Logger.getLogger(DBConnectionManager.class.getName());

    // Parametri di connessione
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD"); // <--- Metti la tua password di MySQL!

    private DBConnectionManager() {}

    private static class SingletonContainer{
        public static final DBConnectionManager singletonInstance = new DBConnectionManager();
    }

    public static DBConnectionManager getSingletonInstance() {
        return DBConnectionManager.SingletonContainer.singletonInstance;
    }

    private Connection connection = null;

    public Connection getConnection() {
        try {
            // Controlliamo se la connessione è null O se è stata chiusa (es. timeout)
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                LOGGER.info("✅ Nuova connessione MySQL stabilita!");
            }
        } catch (SQLException e) {
            LOGGER.info("❌ Errore durante la connessione: " + e.getMessage());
            return null; // Restituisce null solo se fallisce l'apertura
        }
        return connection; // Restituisce la connessione APERTA
    }
}
