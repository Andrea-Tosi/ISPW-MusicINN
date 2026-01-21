package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.TechnicalRiderDAO;
import org.musicinn.musicinn.util.enumerations.CableFunction;

import java.util.ArrayList;
import java.util.List;

public class TechnicalRiderDAOMemory implements TechnicalRiderDAO {
    @Override
    public void create(TechnicalRider rider) {
        System.out.println("rider tecnico " + rider + " creato");
    }

    @Override
    public TechnicalRider read(String username, Session.UserRole role) {
        // 1. Creazione Mixer (FOH e Stage)
        TechnicalRider mockRider = role.equals(Session.UserRole.ARTIST) ? getArtistRider() : getManagerRider();

        // 4. Popolamento equipaggiamento comune
        List<InputEquipment> inputs = new ArrayList<>();
        inputs.add(new MicrophoneSet(5, true));  // 5 Mic con Phantom
        inputs.add(new DIBoxSet(2, false));      // 2 DI Passive
        mockRider.setInputs(inputs);

        List<OutputEquipment> outputs = new ArrayList<>();
        outputs.add(new MonitorSet(4, true));    // 4 Monitor attivi
        mockRider.setOutputs(outputs);

        List<OtherEquipment> others = new ArrayList<>();
        others.add(new MicStandSet(5, true));    // 5 Aste alte
        others.add(new CableSet(10, CableFunction.XLR_XLR));
        mockRider.setOthers(others);

        return mockRider;
    }

    private static ArtistRider getArtistRider() {
        Mixer fohMixer = new Mixer();
        fohMixer.setInputChannels(32);
        fohMixer.setAuxSends(8);
        fohMixer.setDigital(true);
        fohMixer.setFOH(true);
        fohMixer.setHasPhantomPower(true);

        Mixer stageMixer = new Mixer();
        stageMixer.setInputChannels(24);
        stageMixer.setAuxSends(12);
        stageMixer.setDigital(false);
        stageMixer.setFOH(false);
        stageMixer.setHasPhantomPower(true);

        // 2. Creazione Stage Box
        StageBox sb = new StageBox();
        sb.setInputChannels(16);
        sb.setDigital(null);

        // 3. Istanza del Rider (ArtistRider)
        return new ArtistRider(fohMixer, stageMixer, sb);
    }

    private static ManagerRider getManagerRider() {
        // 1. Creazione Lista Mixer (il Manager può averne N)
        List<Mixer> mixerList = new ArrayList<>();

        Mixer mainMixer = new Mixer();
        mainMixer.setInputChannels(48); // Configurazione più grande tipica da Manager/Service
        mainMixer.setAuxSends(16);
        mainMixer.setDigital(true);
        mainMixer.setFOH(true);
        mainMixer.setHasPhantomPower(true);
        mixerList.add(mainMixer);

        Mixer smallMixer = new Mixer();
        smallMixer.setInputChannels(12);
        smallMixer.setAuxSends(2);
        smallMixer.setDigital(false);
        smallMixer.setFOH(false);
        smallMixer.setHasPhantomPower(true);
        mixerList.add(smallMixer);

        // 2. Creazione Lista Stage Box (il Manager può gestirne più di una)
        List<StageBox> sbList = new ArrayList<>();

        StageBox sb1 = new StageBox();
        sb1.setInputChannels(32);
        sb1.setDigital(true);
        sbList.add(sb1);

        StageBox sb2 = new StageBox();
        sb2.setInputChannels(16);
        sb2.setDigital(false); // Magari una stage box analogica di espansione
        sbList.add(sb2);

        // 3. Istanza del Rider (ManagerRider)
        // Passiamo le liste create al costruttore del ManagerRider
        return new ManagerRider(mixerList, sbList);
    }
}
