package org.musicinn.musicinn.util.exceptions;

public class DatabaseException extends PersistenceException {
    public DatabaseException(String message) {
        super(message);
    }
}
