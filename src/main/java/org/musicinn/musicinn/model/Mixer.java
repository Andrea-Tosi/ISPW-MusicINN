package org.musicinn.musicinn.model;

public class Mixer implements Equipment {
    private boolean isFOH; // true = FOH (gestisce audio per pubblico), false = Stage (gestisce audio per musicisti)
    private int inputChannels;
    private int auxSends;
    private Boolean digital; // true = Digitale, false = Analogico, null = indifferente TODO null selezionabile solo da Artisti, non da Gestori
    private Boolean hasPhantomPower; // null = indifferente TODO null selezionabile solo da Artisti, non da Gestori

    public boolean isFOH() {
        return isFOH;
    }

    public void setFOH(boolean foh) {
        isFOH = foh;
    }

    public int getInputChannels() {
        return inputChannels;
    }

    public void setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
    }

    public int getAuxSends() {
        return auxSends;
    }

    public void setAuxSends(int auxSends) {
        this.auxSends = auxSends;
    }

    public Boolean getDigital() {
        return digital;
    }

    public void setDigital(Boolean digital) {
        this.digital = digital;
    }

    public Boolean getHasPhantomPower() {
        return hasPhantomPower;
    }

    public void setHasPhantomPower(Boolean hasPhantomPower) {
        this.hasPhantomPower = hasPhantomPower;
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof Mixer requested)) return false;

        return isHardwareOk(requested) && isDigitalOk(requested);
    }

    private boolean isHardwareOk(Mixer requested) {
        return getInputChannels() >= requested.getInputChannels() && getAuxSends() >= requested.getAuxSends();
    }

    private boolean isDigitalOk (Mixer requested) {
        if (requested.getDigital() != null) {
            return getDigital().equals(requested.getDigital());
        } else {
            return true;
        }
    }
}
