package org.musicinn.musicinn.controller.controller_application.payment_controller;

import com.stripe.exception.StripeException;
import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.PaymentService;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.ApplicationBean;
import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.PaymentDAO;
import org.musicinn.musicinn.util.enumerations.EscrowState;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentController {
    private final PaymentService paymentService;
    private static final int DAYS_OF_DEADLINE = 5;

    // Il costruttore è protected perché può essere chiamato solamente da PaymentServiceFactory
    protected PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public int createPayment(ApplicationBean applicationBean) throws PersistenceException {
        PaymentDAO paymentDAO = DAOFactory.getPaymentDAO();
        paymentDAO.save(applicationBean.getId(), DAYS_OF_DEADLINE);

        return DAYS_OF_DEADLINE;
    }

    public List<PaymentBean> getPayments() throws PersistenceException {
        String username = Session.getSingletonInstance().getUser().getUsername();

        if (Session.UserRole.MANAGER.equals(Session.getSingletonInstance().getRole())) {
            return getPaymentsForManager(username);
        } else {
            return getPaymentsForArtist(username);
        }
    }

    private List<PaymentBean> getPaymentsForManager(String username) throws PersistenceException {
        List<PaymentBean> beans = new ArrayList<>();
        int idVenue = DAOFactory.getVenueDAO().getActiveVenueIdByManager(username);
        List<Announcement> closedAnnouncements = DAOFactory.getAnnouncementDAO().findClosedByIdVenue(idVenue);

        for (Announcement ann : closedAnnouncements) {
            Application acceptedApp = DAOFactory.getApplicationDAO().findAcceptedByAnnouncement(ann.getId());
            if (acceptedApp == null) continue;
            processPaymentForApplication(acceptedApp, ann, beans);
        }
        return beans;
    }

    private void processPaymentForApplication(Application app, Announcement ann, List<PaymentBean> beans)
            throws PersistenceException {

        Payment payment = DAOFactory.getPaymentDAO().findByApplicationId(app.getId());

        if (isPaymentActive(payment)) {
            Artist artist = DAOFactory.getArtistDAO().read(app.getUsernameArtist());
            beans.add(createPaymentBean(payment, ann, artist, app.getId()));
        } else {
            handleExpiredPayment(payment, app.getId());
        }
    }

    private boolean isPaymentActive(Payment payment) {
        return payment.getPaymentDeadline().isAfter(LocalDateTime.now());
    }

    private PaymentBean createPaymentBean(Payment p, Announcement ann, Artist artist, int appId) {
        PaymentBean bean = new PaymentBean();

        bean.setArtistStageName(artist.getStageName());
        bean.setId(appId);
        bean.setCachet(ann.getCachet());
        bean.setDeposit(ann.getDeposit());
        bean.setStartingDate(ann.getStartEventDay());
        bean.setStartingTime(ann.getStartEventTime());
        bean.setPaymentDeadline(p.getPaymentDeadline());
        bean.setPaymentDeadlineString(formatRemainingTime(p.getPaymentDeadline()));
        bean.setCachetPaid(p.getManagerPaymentIntentId() != null);
        bean.setDepositPaid(p.getArtistPaymentIntentId() != null);

        return bean;
    }

    private void handleExpiredPayment(Payment payment, int appId) throws DatabaseException {
        if (payment.getState().equals(EscrowState.REFUNDED)) return;

        // Logica di rimborso delegata al service (DIP)
        List<String> refundIntents = null;
        try {
            refundIntents = DAOFactory.getPaymentDAO().markAsRefunded(appId);
        } catch (org.musicinn.musicinn.util.exceptions.PersistenceException e) {
            throw new RuntimeException(e);
        }
        for (String intentId : refundIntents) {
            executeSafeRefund(intentId);
        }
    }

    private void executeSafeRefund(String intentId) {
        try {
            paymentService.issueRefund(intentId);
        } catch (Exception e) {
            System.err.println("Fallito rimborso automatico on-access per: " + intentId);
        }
    }

    private List<PaymentBean> getPaymentsForArtist(String username) throws PersistenceException {
        List<PaymentBean> beans = new ArrayList<>();

        // 1. Chiedo le mie candidature accettate
        List<Application> applications = DAOFactory.getApplicationDAO().findAcceptedByArtist(username);

        for (Application app : applications) {
            // 2. Chiedo il record di pagamento
            Payment payment = DAOFactory.getPaymentDAO().findByApplicationId(app.getId());

            // 3. Chiedo i dettagli dell'annuncio
            Announcement announcement = DAOFactory.getAnnouncementDAO().findByApplicationId(app.getId());

            // 4. Chiedo i dettagli del locale (il passo aggiuntivo che hai citato)
            Venue venue = DAOFactory.getVenueDAO().findByApplicationId(announcement.getId());

            // 5. Impacchetto tutto nel Bean per la GUI
            if (payment.getPaymentDeadline().isAfter(LocalDateTime.now())) {
                beans.add(createArtistPaymentBean(payment, announcement, venue, app.getId()));
            } else if (!payment.getState().equals(EscrowState.REFUNDED)) {
                List<String> peopleToRefund = null;
                try {
                    peopleToRefund = DAOFactory.getPaymentDAO().markAsRefunded(app.getId());
                } catch (org.musicinn.musicinn.util.exceptions.PersistenceException e) {
                    throw new RuntimeException(e);
                }

                for (String intentId : peopleToRefund) {
                    try {
                        paymentService.issueRefund(intentId);
                    } catch (Exception e) {
                        System.err.println("Fallito rimborso automatico on-access per: " + intentId);
                    }
                }
            }
        }
        return beans;
    }

    private PaymentBean createArtistPaymentBean(Payment p, Announcement ann, Venue venue, int appId) {
        PaymentBean bean = new PaymentBean();

        bean.setVenueName(venue.getName());
        bean.setId(appId);
        bean.setCachet(ann.getCachet());
        bean.setDeposit(ann.getDeposit());
        bean.setPaymentDeadline(p.getPaymentDeadline());
        bean.setPaymentDeadlineString(formatRemainingTime(p.getPaymentDeadline()));
        bean.setStartingDate(ann.getStartEventDay());
        bean.setStartingTime(ann.getStartEventTime());
        bean.setCachetPaid(p.getManagerPaymentIntentId() != null);
        bean.setDepositPaid(p.getArtistPaymentIntentId() != null);

        return bean;
    }

    private String formatRemainingTime(LocalDateTime deadline) {
        LocalDateTime now = LocalDateTime.now();

        // Se la deadline è già passata
        if (now.isAfter(deadline)) {
            return "Tempo scaduto";
        }

        Duration duration = Duration.between(now, deadline);

        long days = duration.toDays();
        if (days > 0) {
            return days + (days == 1 ? " giorno" : " giorni");
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return hours + (hours == 1 ? " ora" : " ore");
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + (minutes == 1 ? " minuto" : " minuti");
        }

        return "Pochi secondi";
    }

    public String getPaymentUrl(PaymentBean bean) throws Exception {
        return paymentService.getCheckoutSessionUrl(bean);
    }

    /**
     * Verifica se il pagamento è ancora processabile.
     * Se scaduto, aggiorna lo stato sul DB prima di ritornare il risultato.
     */
    public boolean isPaymentStillValid(PaymentBean bean) throws DatabaseException {
        if (bean.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            // 1. Aggiorna il DB e prendi gli ID delle transazioni da rimborsare
            List<String> peopleToRefund = null;
            try {
                peopleToRefund = DAOFactory.getPaymentDAO().markAsRefunded(bean.getId());
            } catch (org.musicinn.musicinn.util.exceptions.PersistenceException e) {
                throw new RuntimeException(e);
            }
            // Logica di "Auto-pulizia": se è scaduto, lo marchiamo nel DB
            // 2. Esegui il rimborso reale su Stripe per ogni transazione trovata
            for (String id : peopleToRefund) {
                try {
                    paymentService.issueRefund(id);
                } catch (Exception e) {
                    // Logga l'errore: il DB è REFUNDED ma Stripe ha fallito (rari casi)
                    throw new DatabaseException("Rimborso Stripe fallito per: " + id);
                }
            }
            return false; // Scaduto
        }
        return true; // Ancora valido
    }

    public String createPaymentAccount(String email) throws StripeException {
        return paymentService.createPaymentAccount(email);
    }

    public void completePaymentWorkflow(PaymentBean paymentBean, String stripeSessionId) throws Exception {
        if (!isPaymentStillValid(paymentBean)) {
            throw new Exception("Il pagamento è scaduto durante l'operazione.");
        }

        String transactionId = paymentService.getPaymentIntentFromSession(stripeSessionId);

        DAOFactory.getPaymentDAO().updatePaymentState(
                paymentBean.getId(),
                Session.getSingletonInstance().getRole(),
                transactionId
        );
    }
}
