package org.musicinn.musicinn.util.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PaymentBean {
    private int id;
    private LocalDateTime paymentDeadline;
    private String paymentDeadlineString;
    private boolean isCachetPaid;
    private boolean isDepositPaid;
    private LocalDate startingDate;
    private LocalTime startingTime;
    private Double cachet;
    private Double deposit;
    private String artistStageName;
    private String venueName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(LocalDateTime paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public String getPaymentDeadlineString() {
        return paymentDeadlineString;
    }

    public void setPaymentDeadlineString(String paymentDeadlineString) {
        this.paymentDeadlineString = paymentDeadlineString;
    }

    public boolean isCachetPaid() {
        return isCachetPaid;
    }

    public void setCachetPaid(boolean cachetPaid) {
        isCachetPaid = cachetPaid;
    }

    public boolean isDepositPaid() {
        return isDepositPaid;
    }

    public void setDepositPaid(boolean depositPaid) {
        isDepositPaid = depositPaid;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public Double getCachet() {
        return cachet;
    }

    public void setCachet(Double cachet) {
        this.cachet = cachet;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public String getArtistStageName() {
        return artistStageName;
    }

    public void setArtistStageName(String artistStageName) {
        this.artistStageName = artistStageName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
}
