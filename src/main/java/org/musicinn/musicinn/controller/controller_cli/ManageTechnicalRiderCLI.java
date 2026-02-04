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
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageTechnicalRiderCLI {
    private static final Logger LOGGER = Logger.getLogger(ManageTechnicalRiderCLI.class.getName());
    private final Scanner scanner;
    private final ManagementTechnicalRiderController controller = new ManagementTechnicalRiderController();

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
            LOGGER.info("1. Gestisci Mixer | 2. Gestisci Stage Box | 3. Gestisci Microfoni");
            LOGGER.info("4. Gestisci DI Box | 5. Gestisci Monitor   | 6. Gestisci Aste | 7. Gestisci Cavi");
            LOGGER.info("8. SALVA MODIFICHE   | 9. Pulisci tutto | 10. Esci");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> manageMixers();
                    case "2" -> manageStageBoxes();
                    case "3" -> manageMicrophones();
                    case "4" -> manageDIBoxes();
                    case "5" -> manageMonitors();
                    case "6" -> manageMicStands();
                    case "7" -> manageCables();
                    case "8" -> { if (saveRider()) exitView = true; }
                    case "9" -> clearLists();
                    case "10" -> exitView = true;
                    default -> LOGGER.info("Scelta non valida.");
                }
            } catch (NumberFormatException e) {
                LOGGER.info("Errore: Inserisci un numero valido.");
            }
        }
        backToHome();
    }

    // --- LOGICA GESTIONE MIXER (CON VINCOLI ARTISTA) ---
    private void manageMixers() {
        boolean isArtist = Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST;
        LOGGER.info("1. Aggiungi | 2. Rimuovi");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            if (isArtist && mixers.size() >= 2) {
                LOGGER.info("L'artista può avere massimo 2 mixer.");
                return;
            }
            Boolean dig = askBoolean("Digitale?");
            Boolean ph = askBoolean("Phantom Power?");
            LOGGER.info("Canali Input: ");
            int ch = Integer.parseInt(scanner.nextLine());
            LOGGER.info("Mandate AUX: ");
            int aux = Integer.parseInt(scanner.nextLine());

            MixerBean bean = new MixerBean(ch, aux, dig, ph);
            if (isArtist) {
                boolean isFoh = mixers.isEmpty(); // Il primo è sempre FOH
                bean.setFOH(isFoh);
                LOGGER.info("Aggiunto mixer: " + (isFoh ? "FOH" : "STAGE"));
            } else {
                bean.setFOH(askBoolean("È FOH?"));
            }
            mixers.add(bean);
        } else if (op.equals("2")) {
            if (mixers.isEmpty()) return;
            if (isArtist) {
                // Rimuove Stage (index 1) se esiste, altrimenti FOH (index 0)
                int indexToRemove = (mixers.size() == 2) ? 1 : 0;
                LOGGER.info("Rimosso mixer " + (mixers.get(indexToRemove).isFOH() ? "FOH" : "STAGE"));
                mixers.remove(indexToRemove);
            } else {
                for (int i = 0; i < mixers.size(); i++) LOGGER.info(i + ". Mixer " + (mixers.get(i).isFOH() ? "FOH" : "Stage") + " (" + mixers.get(i).getInputChannels() + "ch)");
                LOGGER.info("Indice da rimuovere: ");
                int idx = Integer.parseInt(scanner.nextLine());
                if (idx >= 0 && idx < mixers.size()) mixers.remove(idx);
            }
        }
    }

    // --- LOGICA GESTIONE STAGE BOX ---
    private void manageStageBoxes() {
        boolean isArtist = Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST;
        LOGGER.info("1. Aggiungi Stage Box | 2. Rimuovi Stage Box");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            // Vincolo Artista: Massimo 1 Stage Box
            if (isArtist && !stageBoxes.isEmpty()) {
                LOGGER.info("Limite raggiunto: l'artista può avere al massimo 1 Stage Box.");
                return;
            }

            Boolean dig = askBoolean("La Stage Box deve essere digitale?");
            LOGGER.info("Canali Input: ");
            int ch = Integer.parseInt(scanner.nextLine());

            stageBoxes.add(new StageBoxBean(ch, dig));
            LOGGER.info("Stage Box aggiunta correttamente.");

        } else if (op.equals("2")) {
            if (stageBoxes.isEmpty()) {
                LOGGER.info("Nessuna Stage Box presente nel rider.");
                return;
            }

            if (isArtist) {
                // L'artista ha al massimo una Stage Box, la rimuoviamo direttamente
                stageBoxes.removeFirst();
                LOGGER.info("Stage Box rimossa dal rider.");
            } else {
                // Il Manager può averne più di una, mostriamo la lista con indici
                LOGGER.info("--- Seleziona la Stage Box da rimuovere ---");
                for (int i = 0; i < stageBoxes.size(); i++) {
                    StageBoxBean sb = stageBoxes.get(i);
                    String tipo = (sb.getDigital() == null) ? "Indifferente" : (sb.getDigital() ? "Digitale" : "Analogica");
                    LOGGER.log(Level.INFO, "{0}. Stage Box ({1} canali, {2})", new Object[]{i, sb.getInputChannels(), tipo});
                }

                LOGGER.info("Inserisci l'indice: ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine());
                    if (idx >= 0 && idx < stageBoxes.size()) {
                        stageBoxes.remove(idx);
                        LOGGER.info("Stage Box rimossa con successo.");
                    } else {
                        LOGGER.info("Indice non valido.");
                    }
                } catch (NumberFormatException e) {
                    LOGGER.info("Errore: Inserisci un numero valido per l'indice.");
                }
            }
        }
    }

    // --- LOGICA GESTIONE SET (MICROFONI, DI, MONITOR, ASTE, CAVI) ---

    private void manageMicrophones() {
        LOGGER.info("1. Aggiungi | 2. Rimuovi");
        String op = scanner.nextLine();
        Boolean ph = askBoolean("Phantom Power?");
        LOGGER.info("Quantità: ");
        int qty = Integer.parseInt(scanner.nextLine());

        if (op.equals("1")) {
            boolean found = false;
            for (MicrophoneSetBean b : mics) {
                if (Objects.equals(b.getNeedsPhantomPower(), ph)) {
                    b.setQuantity(b.getQuantity() + qty);
                    found = true; break;
                }
            }
            if (!found) mics.add(new MicrophoneSetBean(qty, ph));
        } else {
            mics.removeIf(b -> Objects.equals(b.getNeedsPhantomPower(), ph) && isQtyEmptyAfterSub(b, qty));
        }
    }

    private void manageDIBoxes() {
        LOGGER.info("1. Aggiungi | 2. Rimuovi");
        String op = scanner.nextLine();
        Boolean act = askBoolean("Attiva?");
        LOGGER.info("Quantità: ");
        int qty = Integer.parseInt(scanner.nextLine());

        if (op.equals("1")) {
            boolean found = false;
            for (DIBoxSetBean b : diBoxes) {
                if (Objects.equals(b.getActive(), act)) {
                    b.setQuantity(b.getQuantity() + qty);
                    found = true; break;
                }
            }
            if (!found) diBoxes.add(new DIBoxSetBean(qty, act));
        } else {
            diBoxes.removeIf(b -> Objects.equals(b.getActive(), act) && isQtyEmptyAfterSubDI(b, qty));
        }
    }

    private void manageMonitors() {
        LOGGER.info("1. Aggiungi | 2. Rimuovi");
        String op = scanner.nextLine();
        Boolean p = askBoolean("Amplificato?");
        LOGGER.info("Quantità: ");
        int qty = Integer.parseInt(scanner.nextLine());

        if (op.equals("1")) {
            boolean found = false;
            for (MonitorSetBean b : monitors) {
                if (Objects.equals(b.getPowered(), p)) {
                    b.setQuantity(b.getQuantity() + qty);
                    found = true; break;
                }
            }
            if (!found) monitors.add(new MonitorSetBean(qty, p));
        } else {
            monitors.removeIf(b -> Objects.equals(b.getPowered(), p) && isQtyEmptyAfterSubMon(b, qty));
        }
    }

    // --- GESTIONE ASTE (DIVISA) ---
    private void manageMicStands() {
        LOGGER.info("1. Aggiungi | 2. Rimuovi");
        String op = scanner.nextLine();
        Boolean t = askBoolean("Deve essere alta?");
        LOGGER.info("Quantità: ");
        int qty = Integer.parseInt(scanner.nextLine());

        if (op.equals("1")) {
            for (MicStandSetBean b : stands) {
                if (Objects.equals(b.getTall(), t)) {
                    b.setQuantity(b.getQuantity() + qty); return;
                }
            }
            stands.add(new MicStandSetBean(qty, t));
        } else if (op.equals("2")) {
            stands.removeIf(b -> Objects.equals(b.getTall(), t) && isQtyEmptyAfterSubStand(b, qty));
        }
    }

    // --- GESTIONE CAVI (DIVISA) ---
    private void manageCables() {
        LOGGER.info("1. Aggiungi | 2. Rimuovi");
        String op = scanner.nextLine();
        LOGGER.info("Scopo (XLR_XLR, JACK_JACK, POWER_STRIP): ");
        try {
            CablePurpose cp = CablePurpose.valueOf(scanner.nextLine().toUpperCase());
            LOGGER.info("Quantità: ");
            int qty = Integer.parseInt(scanner.nextLine());

            if (op.equals("1")) {
                for (CableSetBean b : cables) {
                    if (b.getPurpose() == cp) {
                        b.setQuantity(b.getQuantity() + qty); return;
                    }
                }
                cables.add(new CableSetBean(qty, cp));
            } else if (op.equals("2")) {
                cables.removeIf(b -> b.getPurpose() == cp && isQtyEmptyAfterSubCable(b, qty));
            }
        } catch (IllegalArgumentException e) {
            LOGGER.info("Scopo cavo non riconosciuto.");
        }
    }

    // --- HELPER PER SOTTRAZIONE (NON MODIFICA I BEAN SE NON PER IL SETTER) ---

    private boolean isQtyEmptyAfterSub(MicrophoneSetBean b, int toRemove) {
        if (b.getQuantity() <= toRemove) return true; // Segnala al removeIf di cancellare il Bean
        b.setQuantity(b.getQuantity() - toRemove);
        return false; // Segnala al removeIf di tenere il Bean aggiornato
    }

    private boolean isQtyEmptyAfterSubDI(DIBoxSetBean b, int toRemove) {
        if (b.getQuantity() <= toRemove) return true;
        b.setQuantity(b.getQuantity() - toRemove);
        return false;
    }

    private boolean isQtyEmptyAfterSubMon(MonitorSetBean b, int toRemove) {
        if (b.getQuantity() <= toRemove) return true;
        b.setQuantity(b.getQuantity() - toRemove);
        return false;
    }

    private boolean isQtyEmptyAfterSubStand(MicStandSetBean b, int toRemove) {
        if (b.getQuantity() <= toRemove) return true;
        b.setQuantity(b.getQuantity() - toRemove);
        return false;
    }

    private boolean isQtyEmptyAfterSubCable(CableSetBean b, int toRemove) {
        if (b.getQuantity() <= toRemove) return true;
        b.setQuantity(b.getQuantity() - toRemove);
        return false;
    }

    // --- METODI DI SUPPORTO STANDARD ---

    private Boolean askBoolean(String prompt) {
        boolean isArt = Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST;
        String opt = isArt ? "(s = Sì, n = No, i = Indifferente)" : "(s = Sì, n = No)";
        while (true) {
            LOGGER.info(prompt + " " + opt + ": ");
            String in = scanner.nextLine().toLowerCase().trim();
            if (in.equals("s")) return true;
            if (in.equals("n")) return false;
            if (isArt && in.equals("i")) return null;
            LOGGER.info("Input non valido.");
        }
    }

    private void displayCurrentRiderState() {
        TechnicalRiderBean b = new TechnicalRiderBean();
        b.setMixers(mixers);
        b.setStageBoxes(stageBoxes);
        b.setMics(mics);
        b.setDiBoxes(diBoxes);
        b.setMonitors(monitors);
        b.setMicStands(stands);
        b.setCables(cables);
        LOGGER.info(TechnicalRiderFormatter.format(b, Session.getSingletonInstance().getRole()));
    }

    private void loadExistingRider() {
        try {
            TechnicalRiderBean b = controller.loadRiderData();
            if (b != null) {
                mixers = new ArrayList<>(b.getMixers());
                stageBoxes = new ArrayList<>(b.getStageBoxes());
                mics = new ArrayList<>(b.getMics());
                diBoxes = new ArrayList<>(b.getDiBoxes());
                monitors = new ArrayList<>(b.getMonitors());
                stands = new ArrayList<>(b.getMicStands());
                cables = new ArrayList<>(b.getCables());
            }
        } catch (PersistenceException e) {
            LOGGER.info("Errore caricamento.");
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Errore", t);
        }
    }

    private boolean saveRider() {
        try {
            controller.saveRiderData(mixers, stageBoxes, mics, diBoxes, monitors, stands, cables);
            LOGGER.info("RIDER SALVATO!");
            return true;
        } catch (NotConsistentRiderException | PersistenceException e) {
            LOGGER.log(Level.INFO, "Salvataggio fallito: {0}", e.getMessage());
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
        LOGGER.info("Rider azzerato localmente.");
    }

    private void backToHome() {
        Session.getSingletonInstance().setCurrentCLIView(Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST ? Session.CLIView.ARTIST_HOME : Session.CLIView.MANAGER_HOME);
    }
}
