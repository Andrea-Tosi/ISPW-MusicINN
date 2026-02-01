package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderMapper;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.exceptions.NotConsistentRiderException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;

public class ManagementTechnicalRiderController {
    public void saveRiderData(List<MixerBean> mixers, List<StageBoxBean> stageBoxes,
                              List<MicrophoneSetBean> mics, List<DIBoxSetBean> diBoxes,
                              List<MonitorSetBean> monitors, List<MicStandSetBean> stands,
                              List<CableSetBean> cables) throws PersistenceException, NotConsistentRiderException {
        // 1. Mapping dell'equipaggiamento comune
        List<InputEquipment> inputs = TechnicalRiderMapper.mapInputsToEntity(mics, diBoxes);
        List<OutputEquipment> outputs = TechnicalRiderMapper.mapOutputsToEntity(monitors);
        List<OtherEquipment> others = TechnicalRiderMapper.mapOthersToEntity(stands, cables);

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

    public TechnicalRiderBean loadRiderData() throws PersistenceException {
        Session.UserRole role = Session.getSingletonInstance().getRole();
        TechnicalRider rider = DAOFactory.getTechnicalRiderDAO().read(Session.getSingletonInstance().getUser().getUsername(), role);

        if (rider == null) return null;

        return TechnicalRiderMapper.toBean(rider);
    }
}
