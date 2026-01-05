package org.musicinn.musicinn.model;

import jdk.jfr.Frequency;

public class MicStandSet extends OtherEquipment {
    private Boolean isTall; // true = Alta (per voce) false = Bassa (per strumenti)
    private int quantity;

    public Boolean isTall() {
        return isTall;
    }

    public void setTall(Boolean tall) {
        isTall = tall;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof MicStandSet requested)) return false;

        return isHeightOk(requested);
    }

    private boolean isHeightOk(MicStandSet requested) {
        if (requested.isTall() != null) {
            return isTall() == requested.isTall();
        } else {
            return true;
        }
    }

    private boolean isQuantityOk(MicStandSet requested) {
        return getQuantity() >= requested.getQuantity();
    }
}
