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
        // Liste di Entity specifiche
        List<Mixer> mixerEntities = new ArrayList<>();
        List<StageBox> stageBoxEntities = new ArrayList<>();
        List<InputEquipment> inputEntities = new ArrayList<>(); // Classe base per Mics e DI
        List<OutputEquipment> outputEntities = new ArrayList<>();
        List<OtherEquipment> otherEntities = new ArrayList<>();

        try {
            // 1. MAPPING: Da Bean a Entity

            // Mixer
//            for (MixerBean b : mixers) {
//                Mixer m = new Mixer();
//                m.setInputChannels(b.getInputChannels());
//                m.setAuxSends(b.getAuxSends());
//                m.setDigital(b.getDigital());
//                m.setFOH(b.isFOH());
//                m.setHasPhantomPower(b.getHasPhantomPower());
//                mixerEntities.add(m);
//            }

            // Stage Box
//            for (StageBoxBean b : stageBoxes) {
//                StageBox sb = new StageBox();
//                sb.setInputChannels(b.getInputChannels());
//                sb.setDigital(b.getDigital());
//                stageBoxEntities.add(sb);
//            }

            // Input Equipment (Uso delle classi Entity separate MicrophoneSet e DIBoxSet)
            for (MicrophoneSetBean b : mics) {
                MicrophoneSet mic = new MicrophoneSet();
                mic.setQuantity(b.getQuantity());
                mic.setNeedsPhantomPower(b.getNeedsPhantomPower());
                inputEntities.add(mic); // Aggiunto alla lista polimorfica
            }

            for (DIBoxSetBean b : diBoxes) {
                DIBoxSet di = new DIBoxSet();
                di.setQuantity(b.getQuantity());
                di.setActive(b.getActive());
                inputEntities.add(di); // Aggiunto alla lista polimorfica
            }

            // Output Equipment (Monitor)
            for (MonitorSetBean b : monitors) {
                MonitorSet mon = new MonitorSet();
                mon.setQuantity(b.getQuantity());
                mon.setPowered(b.getPowered());
                outputEntities.add(mon);
            }

            // Other Equipment (Aste e Cavi)
            for (MicStandSetBean b : stands) {
                MicStandSet ms = new MicStandSet();
                ms.setQuantity(b.getQuantity());
                ms.setTall(b.getTall());
                otherEntities.add(ms);
            }

            for (CableSetBean b : cables) {
                CableSet c = new CableSet();
                c.setQuantity(b.getQuantity());
                c.setFunction(b.getFunction());
                otherEntities.add(c);
            }

            Session.UserRole role = Session.getSingletonInstance().getRole();
            TechnicalRider rider = null;
            if (role.equals(Session.UserRole.ARTIST)) {
                Mixer foh = null;
                Mixer stage = null;
                while (!mixers.isEmpty()) {
                    MixerBean bean = mixers.getFirst();
                    Mixer mixer = new Mixer();
                    Boolean bool = bean.isFOH();
                    mixer.setFOH(bool);
                    mixer.setHasPhantomPower(bean.getHasPhantomPower());
                    mixer.setDigital(bean.getDigital());
                    mixer.setAuxSends(bean.getAuxSends());
                    mixer.setInputChannels(bean.getInputChannels());
                    if (bool) foh = mixer;
                    else stage = mixer;
                    mixers.removeFirst();
                }
                StageBox stageBox = null;
                if (!stageBoxes.isEmpty()) {
                    StageBoxBean bean = stageBoxes.getFirst();
                    stageBox = new StageBox();
                    stageBox.setInputChannels(bean.getInputChannels());
                    stageBox.setDigital(bean.getDigital());
                    stageBoxes.removeFirst();
                }
                rider = new ArtistRider(foh, stage, stageBox);
            } else if (role.equals(Session.UserRole.MANAGER)) {
                for (MixerBean b : mixers) {
                    Mixer m = new Mixer();
                    m.setInputChannels(b.getInputChannels());
                    m.setAuxSends(b.getAuxSends());
                    m.setDigital(b.getDigital());
                    m.setFOH(b.isFOH());
                    m.setHasPhantomPower(b.getHasPhantomPower());
                    mixerEntities.add(m);
                }
                for (StageBoxBean b : stageBoxes) {
                    StageBox sb = new StageBox();
                    sb.setInputChannels(b.getInputChannels());
                    sb.setDigital(b.getDigital());
                    stageBoxEntities.add(sb);
                }
                rider = new ManagerRider(mixerEntities, stageBoxEntities);
            }
            rider.setInputs(inputEntities);
            rider.setOutputs(outputEntities);
            rider.setOthers(otherEntities);

            // 2. PERSISTENZA: Chiamata al DAO
            TechnicalRiderDAO dao = new TechnicalRiderDAO();
            dao.create(rider);

            // Esegue la pulizia dei vecchi equipment e l'inserimento dei nuovi in una transazione
//            dao.updateRiderEquipment(currentRiderId, mixerEntities, stageBoxEntities,
//                    inputEntities, outputEntities, otherEntities);

        } catch (Exception e) {
            // Se il mapping fallisce, il DAO non viene chiamato e il DB resta intatto
            e.printStackTrace();
            throw new RuntimeException("Errore durante la preparazione delle Entity: " + e.getMessage());
        }
    }
}
