package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.EscrowState;

import java.time.LocalDateTime;

public class Payment {
    private String managerPaymentIntentId;
    private String artistPaymentIntentId;
    private LocalDateTime paymentDeadline;
    private EscrowState state;

    public String getManagerPaymentIntentId() {
        return managerPaymentIntentId;
    }

    public void setManagerPaymentIntentId(String managerPaymentIntentId) {
        this.managerPaymentIntentId = managerPaymentIntentId;
    }

    public String getArtistPaymentIntentId() {
        return artistPaymentIntentId;
    }

    public void setArtistPaymentIntentId(String artistPaymentIntentId) {
        this.artistPaymentIntentId = artistPaymentIntentId;
    }

    public LocalDateTime getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(LocalDateTime paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public EscrowState getState() {
        return state;
    }

    public void setState(EscrowState state) {
        this.state = state;
    }
}
