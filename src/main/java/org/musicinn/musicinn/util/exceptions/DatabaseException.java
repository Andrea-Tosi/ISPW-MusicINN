package org.musicinn.musicinn.util.exceptions;

public class DatabaseException extends Exception {
    public DatabaseException(String string) {
        super("A causa di un errore nel database, il salvataggio del rider tecnico non è andato a buon fine. Riprova.");
    }
}
