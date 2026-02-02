package org.musicinn.musicinn.util;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class EmailVerifier {
    private final Random random = new Random();

    private EmailVerifier() {}

    private static class SingletonContainer{
        public static final EmailVerifier singletonInstance = new EmailVerifier();
    }

    public static EmailVerifier getSingletonInstance() {
        return SingletonContainer.singletonInstance;
    }

    private final Map<String, VerificationEntry> verificationCodesCache = new ConcurrentHashMap<>();

    private record VerificationEntry(String code, long expiresAt) {}

    private static final Logger LOGGER = Logger.getLogger(EmailVerifier.class.getName());
    private static final long EXPIRATION_TIME_MS = 10 * 60 * 1000L; //minuti in millisecondi

    public void sendCode(String email) {
        String code = generateSixDigitCode();
        long expiresAt = System.currentTimeMillis() + EXPIRATION_TIME_MS;
        verificationCodesCache.put(email, new VerificationEntry(code, expiresAt));

        try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(email, "Verifica email per registrarti a MusicINN", code);
        } catch (RuntimeException e) {
            verificationCodesCache.remove(email);
            LOGGER.fine(e.getMessage());
        }
    }

    public Boolean checkEnteredCode(String email, String enteredCode) {
        VerificationEntry entry = verificationCodesCache.get(email);
        // CASO A: Il codice non esiste (mai inviato o già rimosso)
        if (entry == null) {
            return false;
        }
        // Controllo della scadenza
        long currentTime = System.currentTimeMillis();
        if (currentTime > entry.expiresAt()) {
            // CASO B: Codice scaduto
            verificationCodesCache.remove(email);
            return false;
        }
        // Confronto dei codici
        if (entry.code().equals(enteredCode)) {
            // CASO C: Successo
            // Rimuove il codice perché evitare attacchi replay
            verificationCodesCache.remove(email);
            return true;
        }
        return false;
    }

    public void invalidateVerificationCode(String email) {
        verificationCodesCache.remove(email);
    }

    private String generateSixDigitCode() {
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }
}
