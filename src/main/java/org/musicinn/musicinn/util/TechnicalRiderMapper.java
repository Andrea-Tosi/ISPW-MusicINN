package org.musicinn.musicinn.util;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;

import java.util.ArrayList;
import java.util.List;

public class TechnicalRiderMapper {
    private TechnicalRiderMapper() {}

    // --- DA DOMINIO A BEAN (Per la visualizzazione nella GUI) ---

    public static TechnicalRiderBean toBean(TechnicalRider rider) {
        TechnicalRiderBean bean = new TechnicalRiderBean();
        bean.setMinLengthStage(rider.getMinLengthStage());
        bean.setMinWidthStage(rider.getMinWidthStage());

        // Mapping Mixer e StageBox (Logica specifica per tipo di rider)
        if (rider instanceof ArtistRider ar) {
            if (ar.getFohMixer() != null) bean.getMixers().add(mapMixerToBean(ar.getFohMixer()));
            if (ar.getStageMixer() != null) bean.getMixers().add(mapMixerToBean(ar.getStageMixer()));
            if (ar.getStageBox() != null) bean.getStageBoxes().add(mapStageBoxToBean(ar.getStageBox()));
        } else if (rider instanceof ManagerRider mr) {
            mr.getMixers().forEach(m -> bean.getMixers().add(mapMixerToBean(m)));
            mr.getStageBoxes().forEach(sb -> bean.getStageBoxes().add(mapStageBoxToBean(sb)));
        }

        // Mapping Equipaggiamento Comune
        mapEquipmentsToBean(rider, bean);

        return bean;
    }

    private static void mapEquipmentsToBean(TechnicalRider rider, TechnicalRiderBean bean) {
        for (InputEquipment input : rider.getInputs()) {
            if (input instanceof MicrophoneSet ms) bean.getMics().add(new MicrophoneSetBean(ms.getQuantity(), ms.getNeedsPhantomPower()));
            if (input instanceof DIBoxSet di) bean.getDiBoxes().add(new DIBoxSetBean(di.getQuantity(), di.getActive()));
        }
        for (OutputEquipment output : rider.getOutputs()) {
            if (output instanceof MonitorSet ms) bean.getMonitors().add(new MonitorSetBean(ms.getQuantity(), ms.getPowered()));
        }
        for (OtherEquipment other : rider.getOthers()) {
            if (other instanceof MicStandSet mss) bean.getMicStands().add(new MicStandSetBean(mss.getQuantity(), mss.getTall()));
            if (other instanceof CableSet cs) bean.getCables().add(new CableSetBean(cs.getQuantity(), cs.getPurpose()));
        }
    }

    public static MixerBean mapMixerToBean(Mixer m) {
        return new MixerBean(m.getInputChannels(), m.getAuxSends(), m.getDigital(), m.getHasPhantomPower(), m.isFOH());
    }

    public static StageBoxBean mapStageBoxToBean(StageBox sb) {
        return new StageBoxBean(sb.getInputChannels(), sb.getDigital());
    }

    // --- DA BEAN A DOMINIO (Per il salvataggio nel DB) ---

    public static List<InputEquipment> mapInputsToEntity(List<MicrophoneSetBean> mics, List<DIBoxSetBean> diBoxes) {
        List<InputEquipment> inputs = new ArrayList<>();
        if (mics != null) mics.forEach(b -> inputs.add(new MicrophoneSet(b.getQuantity(), b.getNeedsPhantomPower())));
        if (diBoxes != null) diBoxes.forEach(b -> inputs.add(new DIBoxSet(b.getQuantity(), b.getActive())));
        return inputs;
    }

    public static List<OutputEquipment> mapOutputsToEntity(List<MonitorSetBean> monitors) {
        List<OutputEquipment> outputs = new ArrayList<>();
        if (monitors != null) monitors.forEach(b -> outputs.add(new MonitorSet(b.getQuantity(), b.getPowered())));
        return outputs;
    }

    public static List<OtherEquipment> mapOthersToEntity(List<MicStandSetBean> stands, List<CableSetBean> cables) {
        List<OtherEquipment> others = new ArrayList<>();
        if (stands != null) stands.forEach(b -> others.add(new MicStandSet(b.getQuantity(), b.getTall())));
        if (cables != null) cables.forEach(b -> others.add(new CableSet(b.getQuantity(), b.getPurpose())));
        return others;
    }

    public static Mixer mapMixerBeanToEntity(MixerBean b) {
        Mixer m = new Mixer();
        m.setInputChannels(b.getInputChannels());
        m.setAuxSends(b.getAuxSends());
        m.setDigital(b.getDigital());
        m.setFOH(b.isFOH());
        m.setHasPhantomPower(b.getHasPhantomPower());
        return m;
    }
}
