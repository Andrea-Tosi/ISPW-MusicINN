package org.musicinn.musicinn.model;

public class StageBox implements Equipment {
    private int inputChannels;
    private Boolean isDigital; // true = Digitale, false = Analogico, null = indifferente TODO null selezionabile solo da Artisti, non da Gestori

    public int getInputChannels() {
        return inputChannels;
    }

    public void setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
    }

    public Boolean isDigital() {
        return this.isDigital;
    }

    public void setDigital(Boolean digital) {
        isDigital = digital;
    }

    @Override
    public boolean satisfiesQuality(Equipment other) {
        if (!(other instanceof StageBox requested)) return false;

        return isHardwareOk(requested) && isDigitalOk(requested);
    }

    private boolean isHardwareOk(StageBox requested) {
        return getInputChannels() >= requested.getInputChannels();
    }

    private boolean isDigitalOk (StageBox requested) {
        if (requested.isDigital() != null) {
            return isDigital().equals(requested.isDigital());
        } else {
            return true;
        }
    }

    @Override
    public int getQuantity() {
        return 1;
    }
}
