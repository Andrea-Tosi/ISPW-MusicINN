package org.musicinn.musicinn.util.technical_rider_bean;

public class DIBoxSetBean {
    private int quantity;
    private Boolean isActive;

    public DIBoxSetBean(int quantity, Boolean isActive) {
        this.quantity = quantity;
        this.isActive = isActive;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
