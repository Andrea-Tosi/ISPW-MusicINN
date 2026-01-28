package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.CablePurpose;

public class CableSet extends OtherEquipment {
    private CablePurpose purpose;
    private int quantity;

    public CableSet(int quantity, CablePurpose purpose) {
        super();
        this.quantity = quantity;
        this.purpose = purpose;
    }

    public CablePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(CablePurpose purpose) {
        this.purpose = purpose;
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

        return isPurposeOk(requested);
    }

    private boolean isPurposeOk(CableSet requested) {
        return getPurpose() == requested.getPurpose();
    }
}
