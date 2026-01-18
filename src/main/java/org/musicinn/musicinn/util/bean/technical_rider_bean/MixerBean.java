package org.musicinn.musicinn.util.bean.technical_rider_bean;

public class MixerBean {
    private boolean isFOH;
    private int inputChannels;
    private int auxSends;
    private Boolean digital;
    private Boolean hasPhantomPower;

    public MixerBean(int inputChannels, int auxSends, Boolean digital, Boolean hasPhantomPower) {
        this.inputChannels = inputChannels;
        this.auxSends = auxSends;
        this.digital = digital;
        this.hasPhantomPower = hasPhantomPower;
    }

    public MixerBean(int inputChannels, int auxSends, Boolean digital, Boolean hasPhantomPower, boolean foh) {
        this.inputChannels = inputChannels;
        this.auxSends = auxSends;
        this.digital = digital;
        this.hasPhantomPower = hasPhantomPower;
        this.isFOH = foh;
    }

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
}
