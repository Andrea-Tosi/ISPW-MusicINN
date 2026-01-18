package org.musicinn.musicinn.util.exceptions;

public class EmailAlreadyUsedException extends Exception {
    public EmailAlreadyUsedException() {
        super("L'email inserita è già associata a un account.");
    }
}
