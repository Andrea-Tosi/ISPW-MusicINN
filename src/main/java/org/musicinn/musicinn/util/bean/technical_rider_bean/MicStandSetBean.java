package org.musicinn.musicinn.util.bean.technical_rider_bean;

public class MicStandSetBean {
    private Boolean isTall;
    private int quantity;

    public MicStandSetBean(int quantity, Boolean isTall) {
        this.quantity = quantity;
        this.isTall = isTall;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getTall() {
        return isTall;
    }

    public void setTall(Boolean tall) {
        isTall = tall;
    }
}
