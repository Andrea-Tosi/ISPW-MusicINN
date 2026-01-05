package org.musicinn.musicinn.model;

public class MicrophoneSet extends InputEquipment {
    private int quantity;
    private Boolean needsPhantomPower; // true = richiede Phantom, false = non richiede Phantom Power, null = indifferente TODO null selezionabile solo da Artisti, non da Gestori

    @Override
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean isPhantomPowerNeeded() {
        return needsPhantomPower;
    }

    public void setNeedsPhantomPower(Boolean needsPhantomPower) {
        this.needsPhantomPower = needsPhantomPower;
    }

    @Override
    public boolean requiresPhantomPower() {
        // Se è null, lo considera false (non obbligatorio)
        return isPhantomPowerNeeded() != null  &&  isPhantomPowerNeeded();
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof MicrophoneSet requested)) return false;

        return isTypeOk(requested);
    }

    private boolean isTypeOk (MicrophoneSet requested) {
        if (requested.isPhantomPowerNeeded() != null) {
            return isPhantomPowerNeeded() == requested.isPhantomPowerNeeded();
        } else {
            return true;
        }
    }
}
// a runtime distingueremo set di microfoni senza e con phantom power