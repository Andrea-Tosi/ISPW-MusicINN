package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.model.observer_pattern.Observer;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.AnnouncementBean;
import org.musicinn.musicinn.util.bean.ApplicationBean;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.AnnouncementDAO;
import org.musicinn.musicinn.util.dao.interfaces.ApplicationDAO;
import org.musicinn.musicinn.util.dao.interfaces.ArtistDAO;
import org.musicinn.musicinn.util.dao.interfaces.TechnicalRiderDAO;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.ApplicationState;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcceptApplicationController {
    List<Announcement> announcements;

    private AcceptApplicationController() {}

    private static class SingletonContainer{
        public static final AcceptApplicationController singletonInstance = new AcceptApplicationController();
    }

    public static AcceptApplicationController getSingletonInstance() {
        return AcceptApplicationController.SingletonContainer.singletonInstance;
    }

    public List<AnnouncementBean> getAllManagerAnnouncements() throws PersistenceException {
        // 1. Recuperiamo l'utente corrente dalla sessione (deve essere un MANAGER)
        String currentManager = Session.getSingletonInstance().getUser().getUsername();

        // 2. Otteniamo l'istanza del DAO tramite la Factory
        AnnouncementDAO announcementDAO = DAOFactory.getAnnouncementDAO();

        // 3. Chiamiamo il metodo del DAO
        List<Announcement> announcementList = announcementDAO.findByManager(currentManager);
        announcements = announcementList;

        // 4. Trasformiamo la lista di Entity in una lista di Bean
        List<AnnouncementBean> beans = new ArrayList<>();
        for (Announcement ann : announcementList) {
            beans.add(convertEntityToBean(ann));
        }

        return beans;
    }

    private AnnouncementBean convertEntityToBean(Announcement entity) {
        AnnouncementBean bean = new AnnouncementBean();

        // Copia dei dati base
        bean.setId(entity.getId());
        bean.setStartingDate(entity.getStartEventDay());
        bean.setStartingTime(entity.getStartEventTime());
        bean.setDuration(entity.getDuration());
        bean.setCachet(entity.getCachet());
        bean.setDeposit(entity.getDeposit());
        bean.setDescription(entity.getDescription());

        // Copia delle liste caricate in batch
        bean.setRequestedGenres(entity.getRequestedGenres());
        bean.setRequestedTypesArtist(entity.getRequestedTypesArtist());

        bean.setNumOfApplications(entity.getNumOfApplications());

        return bean;
    }

    public List<ApplicationBean> getApplicationsForAnnouncement(AnnouncementBean annBean) throws PersistenceException {
        ApplicationDAO appDAO = DAOFactory.getApplicationDAO();
        ArtistDAO artistDAO = DAOFactory.getArtistDAO();
        TechnicalRiderDAO riderDAO = DAOFactory.getTechnicalRiderDAO();

        // Chiedo al DAO le applicazioni e i relativi username (mappa temporanea)
        Map<Application, String> appToUserMap = appDAO.findByAnnouncementId(annBean.getId());

        Announcement announcement = findAnnouncementById(annBean.getId());

        // Costruisco i Bean finali unendo i pezzi
        List<ApplicationBean> beans = new ArrayList<>();
        for (Map.Entry<Application, String> entry : appToUserMap.entrySet()) {
            Application app = entry.getKey();
            String username = entry.getValue();
            if (announcement != null) {
                announcement.addObserver(app);
            }
            Artist artist = artistDAO.read(username);
            ArtistRider rider = (ArtistRider) riderDAO.read(username, Session.UserRole.ARTIST);

            ApplicationBean bean = convertToBean(app, artist);
            bean.setRiderBean(toBean(rider));
            beans.add(bean);
        }

        // Ordinamento finale
        beans.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        return beans;
    }

    private Announcement findAnnouncementById(int id) {
        return announcements.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private ApplicationBean convertToBean(Application app, Artist artist) {
        ApplicationBean bean = new ApplicationBean();
        bean.setId(app.getId());
        bean.setArtistStageName(artist.getStageName());
        bean.setArtistGenres(artist.getGenresList());
        bean.setRequestedSoundcheck(app.getSoundcheckTime());
        bean.setMatchGenresPercentage(app.getScore());
        bean.setTotalScore(app.getScore());
        return bean;
    } //TODO da aggiungere differenza tra TotalScore e MatchGenresPercentage e tutti gli altri attributi di ApplicationBean (dopo aver popolato il database)

    private TechnicalRiderBean toBean(ArtistRider rider) {
        TechnicalRiderBean bean = new TechnicalRiderBean();
        bean.setMinLengthStage(rider.getMinLengthStage());
        bean.setMinWidthStage(rider.getMinWidthStage());

        List<MixerBean> mixers = new ArrayList<>();
        List<StageBoxBean> sbs = new ArrayList<>();
        if (rider.getFohMixer() != null) mixers.add(mapToMixerBean(rider.getFohMixer()));
        if (rider.getStageMixer() != null) mixers.add(mapToMixerBean(rider.getStageMixer()));
        if (rider.getStageBox() != null) sbs.add(mapToStageBoxBean(rider.getStageBox()));

        List<MicrophoneSetBean> mics = mapToMicrophoneBeans(rider.getInputs());
        List<DIBoxSetBean> diBoxes = mapToDIBoxBeans(rider.getInputs());
        List<MonitorSetBean> monitors = mapToMonitorBeans(rider.getOutputs());
        List<MicStandSetBean> stands = mapToMicStandBeans(rider.getOthers());
        List<CableSetBean> cables = mapToCableBeans(rider.getOthers());

        bean.setMixers(mixers);
        bean.setStageBoxes(sbs);
        bean.setMics(mics);
        bean.setDiBoxes(diBoxes);
        bean.setMonitors(monitors);
        bean.setMicStands(stands);
        bean.setCables(cables);

        return bean;
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
                .map(c -> new CableSetBean(c.getQuantity(), c.getPurpose()))
                .toList();
    }

    public void chooseApplication(AnnouncementBean announcementBean, ApplicationBean applicationBean) throws PersistenceException {
        Announcement ann = findAnnouncementById(announcementBean.getId());
        Application app = (Application) findApplicationById(ann.getApplicationList(), applicationBean.getId());

        app.setState(ApplicationState.ACCEPTED);
        ann.setState(AnnouncementState.CLOSED);

        AnnouncementDAO announcementDAO = DAOFactory.getAnnouncementDAO();
        announcementDAO.updateAnnouncementState(ann);

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        for (Observer observer : ann.getApplicationList()) {
            applicationDAO.updateApplicationState((Application) observer);
        }
    }

    private Observer findApplicationById(List<Observer> applications, int id) {
        return applications.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
