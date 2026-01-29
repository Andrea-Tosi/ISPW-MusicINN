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
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
                    deadline.format(formatter),
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
        payment.setPaymentDeadline(LocalDateTime.parse(v[2], formatter));

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
            boolean updated = false;

            for (int numOfLine = 1; numOfLine < lines.size(); numOfLine++) {
                String[] parts = lines.get(numOfLine).split(DELIMITER);
                if (Integer.parseInt(parts[0]) == applicationId) {

                    // Aggiornamento dell'ID transazione specifico
                    if (role == Session.UserRole.MANAGER) {
                        parts[3] = transactionId; // venue_payment_intent_id
                    } else {
                        parts[4] = transactionId; // artist_payment_intent_id
                    }

                    // Logica ricalcolo EscrowState (simulata come nel DB)
                    boolean venuePaid = !parts[3].equals("null");
                    boolean artistPaid = !parts[4].equals("null");

                    if (venuePaid && artistPaid) {
                        parts[1] = EscrowState.SECURED.toString();
                    } else if (venuePaid || artistPaid) {
                        parts[1] = EscrowState.PARTIAL.toString();
                    }

                    lines.set(numOfLine, String.join(DELIMITER, parts));
                    updated = true;
                    break;
                }
            }

            if (updated) {
                Files.write(path, lines);
            }
        } catch (IOException e) {
            throw new CSVException("Errore durante l'aggiornamento dello stato del pagamento: " + e.getMessage());
        }
    }

    @Override
    public List<String> markAsRefunded(int applicationId) throws CSVException {
        List<String> peopleToRefund = new ArrayList<>();
        try {
            Path path = Paths.get(CSV_FILE);
            List<String> lines = Files.readAllLines(path);
            boolean updated = false;

            for (int numOfLine = 1; numOfLine < lines.size(); numOfLine++) {
                String[] parts = lines.get(numOfLine).split(DELIMITER);
                if (Integer.parseInt(parts[0]) == applicationId) {

                    // Verifica deadline prima di rimborsare
                    LocalDateTime deadline = LocalDateTime.parse(parts[2], formatter);
                    if (deadline.isBefore(LocalDateTime.now())) {

                        // Raccogliamo gli ID transazione esistenti (se non sono "null")
                        if (!parts[3].equals("null")) peopleToRefund.add(parts[3]);
                        if (!parts[4].equals("null")) peopleToRefund.add(parts[4]);

                        // Cambiamo stato in REFUNDED
                        parts[1] = EscrowState.REFUNDED.toString();
                        lines.set(numOfLine, String.join(DELIMITER, parts));
                        updated = true;
                    }
                    break;
                }
            }

            if (updated) {
                Files.write(path, lines);
            }
            return peopleToRefund;
        } catch (IOException e) {
            throw new CSVException("Errore durante la procedura di rimborso: " + e.getMessage());
        }
    }
}
