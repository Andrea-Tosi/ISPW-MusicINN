package org.musicinn.musicinn.util.bean.technical_rider_bean;

import java.util.List;

public class TechnicalRiderBean {
    private int minLengthStage;
    private int minWidthStage;
    private List<MixerBean> mixers;
    private List<StageBoxBean> stageBoxes;
    private List<MicrophoneSetBean> mics;
    private List<DIBoxSetBean> diBoxes;
    private List<MonitorSetBean> monitors;
    private List<MicStandSetBean> micStands;
    private List<CableSetBean> cables;

    public int getMinLengthStage() {
        return minLengthStage;
    }

    public void setMinLengthStage(int minLengthStage) {
        this.minLengthStage = minLengthStage;
    }

    public int getMinWidthStage() {
        return minWidthStage;
    }

    public void setMinWidthStage(int minWidthStage) {
        this.minWidthStage = minWidthStage;
    }

    public List<MixerBean> getMixers() {
        return mixers;
    }

    public void setMixers(List<MixerBean> mixers) {
        this.mixers = mixers;
    }

    public List<StageBoxBean> getStageBoxes() {
        return stageBoxes;
    }

    public void setStageBoxes(List<StageBoxBean> stageBoxes) {
        this.stageBoxes = stageBoxes;
    }

    public List<MicrophoneSetBean> getMics() {
        return mics;
    }

    public void setMics(List<MicrophoneSetBean> mics) {
        this.mics = mics;
    }

    public List<DIBoxSetBean> getDiBoxes() {
        return diBoxes;
    }

    public void setDiBoxes(List<DIBoxSetBean> diBoxes) {
        this.diBoxes = diBoxes;
    }

    public List<MonitorSetBean> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<MonitorSetBean> monitors) {
        this.monitors = monitors;
    }

    public List<MicStandSetBean> getMicStands() {
        return micStands;
    }

    public void setMicStands(List<MicStandSetBean> micStands) {
        this.micStands = micStands;
    }

    public List<CableSetBean> getCables() {
        return cables;
    }

    public void setCables(List<CableSetBean> cables) {
        this.cables = cables;
    }
}
