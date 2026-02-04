package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.ManagementTechnicalRiderController;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.enumerations.CablePurpose;
import org.musicinn.musicinn.util.exceptions.NotConsistentRiderException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageTechnicalRiderCLI {
    private static final Logger LOGGER = Logger.getLogger(ManageTechnicalRiderCLI.class.getName());
    private final Scanner scanner;
    private final ManagementTechnicalRiderController controller = new ManagementTechnicalRiderController();

    // Liste temporanee per accumulare i Bean prima del salvataggio
    private List<MixerBean> mixers = new ArrayList<>();
    private List<StageBoxBean> stageBoxes = new ArrayList<>();
    private List<MicrophoneSetBean> mics = new ArrayList<>();
    private List<DIBoxSetBean> diBoxes = new ArrayList<>();
    private List<MonitorSetBean> monitors = new ArrayList<>();
    private List<MicStandSetBean> stands = new ArrayList<>();
    private List<CableSetBean> cables = new ArrayList<>();

    public ManageTechnicalRiderCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        LOGGER.info("\n*** GESTIONE RIDER TECNICO ***");
        loadExistingRider();

        boolean exitView = false;
        while (!exitView) {
            displayCurrentRiderState();

            LOGGER.info("\n--- MENU AZIONI ---");
            LOGGER.info("1. Aggiungi Mixer");
            LOGGER.info("2. Aggiungi Stage Box");
            LOGGER.info("3. Aggiungi Microfoni");
            LOGGER.info("4. Aggiungi DI Box");
            LOGGER.info("5. Aggiungi Monitor (Spie)");
            LOGGER.info("6. Aggiungi Aste/Cavi");
            LOGGER.info("7. SALVA MODIFICHE");
            LOGGER.info("8. Pulisci tutto e ricomincia");
            LOGGER.info("9. Esci (senza salvare)");
            LOGGER.info("Scelta: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addMixer();
                case "2" -> addStageBox();
                case "3" -> addMics();
                case "4" -> addDIBoxes();
                case "5" -> addMonitors();
                case "6" -> addOthers();
                case "7" -> { if (saveRider()) exitView = true; }
                case "8" -> clearLists();
                case "9" -> exitView = true;
                default -> LOGGER.info("Scelta non valida.");
            }
        }
        backToHome();
    }

    private void loadExistingRider() {
        try {
            TechnicalRiderBean current = controller.loadRiderData();
            if (current != null) {
                this.mixers = new ArrayList<>(current.getMixers());
                this.stageBoxes = new ArrayList<>(current.getStageBoxes());
                this.mics = new ArrayList<>(current.getMics());
                this.diBoxes = new ArrayList<>(current.getDiBoxes());
                this.monitors = new ArrayList<>(current.getMonitors());
                this.stands = new ArrayList<>(current.getMicStands());
                this.cables = new ArrayList<>(current.getCables());
            }
        } catch (PersistenceException e) {
            LOGGER.info("Nessun rider esistente trovato o errore database.");
        } catch (Throwable t) { // CATTURA ANCHE GLI ERRORI DI SISTEMA
            LOGGER.log(Level.SEVERE, "!!! CRASH RILEVATO !!!", t);
        }
    }

    private void displayCurrentRiderState() {
        LOGGER.info("\n--- STATO ATTUALE DEL RIDER (Non ancora salvato) ---");
        // Impacchettiamo temporaneamente in un Bean per usare il Formatter esistente
        TechnicalRiderBean tempBean = new TechnicalRiderBean();
        tempBean.setMixers(mixers);
        tempBean.setStageBoxes(stageBoxes);
        tempBean.setMics(mics);
        tempBean.setDiBoxes(diBoxes);
        tempBean.setMonitors(monitors);
        tempBean.setMicStands(stands);
        tempBean.setCables(cables);

        LOGGER.info(TechnicalRiderFormatter.format(tempBean, Session.getSingletonInstance().getRole()));
    }

    // --- METODI PER L'AGGIUNTA DI COMPONENTI (CREAZIONE BEAN) ---

    private void addMixer() {
        LOGGER.info("Canali Input: ");
        int ch = Integer.parseInt(scanner.nextLine());
        LOGGER.info("Mandate AUX: ");
        int aux = Integer.parseInt(scanner.nextLine());
        LOGGER.info("Digitale? (s/n): ");
        boolean dig = scanner.nextLine().equalsIgnoreCase("s");
        LOGGER.info("Supporta Phantom Power? (s/n): ");
        boolean ph = scanner.nextLine().equalsIgnoreCase("s");

        MixerBean bean = new MixerBean(ch, aux, dig, ph);

        if (Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST) {
            LOGGER.info("È il mixer principale (FOH)? (s/n): ");
            bean.setFOH(scanner.nextLine().equalsIgnoreCase("s"));
        }
        mixers.add(bean);
    }

    private void addStageBox() {
        LOGGER.info("Canali Input: ");
        int ch = Integer.parseInt(scanner.nextLine());
        LOGGER.info("Digitale? (s/n): ");
        boolean dig = scanner.nextLine().equalsIgnoreCase("s");
        stageBoxes.add(new StageBoxBean(ch, dig));
    }

    private void addMics() {
        LOGGER.info("Quantità microfoni: ");
        int q = Integer.parseInt(scanner.nextLine());
        LOGGER.info("Richiedono Phantom Power? (s/n): ");
        boolean ph = scanner.nextLine().equalsIgnoreCase("s");
        mics.add(new MicrophoneSetBean(q, ph));
    }

    private void addDIBoxes() {
        LOGGER.info("Quantità DI Box: ");
        int q = Integer.parseInt(scanner.nextLine());
        LOGGER.info("Attive? (Richiedono Phantom) (s/n): ");
        boolean act = scanner.nextLine().equalsIgnoreCase("s");
        diBoxes.add(new DIBoxSetBean(q, act));
    }

    private void addMonitors() {
        LOGGER.info("Quantità Monitor: ");
        int q = Integer.parseInt(scanner.nextLine());
        LOGGER.info("Amplificati (Powered)? (s/n): ");
        boolean p = scanner.nextLine().equalsIgnoreCase("s");
        monitors.add(new MonitorSetBean(q, p));
    }

    private void addOthers() {
        LOGGER.info("1. Aggiungi Asta | 2. Aggiungi Cavo");
        String sub = scanner.nextLine();
        if (sub.equals("1")) {
            LOGGER.info("Quantità: ");
            int q = Integer.parseInt(scanner.nextLine());
            LOGGER.info("Altezza (s = Alta, n = Bassa): ");
            boolean t = scanner.nextLine().equalsIgnoreCase("s");
            stands.add(new MicStandSetBean(q, t));
        } else if (sub.equals("2")) {
            LOGGER.info("Quantità: ");
            int q = Integer.parseInt(scanner.nextLine());
            LOGGER.info("Scopo (XLR_XLR, JACK_JACK, POWER_STRIP): ");
            CablePurpose cp = CablePurpose.valueOf(scanner.nextLine().toUpperCase());
            cables.add(new CableSetBean(q, cp));
        }
    }

    private boolean saveRider() {
        try {
            // Invochiamo il controller passando tutte le liste di Bean accumulate
            controller.saveRiderData(mixers, stageBoxes, mics, diBoxes, monitors, stands, cables);
            LOGGER.info("RIDER SALVATO CON SUCCESSO!");
            return true;
        } catch (NotConsistentRiderException e) {
            LOGGER.log(Level.INFO, "\nERRORE DI COERENZA: {0}", e.getMessage());
            LOGGER.info("Controlla che i mixer aggiunti possano gestire gli input richiesti.");
            return false;
        } catch (PersistenceException e) {
            LOGGER.log(Level.SEVERE, "Errore database: {0}", e.getMessage());
            return false;
        } catch (Throwable t) { // CATTURA ANCHE GLI ERRORI DI SISTEMA
            LOGGER.log(Level.SEVERE, "!!! CRASH RILEVATO !!!", t);
            return false;
        }
    }

    private void clearLists() {
        mixers.clear();
        stageBoxes.clear();
        mics.clear();
        diBoxes.clear();
        monitors.clear();
        stands.clear();
        cables.clear();
        LOGGER.info("Rider azzerato (solo in memoria locale).");
    }

    private void backToHome() {
        if (Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST) {
            Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.ARTIST_HOME);
        } else {
            Session.getSingletonInstance().setCurrentCLIView(Session.CLIView.MANAGER_HOME);
        }
    }
}
