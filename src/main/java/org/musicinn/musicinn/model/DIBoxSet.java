package org.musicinn.musicinn.model;

public class DIBoxSet extends InputEquipment {
    private int quantity;
    private Boolean isActive; // true = richiede Phantom, false = non richiede Phantom Power, null = indifferente

    public DIBoxSet(int quantity, Boolean active) {
        super();
        this.quantity = quantity;
        this.isActive = active;
    }
    // Le DI attive offrono più qualità ma richiedono la Phantom Power (48V)

    @Override
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

    @Override
    public boolean requiresPhantomPower() {
        // Se è null, lo considera false (non obbligatorio)
        return getActive() != null  &&  getActive();
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof DIBoxSet requested)) return false;

        return isTypeOk(requested);
    }

    private boolean isTypeOk (DIBoxSet requested) {
        if (requested.getActive() != null) {
            return getActive().equals(requested.getActive());
        } else {
            return true;
        }
    }
}
