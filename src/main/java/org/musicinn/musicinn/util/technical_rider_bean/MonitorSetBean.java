package org.musicinn.musicinn.util.technical_rider_bean;

public class MonitorSetBean {
    private Boolean isPowered;
    private int quantity;

    public MonitorSetBean(int quantity, Boolean isPowered) {
        this.quantity = quantity;
        this.isPowered = isPowered;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getPowered() {
        return isPowered;
    }

    public void setPowered(Boolean powered) {
        isPowered = powered;
    }
}
