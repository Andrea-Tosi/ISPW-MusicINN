package org.musicinn.musicinn.util.bean.technical_rider_bean;

import org.musicinn.musicinn.util.enumerations.CableFunction;

public class CableSetBean {
    private CableFunction function;
    private int quantity;

    public CableSetBean(int quantity, CableFunction function) {
        this.quantity = quantity;
        this.function = function;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CableFunction getFunction() {
        return function;
    }

    public void setFunction(CableFunction function) {
        this.function = function;
    }
}
