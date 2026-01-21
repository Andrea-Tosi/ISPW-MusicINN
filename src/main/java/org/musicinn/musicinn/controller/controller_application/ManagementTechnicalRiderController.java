package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.exceptions.DatabaseException;
import org.musicinn.musicinn.util.exceptions.NotConsistentRiderException;

import java.util.ArrayList;
import java.util.List;

public class ManagementTechnicalRiderController {
    public void saveRiderData(List<MixerBean> mixers, List<StageBoxBean> stageBoxes,
                              List<MicrophoneSetBean> mics, List<DIBoxSetBean> diBoxes,
                              List<MonitorSetBean> monitors, List<MicStandSetBean> stands,
                              List<CableSetBean> cables) throws DatabaseException, NotConsistentRiderException {
        // 1. Mapping dell'equipaggiamento comune
        List<InputEquipment> inputs = mapInputs(mics, diBoxes);
        List<OutputEquipment> outputs = mapOutputs(monitors);
        List<OtherEquipment> others = mapOthers(stands, cables);

        // 2. Creazione del Rider specifico in base al ruolo
        Session.UserRole role = Session.getSingletonInstance().getRole();
        TechnicalRider rider = createRiderByRole(role, mixers, stageBoxes);

        // 3. Completamento
        rider.setInputs(inputs);
        rider.setOutputs(outputs);
        rider.setOthers(others);

        // 4. Controllo coerenza nel caso di ArtistRider
        if (rider instanceof ArtistRider ar) {
            ValidationResult report = ar.validate();
            if (!report.isValid()) throw new NotConsistentRiderException(report.toString());
        }

        // 5. Persistenza
        DAOFactory.getTechnicalRiderDAO().create(rider);
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

    public void loadRiderData(TechnicalRiderBean trBean) throws DatabaseException {
        Session.UserRole role = Session.getSingletonInstance().getRole();
        TechnicalRider rider = DAOFactory.getTechnicalRiderDAO().read(Session.getSingletonInstance().getUsername(), role);

        if (rider == null) return;

        // Mapping inverso: da Domain a Bean
        List<MixerBean> mixers = new ArrayList<>();
        List<StageBoxBean> sbs = new ArrayList<>();

        int width = rider.getMinWidthStage();
        int length = rider.getMinLengthStage();

        if (rider instanceof ArtistRider ar) {
            if (ar.getFohMixer() != null) mixers.add(mapToMixerBean(ar.getFohMixer()));
            if (ar.getStageMixer() != null) mixers.add(mapToMixerBean(ar.getStageMixer()));
            if (ar.getStageBox() != null) sbs.add(mapToStageBoxBean(ar.getStageBox()));
        } else if (rider instanceof ManagerRider mr) {
            if (mr.getMixers() != null) {
                for (Mixer m : mr.getMixers()) mixers.add(mapToMixerBean(m));
            }
            if (mr.getStageBoxes() != null) {
                for (StageBox sb : mr.getStageBoxes()) sbs.add(mapToStageBoxBean(sb));
            }
        }

        // Mapping equipaggiamento comune
        List<MicrophoneSetBean> mics = mapToMicrophoneBeans(rider.getInputs());
        List<DIBoxSetBean> diBoxes = mapToDIBoxBeans(rider.getInputs());
        List<MonitorSetBean> monitors = mapToMonitorBeans(rider.getOutputs());
        List<MicStandSetBean> stands = mapToMicStandBeans(rider.getOthers());
        List<CableSetBean> cables = mapToCableBeans(rider.getOthers());

        // Invio tutto alla GUI modificando il bean che essa aveva creato
        trBean.setMinWidthStage(width);
        trBean.setMinLengthStage(length);
        trBean.setMixers(mixers);
        trBean.setStageBoxes(sbs);
        trBean.setMics(mics);
        trBean.setDiBoxes(diBoxes);
        trBean.setMonitors(monitors);
        trBean.setMicStands(stands);
        trBean.setCables(cables);
    }

    // Helper privati per il mapping Domain -> Bean
    private MixerBean mapToMixerBean(Mixer m) {
        return new MixerBean(m.getInputChannels(), m.getAuxSends(), m.getDigital(), m.getHasPhantomPower(), m.isFOH());
    }

    private StageBoxBean mapToStageBoxBean(StageBox sb) {
        return new StageBoxBean(sb.getInputChannels(), sb.getDigital());
    }

    private List<MicrophoneSetBean> mapToMicrophoneBeans(List<InputEquipment> inputs) {
        return inputs.stream()
                .filter(MicrophoneSet.class::isInstance)
                .map(i -> (MicrophoneSet) i)
                .map(m -> new MicrophoneSetBean(m.getQuantity(), m.getNeedsPhantomPower()))
                .toList();
    }

    private List<DIBoxSetBean> mapToDIBoxBeans(List<InputEquipment> inputs) {
        return inputs.stream()
                .filter(DIBoxSet.class::isInstance)
                .map(i -> (DIBoxSet) i)
                .map(d -> new DIBoxSetBean(d.getQuantity(), d.getActive()))
                .toList();
    }

    private List<MonitorSetBean> mapToMonitorBeans(List<OutputEquipment> outputs) {
        return outputs.stream()
                .filter(MonitorSet.class::isInstance)
                .map(m -> (MonitorSet) m)
                .map(m -> new MonitorSetBean(m.getQuantity(), m.getPowered()))
                .toList();
    }

    private List<MicStandSetBean> mapToMicStandBeans(List<OtherEquipment> others) {
        return others.stream()
                .filter(MicStandSet.class::isInstance)
                .map(i -> (MicStandSet) i)
                .map(s -> new MicStandSetBean(s.getQuantity(), s.getTall()))
                .toList();
    }

    private List<CableSetBean> mapToCableBeans(List<OtherEquipment> others) {
        return others.stream()
                .filter(CableSet.class::isInstance)
                .map(i -> (CableSet) i)
                .map(c -> new CableSetBean(c.getQuantity(), c.getFunction()))
                .toList();
    }
}
