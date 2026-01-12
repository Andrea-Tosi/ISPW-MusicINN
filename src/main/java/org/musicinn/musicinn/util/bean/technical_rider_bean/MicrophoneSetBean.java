package org.musicinn.musicinn.util.bean.technical_rider_bean;

public class MicrophoneSetBean {
    private int quantity;
    private Boolean needsPhantomPower;

    public MicrophoneSetBean(int quantity, Boolean needsPhantomPower) {
        this.quantity = quantity;
        this.needsPhantomPower = needsPhantomPower;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getNeedsPhantomPower() {
        return needsPhantomPower;
    }

    public void setNeedsPhantomPower(Boolean needsPhantomPower) {
        this.needsPhantomPower = needsPhantomPower;
    }
}
