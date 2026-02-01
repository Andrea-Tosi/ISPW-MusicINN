package org.musicinn.musicinn.util.dao.filesystem;

import org.musicinn.musicinn.model.Payment;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.PaymentDAO;
import org.musicinn.musicinn.util.enumerations.EscrowState;
import org.musicinn.musicinn.util.exceptions.CSVException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PaymentDAOCSV implements PaymentDAO {
    private static final String CSV_FILE = "csv_files/payments.csv";
    private static final String HEADER = "application_id;escrow_state;payment_deadline;venue_payment_intent_id;artist_payment_intent_id";
    private static final String DELIMITER = ";";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public PaymentDAOCSV() throws CSVException {
        try {
            Path path = Paths.get(CSV_FILE);
            if (!Files.exists(path)) {
                Path parent = path.getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.writeString(path, HEADER + System.lineSeparator(), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new CSVException("Errore inizializzazione file pagamenti: " + e.getMessage());
        }
    }

    @Override
    public void save(int applicationId, int daysOfDeadline) throws CSVException {
        try {
            LocalDateTime deadline = LocalDateTime.now().plusDays(daysOfDeadline);

            // Creiamo un record iniziale: stato WAITING_BOTH, intent IDs nulli
            String row = String.join(DELIMITER,
                    String.valueOf(applicationId),
                    EscrowState.WAITING_BOTH.toString(),
                    deadline.format(DATE_TIME_FORMATTER),
                    "null", // venue_payment_intent_id
                    "null"  // artist_payment_intent_id
            );

            Files.write(Paths.get(CSV_FILE), (row + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new CSVException("Impossibile salvare il record di pagamento: " + e.getMessage());
        }
    }

    @Override
    public Payment findByApplicationId(int applicationId) throws CSVException {
        try (Stream<String> stream = Files.lines(Paths.get(CSV_FILE))){
            return stream
                    .skip(1) // Salta header
                    .filter(line -> line.startsWith(applicationId + DELIMITER))
                    .map(this::mapToEntity)
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new CSVException("Errore nel recupero del pagamento: " + e.getMessage());
        }
    }

    private Payment mapToEntity(String row) {
        String[] v = row.split(DELIMITER);
        Payment payment = new Payment();

        payment.setState(EscrowState.valueOf(v[1]));
        payment.setPaymentDeadline(LocalDateTime.parse(v[2], DATE_TIME_FORMATTER));

        // Gestione valori null per i Payment Intent
        payment.setManagerPaymentIntentId(v[3].equals("null") ? null : v[3]);
        payment.setArtistPaymentIntentId(v[4].equals("null") ? null : v[4]);

        return payment;
    }

    @Override
    public void updatePaymentState(int applicationId, Session.UserRole role, String transactionId) throws CSVException {
        try {
            Path path = Paths.get(CSV_FILE);
            List<String> lines = Files.readAllLines(path);
            boolean updated = findAndUpdateLine(lines, applicationId, role, transactionId);

            if (updated) {
                Files.write(path, lines);
            }
        } catch (IOException e) {
            throw new CSVException("Errore durante l'aggiornamento dello stato del pagamento: " + e.getMessage());
        }
    }

    private boolean findAndUpdateLine(List<String> lines, int appId, Session.UserRole role, String txId) {
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(DELIMITER);

            if (Integer.parseInt(parts[0]) == appId) {
                updateParts(parts, role, txId);
                lines.set(i, String.join(DELIMITER, parts));
                return true;
            }
        }
        return false;
    }

    private void updateParts(String[] parts, Session.UserRole role, String txId) {
        // 1. Assegnazione dell'ID transazione (Uso dell'indice basato sul ruolo)
        int index = (role == Session.UserRole.MANAGER) ? 3 : 4;
        parts[index] = txId;

        // 2. Ricalcolo Stato (Logica isolata e leggibile)
        boolean venuePaid = !"null".equals(parts[3]);
        boolean artistPaid = !"null".equals(parts[4]);

        parts[1] = determineNewState(venuePaid, artistPaid).toString();
    }

    private EscrowState determineNewState(boolean vPaid, boolean aPaid) {
        if (vPaid && aPaid) return EscrowState.SECURED;
        if (vPaid || aPaid) return EscrowState.PARTIAL;
        return EscrowState.WAITING_BOTH;
    }

    @Override
    public List<String> markAsRefunded(int applicationId) throws CSVException {
        try {
            Path path = Paths.get(CSV_FILE);
            List<String> lines = Files.readAllLines(path);
            List<String> peopleToRefund = processLinesForRefund(lines, applicationId);

            if (!peopleToRefund.isEmpty()) {
                Files.write(path, lines);
            }
            return peopleToRefund;
        } catch (IOException e) {
            throw new CSVException("Errore durante la procedura di rimborso: " + e.getMessage());
        }
    }

    private List<String> processLinesForRefund(List<String> lines, int applicationId) {
        List<String> peopleToRefund = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(DELIMITER);

            // Clausola di guardia: ignora le righe che non corrispondono all'ID
            if (Integer.parseInt(parts[0]) == applicationId) {
                // Trovato l'ID, gestisce la logica specifica ed esce.
                LocalDateTime deadline = LocalDateTime.parse(parts[2], DATE_TIME_FORMATTER);

                if (deadline.isBefore(LocalDateTime.now())) {
                    // Se la deadline è superata, procede
                    collectRefunds(parts, peopleToRefund);
                    markState(parts);
                    lines.set(i, String.join(DELIMITER, parts));
                }
                break;
            }
        }
        return peopleToRefund;
    }

    private void collectRefunds(String[] parts, List<String> refundList) {
        addIfPresent(refundList, parts[3]);
        addIfPresent(refundList, parts[4]);
    }

    private void markState(String[] parts) {
        parts[1] = EscrowState.REFUNDED.toString();
    }

    private void addIfPresent(List<String> list, String value) {
        // Helper method: aggiunge solo se la stringa non è "null" o vuota
        if (value != null && !value.equals("null") && !value.isEmpty()) {
            list.add(value);
        }
    }
}
