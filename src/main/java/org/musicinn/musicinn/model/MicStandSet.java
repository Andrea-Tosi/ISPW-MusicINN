package org.musicinn.musicinn.model;

public class MicStandSet extends OtherEquipment {
    private Boolean isTall; // true = Alta (per voce) false = Bassa (per strumenti)
    private int quantity;

    public MicStandSet(int quantity, Boolean tall) {
        super();
        this.quantity = quantity;
        this.isTall = tall;
    }

    public Boolean getTall() {
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
        if (requested.getTall() != null) {
            return getTall().equals(requested.getTall());
        } else {
            return true;
        }
    }
}
