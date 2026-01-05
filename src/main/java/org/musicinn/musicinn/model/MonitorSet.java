package org.musicinn.musicinn.model;

public class MonitorSet extends OutputEquipment {
    private Boolean isPowered; // true = Attiva, false = Passiva (richiede amplificatore esterno), null = indifferente TODO null selezionabile solo da Artisti, non da Gestori
    private int quantity;

    public Boolean isPowered() {
        return isPowered;
    }

    public void setPowered(Boolean powered) {
        isPowered = powered;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof MonitorSet requested)) return false;

        return isPoweredOk(requested);
    }

    private boolean isPoweredOk (MonitorSet requested) {
        if (requested.isPowered() != null) {
            return isPowered() == requested.isPowered();
        } else {
            return true;
        }
    }

    private boolean isQuantityOk(MonitorSet requested) {
        return getQuantity() >= requested.getQuantity();
    }
}
//TODO eccezioni in caso di cross-checks non andati a buon fine da notificare all'utente