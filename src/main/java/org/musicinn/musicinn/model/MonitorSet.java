package org.musicinn.musicinn.model;

public class MonitorSet extends OutputEquipment {
    private Boolean isPowered; // true = Attiva, false = Passiva (richiede amplificatore esterno), null = indifferente TODO null selezionabile solo da Artisti, non da Gestori
    private int quantity;

    public MonitorSet(int quantity, Boolean powered) {
        super();
        this.quantity = quantity;
        this.isPowered = powered;
    }

    public Boolean getPowered() {
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
        if (requested.getPowered() != null) {
            return getPowered().equals(requested.getPowered());
        } else {
            return true;
        }
    }
}
//TODO eccezioni in caso di cross-checks non andati a buon fine da notificare all'utente