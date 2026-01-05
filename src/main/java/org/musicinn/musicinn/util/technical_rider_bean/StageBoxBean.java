package org.musicinn.musicinn.util.technical_rider_bean;

public class StageBoxBean {
    private int inputChannels;
    private Boolean isDigital;

    public StageBoxBean(int inputChannels, boolean isDigital) {
        this.inputChannels = inputChannels;
        this.isDigital = isDigital;
    }

    public int getInputChannels() {
        return inputChannels;
    }

    public void setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
    }

    public Boolean getDigital() {
        return isDigital;
    }

    public void setDigital(Boolean digital) {
        isDigital = digital;
    }
}
