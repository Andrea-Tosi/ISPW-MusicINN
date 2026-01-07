package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.TechnicalRiderDAO;
import org.musicinn.musicinn.util.technical_rider_bean.*;

import java.util.ArrayList;
import java.util.List;

public class ManagementTechnicalRiderController {
    public void saveRiderData(List<MixerBean> mixers, List<StageBoxBean> stageBoxes,
                              List<MicrophoneSetBean> mics, List<DIBoxSetBean> diBoxes,
                              List<MonitorSetBean> monitors, List<MicStandSetBean> stands,
                              List<CableSetBean> cables) {
        try {
            // 1. Mapping dell'equipaggiamento comune
            List<InputEquipment> inputs = mapInputs(mics, diBoxes);
            List<OutputEquipment> outputs = mapOutputs(monitors);
            List<OtherEquipment> others = mapOthers(stands, cables);

            // 2. Creazione del Rider specifico in base al ruolo
            Session.UserRole role = Session.getSingletonInstance().getRole();
            TechnicalRider rider = createRiderByRole(role, mixers, stageBoxes);

            // 3. Completamento e Persistenza
            rider.setInputs(inputs);
            rider.setOutputs(outputs);
            rider.setOthers(others);

            new TechnicalRiderDAO().create(rider);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante la preparazione delle Entity: " + e.getMessage(), e);
        }
    }

    private List<InputEquipment> mapInputs(List<MicrophoneSetBean> mics, List<DIBoxSetBean> diBoxes) {
        List<InputEquipment> inputs = new ArrayList<>();
        for (MicrophoneSetBean b : mics) {
            inputs.add(new MicrophoneSet(b.getQuantity(), b.getNeedsPhantomPower()));
        }
        for (DIBoxSetBean b : diBoxes) {
            inputs.add(new DIBoxSet(b.getQuantity(), b.getActive()));
        }
        return inputs;
    }

    private List<OutputEquipment> mapOutputs(List<MonitorSetBean> monitors) {
        List<OutputEquipment> outputs = new ArrayList<>();
        for (MonitorSetBean b : monitors) {
            outputs.add(new MonitorSet(b.getQuantity(), b.getPowered()));
        }
        return outputs;
    }

    private List<OtherEquipment> mapOthers(List<MicStandSetBean> stands, List<CableSetBean> cables) {
        List<OtherEquipment> others = new ArrayList<>();
        for (MicStandSetBean b : stands) {
            others.add(new MicStandSet(b.getQuantity(), b.getTall()));
        }
        for (CableSetBean b : cables) {
            others.add(new CableSet(b.getQuantity(), b.getFunction()));
        }
        return others;
    }

    private TechnicalRider createRiderByRole(Session.UserRole role, List<MixerBean> mixers, List<StageBoxBean> sbs) {
        if (role.equals(Session.UserRole.ARTIST)) {
            return createArtistRider(mixers, sbs);
        } else {
            return createManagerRider(mixers, sbs);
        }
    }

    private ArtistRider createArtistRider(List<MixerBean> mixers, List<StageBoxBean> sbs) {
        Mixer foh = null;
        Mixer stage = null;
        for (MixerBean b : mixers) {
            Mixer m = mapMixer(b);
            if (b.isFOH()) foh = m;
            else stage = m;
        }
        StageBox sb = sbs.isEmpty() ? null : mapStageBox(sbs.getFirst());
        return new ArtistRider(foh, stage, sb);
    }

    private ManagerRider createManagerRider(List<MixerBean> mixers, List<StageBoxBean> sbs) {
        List<Mixer> mixerList = new ArrayList<>();
        List<StageBox> sbList = new ArrayList<>();
        for (MixerBean b : mixers) mixerList.add(mapMixer(b));
        for (StageBoxBean b : sbs) sbList.add(mapStageBox(b));
        return new ManagerRider(mixerList, sbList);
    }

    private Mixer mapMixer(MixerBean b) {
        Mixer m = new Mixer();
        m.setInputChannels(b.getInputChannels());
        m.setAuxSends(b.getAuxSends());
        m.setDigital(b.getDigital());
        m.setFOH(b.isFOH());
        m.setHasPhantomPower(b.getHasPhantomPower());
        return m;
    }

    private StageBox mapStageBox(StageBoxBean b) {
        StageBox sb = new StageBox();
        sb.setInputChannels(b.getInputChannels());
        sb.setDigital(b.getDigital());
        return sb;
    }
}
