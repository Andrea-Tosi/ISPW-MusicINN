package org.musicinn.musicinn.util;

import org.musicinn.musicinn.controller.controller_application.LoginController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class EmailVerifier {
    private EmailVerifier() {};

    private static class SingletonContainer{
        public final static EmailVerifier singletonInstance = new EmailVerifier();
    }

    public static final EmailVerifier getSingletonInstance() {
        return SingletonContainer.singletonInstance;
    }

    private final Map<String, VerificationEntry> verificationCodesCache = new ConcurrentHashMap<>();

    private record VerificationEntry(String code, long expiresAt) {}

    private static final long EXPIRATION_TIME_MS = 10 * 60 * 1000; //minuti in millisecondi

    public void sendCode(String email) {
        String code = generateSixDigitCode();
        long expiresAt = System.currentTimeMillis() + EXPIRATION_TIME_MS;
        verificationCodesCache.put(email, new VerificationEntry(code, expiresAt));

        try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(email, "Verifica email per registrarti a MusicINN", code);
        } catch (RuntimeException e) {
            verificationCodesCache.remove(email);
            throw new RuntimeException(e);
        }
    }

    public Boolean checkEnteredCode(String email, String code) {
        return code.equals(verificationCodesCache.get(email).code);
    }

    public void invalidateVerificationCode(String email) {
        verificationCodesCache.remove(email);
    }

    private String generateSixDigitCode() {
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }
}
