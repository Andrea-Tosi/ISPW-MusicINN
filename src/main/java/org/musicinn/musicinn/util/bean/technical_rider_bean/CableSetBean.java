package org.musicinn.musicinn.util.bean.technical_rider_bean;

import org.musicinn.musicinn.util.enumerations.CablePurpose;

public class CableSetBean {
    private CablePurpose purpose;
    private int quantity;

    public CableSetBean(int quantity, CablePurpose purpose) {
        this.quantity = quantity;
        this.purpose = purpose;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CablePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(CablePurpose purpose) {
        this.purpose = purpose;
    }
}
