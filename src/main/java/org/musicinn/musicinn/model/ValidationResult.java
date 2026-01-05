package org.musicinn.musicinn.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean isValid;
    private final List<String> errors;

    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.isValid = true;
    }

    // Aggiunge un errore bloccante
    public void addError(String message) {
        this.errors.add(message);
    }

    // Restituisce true se non ci sono errori (i warning sono permessi)
    public boolean isValidRepressed() {
        return errors.isEmpty();
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    // Utile per stampare rapidamente il risultato in console o GUI
    @Override
    public String toString() {
        if (isValid()) return "Validazione superata con successo.";

        StringBuilder sb = new StringBuilder();
        if (!errors.isEmpty()) {
            sb.append("ERRORI:\n- ").append(String.join("\n- ", errors)).append("\n");
        }
        return sb.toString();
    }
}
