package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.TechnicalRiderDAO;
import org.musicinn.musicinn.util.enumerations.CablePurpose;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TechnicalRiderDAOMemory implements TechnicalRiderDAO {
    private static final Logger LOGGER = Logger.getLogger(TechnicalRiderDAOMemory.class.getName());
    private static boolean isInitialized = false;

    // Uso del lazy loading invece che dello static initializer per evitare l'ExceptionInInitializerError
    // causato dall'inizializzazione circolare tra TechnicalRiderDAOMemory, VenueDAOMemory e UserDAOMemory
    private static synchronized void ensureDemoDataLoaded() {
        if (isInitialized) return;

        try {
            VenueDAOMemory.ensureDataLoaded();
            // 1. CARICAMENTO UTENTE ARTISTA PRINCIPALE (mario88)
            Artist artist = (Artist) DAOFactory.getUserDAO().findByIdentifier("mario88");

            if (artist != null) {
                ArtistRider artistRider = new ArtistRider();
                artistRider.setMinLengthStage(8);
                artistRider.setMinWidthStage(10);

                Mixer foh = new Mixer();
                foh.setFOH(true);
                foh.setHasPhantomPower(null);
                foh.setDigital(true);
                foh.setAuxSends(6);
                foh.setInputChannels(10);
                artistRider.setFohMixer(foh);

                Mixer stage = new Mixer();
                stage.setFOH(false);
                stage.setHasPhantomPower(null);
                stage.setDigital(true);
                stage.setAuxSends(5);
                stage.setInputChannels(8);
                artistRider.setStageMixer(stage);

                StageBox sb = new StageBox();
                sb.setInputChannels(6);
                sb.setDigital(true);
                artistRider.setStageBox(sb);

                List<InputEquipment> inputs = new ArrayList<>();
                inputs.add(new MicrophoneSet(4, null));
                inputs.add(new DIBoxSet(2, false));
                artistRider.setInputs(inputs);

                List<OutputEquipment> outputs = new ArrayList<>();
                outputs.add(new MonitorSet(3, false));
                outputs.add(new MonitorSet(1, null));
                artistRider.setOutputs(outputs);

                List<OtherEquipment> others = new ArrayList<>();
                others.add(new MicStandSet(2, true));
                others.add(new MicStandSet(3, false));
                others.add(new CableSet(6, CablePurpose.XLR_XLR));
                artistRider.setOthers(others);

                // Assegnazione ad Artista 1
                artist.setRider(artistRider);

                // ASSEGNAZIONE AGLI ALTRI ARTISTI (art1, art5, art6, art7)
                String[] otherArtists = {"art1", "art5", "art6", "art7"};
                for (String username : otherArtists) {
                    Artist other = (Artist) DAOFactory.getUserDAO().findByIdentifier(username);
                    if (other != null) other.setRider(artistRider);
                }
            }

            // 2. SETUP DEL MANAGER (the_rock_club)
            Manager manager = (Manager) DAOFactory.getUserDAO().findByIdentifier("the_rock_club");

            // Controllo difensivo per evitare la NullPointerException rilevata nello stacktrace
            if (manager != null && manager.getActiveVenue() != null) {
                ManagerRider managerRider = new ManagerRider();
                managerRider.setMinWidthStage(12);
                managerRider.setMinLengthStage(15);

                List<Mixer> mixers = new ArrayList<>();
                Mixer mixer1 = new Mixer();
                mixer1.setInputChannels(24);
                mixer1.setAuxSends(12);
                mixer1.setDigital(true);
                mixer1.setHasPhantomPower(true);
                mixers.add(mixer1);

                Mixer mixer2 = new Mixer();
                mixer2.setInputChannels(24);
                mixer2.setAuxSends(12);
                mixer2.setDigital(true);
                mixer2.setHasPhantomPower(true);
                mixers.add(mixer2);

                Mixer mixer3 = new Mixer();
                mixer3.setInputChannels(24);
                mixer3.setAuxSends(16);
                mixer3.setDigital(true);
                mixer3.setHasPhantomPower(true);
                mixers.add(mixer3);
                managerRider.setMixers(mixers);

                List<StageBox> stageBoxes = new ArrayList<>();
                StageBox stageBox1 = new StageBox();
                stageBox1.setInputChannels(24);
                stageBox1.setDigital(true);
                stageBoxes.add(stageBox1);

                StageBox stageBox2 = new StageBox();
                stageBox2.setInputChannels(24);
                stageBox2.setDigital(false);
                stageBoxes.add(stageBox2);
                managerRider.setStageBoxes(stageBoxes);

                List<InputEquipment> inputEquipments = new ArrayList<>();
                inputEquipments.add(new MicrophoneSet(11, false));
                inputEquipments.add(new MicrophoneSet(10, false));
                inputEquipments.add(new DIBoxSet(12, true));
                inputEquipments.add(new DIBoxSet(8, false));
                managerRider.setInputs(inputEquipments);

                List<OutputEquipment> outputEquipments = new ArrayList<>();
                outputEquipments.add(new MonitorSet(8, true));
                outputEquipments.add(new MonitorSet(6, false));
                managerRider.setOutputs(outputEquipments);

                List<OtherEquipment> otherEquipments = new ArrayList<>();
                otherEquipments.add(new MicStandSet(8, true));
                otherEquipments.add(new MicStandSet(7, false));
                otherEquipments.add(new CableSet(9, CablePurpose.XLR_XLR));
                managerRider.setOthers(otherEquipments);

                manager.getActiveVenue().setRider(managerRider);
            }

            isInitialized = true;
        } catch (PersistenceException e) {
            // Log dell'errore senza interrompere il caricamento della classe
            LOGGER.log(Level.INFO, "[DEMO] Impossibile caricare alcuni dati tecnici!", e);
        }
    }

    @Override
    public void create(TechnicalRider rider) {
        ensureDemoDataLoaded();
        User currentUser = Session.getSingletonInstance().getUser();
        if (currentUser instanceof Artist artist) {
            artist.setRider((ArtistRider) rider);
        } else if (currentUser instanceof Manager manager) {
            manager.getActiveVenue().setRider((ManagerRider) rider);
        }
    }

    @Override
    public TechnicalRider read(String username, Session.UserRole role) throws PersistenceException {
        // Fondamentale: carichiamo i dati all'atto della lettura, non all'avvio dell'app
        ensureDemoDataLoaded();

        User currentUser = DAOFactory.getUserDAO().findByIdentifier(username);
        if (role.equals(Session.UserRole.ARTIST)) {
            return ((Artist) currentUser).getRider();
        } else if (role.equals(Session.UserRole.MANAGER)) {
            return ((Manager) currentUser).getActiveVenue().getRider();
        }
        return null;
    }

    @Override
    public void updateStageDimensions(int length, int width) throws PersistenceException {
        TechnicalRider rider = read(Session.getSingletonInstance().getUser().getUsername(), Session.getSingletonInstance().getRole());
        if (rider != null) {
            rider.setMinLengthStage(length);
            rider.setMinWidthStage(width);
        }
    }
}
