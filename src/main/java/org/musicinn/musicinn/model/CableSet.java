package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.CableFunction;

public class CableSet extends OtherEquipment {
    private CableFunction function;
    private int quantity;

    public CableFunction getFunction() {
        return function;
    }

    public void setFunction(CableFunction function) {
        this.function = function;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof CableSet requested)) return false;

        return isFunctionOk(requested);
    }

    private boolean isFunctionOk(CableSet requested) {
        return getFunction() == requested.getFunction();
    }

    private boolean isQuantityOk(CableSet requested) {
        return getQuantity() == requested.getQuantity();
    }
}
