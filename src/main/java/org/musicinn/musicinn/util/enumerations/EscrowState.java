package org.musicinn.musicinn.util.enumerations;

public enum EscrowState {
    WAITING_BOTH, // L'accordo è nato, ma nessuno ha ancora versato la sua quota.
    PARTIAL, // Uno dei due ha pagato, l'altro no, ma il timer non è ancora scaduto.
    SECURED, // Entrambi hanno versato i fondi. I soldi sono "congelati" nei server di Stripe. È lo stato di "sicurezza" prima dell'evento.
    COMPLETED, // L'evento si è svolto. La piattaforma trasferisce il cachet all'account dell'Artista e restituisce la cauzione all'Artista.
    NOT_COMPLETED, // L'evento è stato annullato a causa dell'assenza dell'artista. La piattaforma trasferisce la cauzione all'account del Manager e restituisce il cachet al Manager.
    REFUNDED // Il timer è scaduto. La piattaforma eventualmente restituisce la quota a chi l'ha versata.
}
