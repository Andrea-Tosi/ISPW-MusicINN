package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.EventBean;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.database.ArtistDAODatabase;
import org.musicinn.musicinn.util.dao.interfaces.*;
import org.musicinn.musicinn.util.dao.memory.AnnouncementDAOMemory;
import org.musicinn.musicinn.util.dao.memory.ApplicationDAOMemory;
import org.musicinn.musicinn.util.dao.memory.TechnicalRiderDAOMemory;
import org.musicinn.musicinn.util.dao.memory.UserDAOMemory;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplyController {
    public void getEquipmentBeans(TechnicalRiderBean trBean) throws DatabaseException {
        TechnicalRiderDAO technicalRiderDAO = DAOFactory.getTechnicalRiderDAO();
        TechnicalRider rider = technicalRiderDAO.read(Session.UserRole.ARTIST);

        trBean.setMinWidthStage(rider.getMinWidthStage());
        trBean.setMinLengthStage(rider.getMinLengthStage());

        if(rider instanceof ArtistRider artistRider) setupBeanArtistRider(artistRider, trBean);
        else if (rider instanceof ManagerRider managerRider) setupBeanManagerRider(managerRider, trBean);

        // Trasforma gli Input in Bean
        List<MicrophoneSetBean> mics = new ArrayList<>();
        List<DIBoxSetBean> diBoxes = new ArrayList<>();
        for (InputEquipment input : rider.getInputs()) {
            if (input instanceof MicrophoneSet mic) {
                MicrophoneSetBean micBean = new MicrophoneSetBean(mic.getQuantity(), mic.getNeedsPhantomPower());
                mics.add(micBean);
            } else if (input instanceof DIBoxSet di) {
                DIBoxSetBean diBean = new DIBoxSetBean(di.getQuantity(), di.getActive());
                diBoxes.add(diBean);
            }
        }
        trBean.setMics(mics);
        trBean.setDiBoxes(diBoxes);

        // Trasforma gli Output in Bean
        List<MonitorSetBean> monitors = new ArrayList<>();
        for (OutputEquipment output : rider.getOutputs()) {
            if (output instanceof MonitorSet ms) {
                MonitorSetBean mBean = new MonitorSetBean(ms.getQuantity(), ms.getPowered());
                monitors.add(mBean);
            }
        }
        trBean.setMonitors(monitors);

        // Trasforma gli Others in Bean
        List<MicStandSetBean> micStands = new ArrayList<>();
        List<CableSetBean> cables = new ArrayList<>();
        for (OtherEquipment other : rider.getOthers()) {
            if (other instanceof MicStandSet mss) {
                MicStandSetBean msBean = new MicStandSetBean(mss.getQuantity(), mss.getTall());
                micStands.add(msBean);
            } else if (other instanceof CableSet cs) {
                CableSetBean cBean = new CableSetBean(cs.getQuantity(), cs.getFunction());
                cables.add(cBean);
            }
        }
        trBean.setMicStands(micStands);
        trBean.setCables(cables);
    }

    private void setupBeanArtistRider(ArtistRider rider, TechnicalRiderBean trBean) {
        Mixer foh = rider.getFohMixer();
        List<MixerBean> mixers = new ArrayList<>();
        if (foh != null) {
            MixerBean mbFoh = new MixerBean(foh.getInputChannels(), foh.getAuxSends(), foh.getDigital(), foh.getHasPhantomPower());
            mixers.add(mbFoh);
        }
        Mixer stage = rider.getStageMixer();
        if (stage != null) {
            MixerBean mbStage = new MixerBean(stage.getInputChannels(), stage.getAuxSends(), stage.getDigital(), stage.getHasPhantomPower());
            mixers.add(mbStage);
        }
        trBean.setMixers(mixers);

        StageBox sb = rider.getStageBox();
        List<StageBoxBean> stageBoxes = new ArrayList<>();
        if (sb != null) {
            StageBoxBean sbBean = new StageBoxBean(sb.getInputChannels(), sb.getDigital());
            stageBoxes.add(sbBean);
        }
        trBean.setStageBoxes(stageBoxes);
    }

    private void setupBeanManagerRider(ManagerRider rider, TechnicalRiderBean trBean) {
        List<MixerBean> mixers = new ArrayList<>();
        for (Mixer m : rider.getMixers()) {
            mixers.add(new MixerBean(m.getInputChannels(), m.getAuxSends(), m.getDigital(), m.getHasPhantomPower()));
        }
        trBean.setMixers(mixers);

        List<StageBoxBean> stageBoxes = new ArrayList<>();
        for (StageBox sb : rider.getStageBoxes()) {
            stageBoxes.add(new StageBoxBean(sb.getInputChannels(), sb.getDigital()));
        }
        trBean.setStageBoxes(stageBoxes);
    }



    private final AnnouncementDAO announcementDAO = DAOFactory.getAnnouncementDAO();
    private final TechnicalRiderDAO riderDAO = DAOFactory.getTechnicalRiderDAO();
    // Supponiamo di avere un servizio per la distanza
//    private final DistanceService distanceService = new DistanceService();

    public List<EventBean> getCompatibleEvents(int page) throws DatabaseException {
        // 1. Recupero Artista e il suo Rider dalla Sessione
        String currentUserId = Session.getSingletonInstance().getUsername();
        UserDAO userDAO = DAOFactory.getUserDAO();
        Artist currentUser = (Artist) userDAO.findByIdentifier(currentUserId);
        // Carichiamo l'Entity completa del rider dell'artista per fare i confronti
        ArtistRider artistRider = (ArtistRider) riderDAO.read(Session.UserRole.ARTIST);

        // 2. FILTRO SQL (Strategia A): Annunci OPEN + Generi compatibili
        // Il DAO restituisce Announcement che hanno già al loro interno l'oggetto Venue
        if (Session.getSingletonInstance().getPersistenceType().equals(Session.PersistenceType.DATABASE)) {
            ArtistDAODatabase artistDAO = new ArtistDAODatabase();
            currentUser.setGenresList(artistDAO.loadArtistGenres(Session.getSingletonInstance().getUsername()));
        }
        List<Announcement> announcements = announcementDAO.findActiveByGenres(
                currentUser.getGenresList(),
                page,
                10
        );

        List<EventBean> results = new ArrayList<>();

        for (Announcement ann : announcements) {
            // 3. NAVIGAZIONE: Otteniamo il locale e il suo rider tecnico
            Venue venue = ann.getVenue();
            ManagerRider venueRider = venue.getRider();

            // 4. ASSEMBLAGGIO EVENTBEAN
            EventBean bean = new EventBean();

            // Dati del Locale
            bean.setVenueName(venue.getName());
            bean.setVenueAddress(venue.getAddress());
            bean.setVenueCity(venue.getCity());
            bean.setTypeVenue(venue.getTypeVenue());

            // Calcolo distanza (Logica di Business)
            //int dist = distanceService.calculate(currentUser.getAddress(), venue.getAddress());
            bean.setDistance(10); //TODO introdurre servizio calcolo distanza

            // Incapsulamento Bean Interni
            bean.setAnnouncementBean(createAnnouncementBean(ann));
            bean.setTechnicalRiderBean(createRiderBean(venueRider));
            ValidationResult vr = venueRider.contains(artistRider);
            bean.setReport(vr);

            results.add(bean);
        }
        return results;
    }

    private AnnouncementBean createAnnouncementBean(Announcement ann) {
        // Trasferisce i dati da Entity Announcement a AnnouncementBean
        AnnouncementBean bean = new AnnouncementBean();
        bean.setId(ann.getId());
        bean.setStartingDate(ann.getStartEventDay());
        bean.setStartingTime(ann.getStartEventTime());
        bean.setDuration(ann.getDuration());
        bean.setCachet(ann.getCachet());
        bean.setDeposit(ann.getDeposit());
        bean.setRequestedGenres(ann.getRequestedGenres());
        bean.setRequestedTypesArtist(ann.getRequestedTypesArtist());
        bean.setDoesUnreleased(ann.getDoesUnreleased());
        bean.setDescription(ann.getDescription());
        return bean;
    }

    private TechnicalRiderBean createRiderBean(ManagerRider rider) {
        // Crea un riepilogo testuale o strutturato dei requisiti del locale
        TechnicalRiderBean bean = new TechnicalRiderBean();
        bean.setMinLengthStage(rider.getMinLengthStage());
        bean.setMinWidthStage(rider.getMinWidthStage());
        bean.setMixers(mapMixersToBean(rider));
        bean.setStageBoxes(mapStageBoxesToBean(rider));

        List<MicrophoneSetBean> microphoneSetBeans = new ArrayList<>();
        List<DIBoxSetBean> diBoxSetBeans = new ArrayList<>();
        for (InputEquipment input : rider.getInputs()) {
            if (input instanceof MicrophoneSet ms) microphoneSetBeans.add(mapMicrophonesToBean(ms));
            if (input instanceof DIBoxSet di) diBoxSetBeans.add(mapDIBoxesToBean(di));
        }
        bean.setMics(microphoneSetBeans);
        bean.setDiBoxes(diBoxSetBeans);

        List<MonitorSetBean> monitorSetBeans = new ArrayList<>();
        for (OutputEquipment output : rider.getOutputs()) {
            if (output instanceof MonitorSet ms) monitorSetBeans.add(mapMonitorsToBean(ms));
        }
        bean.setMonitors(monitorSetBeans);

        List<MicStandSetBean> micStandSetBeans = new ArrayList<>();
        List<CableSetBean> cableSetBeans = new ArrayList<>();
        for (OtherEquipment other : rider.getOthers()) {
            if (other instanceof MicStandSet mss) micStandSetBeans.add(mapMicStandsToBean(mss));
            if (other instanceof CableSet cs) cableSetBeans.add(mapCablesToBean(cs));
        }
        bean.setMicStands(micStandSetBeans);
        bean.setCables(cableSetBeans);

        return bean;
    }

    private List<MixerBean> mapMixersToBean(TechnicalRider rider) {
        List<MixerBean> beans = new ArrayList<>();

        if (rider instanceof ManagerRider managerRider) {
            // Se è un locale, ha una lista di mixer disponibili
            for (Mixer m : managerRider.getMixers()) {
                beans.add(mapSingleMixerToBean(m));
            }
        } else if (rider instanceof ArtistRider artistRider) {
            // Se fosse un artista (per altri UC), prendiamo i due mixer specifici
            if (artistRider.getFohMixer() != null) beans.add(mapSingleMixerToBean(artistRider.getFohMixer()));
            if (artistRider.getStageMixer() != null) beans.add(mapSingleMixerToBean(artistRider.getStageMixer()));
        }
        return beans;
    }

    private MixerBean mapSingleMixerToBean(Mixer m) {
        MixerBean b = new MixerBean(m.getInputChannels(), m.getAuxSends(), m.getDigital(), m.getHasPhantomPower());
        b.setFOH(m.isFOH());
        return b;
    }

    private List<StageBoxBean> mapStageBoxesToBean(TechnicalRider rider) {
        List<StageBoxBean> beans = new ArrayList<>();

        if (rider instanceof ManagerRider managerRider) {
            // Se è un locale, ha una lista di stage box disponibili
            for (StageBox sb : managerRider.getStageBoxes()) {
                beans.add(mapSingleStageBoxToBean(sb));
            }
        } else if (rider instanceof ArtistRider artistRider) {
            beans.add(mapSingleStageBoxToBean(artistRider.getStageBox()));
        }

        return beans;
    }

    private StageBoxBean mapSingleStageBoxToBean(StageBox sb) {
        return new StageBoxBean(sb.getInputChannels(), sb.getDigital());
    }

    private MicrophoneSetBean mapMicrophonesToBean(MicrophoneSet ms) {
        return new MicrophoneSetBean(ms.getQuantity(), ms.getNeedsPhantomPower());
    }

    private DIBoxSetBean mapDIBoxesToBean(DIBoxSet di) {
        return new DIBoxSetBean(di.getQuantity(), di.getActive());
    }

    private MonitorSetBean mapMonitorsToBean(MonitorSet ms) {
        return new MonitorSetBean(ms.getQuantity(), ms.getPowered());
    }

    private MicStandSetBean mapMicStandsToBean(MicStandSet mss) {
        return new MicStandSetBean(mss.getQuantity(), mss.getTall());
    }

    private CableSetBean mapCablesToBean(CableSet cs) {
        return new CableSetBean(cs.getQuantity(), cs.getFunction());
    }

    public void createApplication(EventBean eventBean) throws DatabaseException {
        Application application = new Application();
        application.setState(ApplicationState.PENDING);
        application.setSoundcheckTime(eventBean.getAnnouncementBean().getSoundcheckTime());
        List<MusicalGenre> requestedGenres = eventBean.getAnnouncementBean().getRequestedGenres();

        ArtistDAO artistDAO = DAOFactory.getArtistDAO();
        List<MusicalGenre> artistGenres = artistDAO.loadArtistGenres(Session.getSingletonInstance().getUsername());

        long inCommon = requestedGenres.stream().filter(artistGenres::contains).count();
        double genres_score = requestedGenres.isEmpty() ? 0.0 : (inCommon * 100.0) / requestedGenres.size();
        application.setScore(genres_score);

        Venue venue = new Venue();
        venue.setName(eventBean.getVenueName());
        venue.setAddress(eventBean.getVenueAddress());
        venue.setCity(eventBean.getVenueCity());
        venue.setTypeVenue(eventBean.getTypeVenue());

        Announcement announcement = new Announcement();
        announcement.setId(eventBean.getAnnouncementBean().getId());
        announcement.setStartEventDay(eventBean.getAnnouncementBean().getStartingDate());
        announcement.setStartEventTime(eventBean.getAnnouncementBean().getStartingTime());
        announcement.setVenue(venue);

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        applicationDAO.save(application, announcement);
    }
}
