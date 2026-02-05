package org.musicinn.musicinn.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.PaymentBean;
import org.musicinn.musicinn.util.exceptions.PaymentServiceException;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test di integrazione tra il sistema MusicINN e l'API di Stripe.
 * @author Andrea Tosi
 */
class StripeIntegrationTest {
    private static PaymentController controller;

    @BeforeAll
    static void setup() {
        controller = PaymentServiceFactory.getPaymentController();

        // Simulazione di una sessione utente
        User mockUser = new Artist();
        mockUser.setUsername("test_user");
        Session.getSingletonInstance().setUser(mockUser);
        Session.getSingletonInstance().setRole(Session.UserRole.MANAGER);
    }

    /**
     * Il test fornisce al sistema un PaymentBean contenente dettagli reali su cachet e cauzione.
     * Viene verificato che il sistema sia in grado di contattare Stripe e ricevere un URL univoco per il checkout.
     * Assicura che l'accordo economico tra le parti venga correttamente tradotto in una transazione valida sulla piattaforma Escrow.
     * @throws PaymentServiceException
     */
    @Test
    void testGenerateStripeCheckoutUrl() throws PaymentServiceException {
        PaymentBean bean = new PaymentBean();
        bean.setCachet(150.0);
        bean.setDeposit(50.0);
        bean.setStartingDate(LocalDate.now().plusDays(10));
        bean.setStartingTime(LocalTime.of(21, 0));

        String url = controller.getPaymentUrl(bean);

        // Verifica: Stripe deve restituire un URL che punta ai suoi server di checkout
        assertNotNull(url, "L'URL di checkout non deve essere nullo");
        assertTrue(url.startsWith("https://checkout.stripe.com"),
                "L'URL generato dal sistema deve puntare al dominio ufficiale Stripe");
    }

    /**
     * Simula la richiesta di creazione di un account "Express" per un nuovo utente.
     * Questa operazione è fondamentale per il funzionamento del sistema: ogni utente deve avere
     * un'identità finanziaria su Stripe per poter inviare (Manager) o ricevere (Artista) pagamenti in modo sicuro.
     * @throws PaymentServiceException
     */
    @Test
    void testCreateStripeConnectedAccount() throws PaymentServiceException {
        String email = "test_integration_" + System.currentTimeMillis() + "@musicinn.org";

        String accountId = controller.createPaymentAccount(email);

        // Verifica: Stripe restituisce un ID che inizia con 'acct_'
        assertNotNull(accountId);
        assertTrue(accountId.startsWith("acct_"), "Il sistema deve ricevere un ID account valido da Stripe");
    }

    /**
     * Tenta di completare un flusso di pagamento passando un identificativo di sessione (cs_test...) volutamente errato o inesistente.
     */
    @Test
    void testInvalidRefundIntent() {
        PaymentBean bean = new PaymentBean();
        bean.setId(999); // ID fittizio
        bean.setPaymentDeadline(java.time.LocalDateTime.now().plusDays(1));
        // Verifica come il sistema gestisce un errore di Stripe (es. ID transazione inesistente)
        // Questo testa la robustezza del PaymentController nel catturare le eccezioni del service
        assertThrows(PaymentServiceException.class, () -> {
            // Tentativo di rimborsare un ID falso
            controller.completePaymentWorkflow(bean, "cs_invalid_id");
        }, "Il sistema deve sollevare un'eccezione se Stripe nega l'operazione");
    }
}
