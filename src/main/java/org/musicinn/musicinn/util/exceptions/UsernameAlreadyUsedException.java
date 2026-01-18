package org.musicinn.musicinn.util.exceptions;

public class UsernameAlreadyUsedException extends Exception {
    public UsernameAlreadyUsedException() {
        super("Lo username inserito è già associato a un account.");
    }
}
