package org.musicinn.musicinn.util.exceptions;

public class CalendarException extends Exception {
    public CalendarException() {
        super("L'orario selezionato non è disponibile: si sovrappone a un altro impegno nel calendario.");
    }
}
