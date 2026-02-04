package org.musicinn.musicinn.controller.controller_cli;

import org.musicinn.musicinn.controller.controller_application.ManagementTechnicalRiderController;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.TechnicalRiderFormatter;
import org.musicinn.musicinn.util.bean.technical_rider_bean.*;
import org.musicinn.musicinn.util.enumerations.CablePurpose;
import org.musicinn.musicinn.util.exceptions.NotConsistentRiderException;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManageTechnicalRiderCLI {
    private static final String OP_CHOICE = "1. Aggiungi | 2. Rimuovi";
    private static final String QUANTITY = "Quantità: ";
    private static final String NOT_VALID_OPTION = "Opzione non valida.";
    private static final String PHANTOM_POWER_QUESTION = "Phantom Power?";
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
                    case "8" -> exitView = saveRider();
                    case "9" -> clearLists();
                    case "10" -> exitView = true;
                    default -> LOGGER.info("Scelta non valida.");
                }
            } catch (NumberFormatException _) {
                LOGGER.info("Errore: Inserisci un numero valido.");
            }
        }
        backToHome();
    }

    // --- LOGICA GESTIONE MIXER (CON VINCOLI ARTISTA) ---
    private void manageMixers() {
        boolean isArtist = Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST;
        LOGGER.info(OP_CHOICE);
        String op = scanner.nextLine();

        switch (op) {
            case "1" -> addMixer(isArtist);
            case "2" -> removeMixer(isArtist);
            default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addMixer(boolean isArtist) {
        if (isArtist && mixers.size() >= 2) {
            LOGGER.info("PUoi avere massimo 2 mixer.");
            return;
        }

        MixerBean bean = collectMixerData(); // Delegato il recupero dati input

        if (isArtist) {
            boolean isFoh = mixers.isEmpty();
            bean.setFOH(isFoh);
            LOGGER.log(Level.INFO, "Aggiunto mixer: {0}", isFoh ? "FOH" : "STAGE");
        } else {
            bean.setFOH(askBoolean("È FOH?").orElse(false));
        }
        mixers.add(bean);
    }

    private void removeMixer(boolean isArtist) {
        if (mixers.isEmpty()) return;

        if (isArtist) {
            // Logica specifica Artista: rimuove lo Stage se presente, altrimenti il FOH
            int indexToRemove = (mixers.size() == 2) ? 1 : 0;
            LOGGER.info("Rimosso mixer " + (mixers.get(indexToRemove).isFOH() ? "FOH" : "STAGE"));
            mixers.remove(indexToRemove);
        } else {
            removeMixerByList(); // Delegata logica di rimozione manuale
        }
    }

    private MixerBean collectMixerData() {
        Boolean dig = askBoolean("Digitale?").orElse(null);
        Boolean ph = askBoolean(PHANTOM_POWER_QUESTION).orElse(null);
        int ch = askInt("Canali Input: ");
        int aux = askInt("Mandate AUX: ");
        return new MixerBean(ch, aux, dig, ph);
    }

    private void removeMixerByList() {
        for (int i = 0; i < mixers.size(); i++) {
            MixerBean m = mixers.get(i);
            String type = m.isFOH() ? "FOH" : "Stage";
            LOGGER.log(Level.INFO, "{0}. Mixer {1} ({2} ch)", new Object[] { i, type, m.getInputChannels() });
        }

        LOGGER.info("Indice da rimuovere: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine());

            if (idx >= 0 && idx < mixers.size()) {
                mixers.remove(idx);
                LOGGER.info("Mixer rimosso con successo.");
            } else {
                LOGGER.info("Indice non valido.");
            }
        } catch (NumberFormatException _) {
            LOGGER.info("Inserimento non valido: inserire un numero.");
        }
    }

    // --- LOGICA GESTIONE STAGE BOX ---
    private void manageStageBoxes() {
        boolean isArtist = Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST;
        LOGGER.info("1. Aggiungi Stage Box | 2. Rimuovi Stage Box");
        String op = scanner.nextLine();

        switch (op) {
        case "1" -> addStageBox(isArtist);
        case "2" -> removeStageBox(isArtist);
        default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addStageBox(boolean isArtist) {
        if (isArtist && !stageBoxes.isEmpty()) {
            LOGGER.info("Limite raggiunto: puoi avere al massimo 1 Stage Box.");
            return;
        }

        Boolean dig = askBoolean("La Stage Box deve essere digitale?").orElse(null);
        int ch = askInt("Canali Input: ");

        stageBoxes.add(new StageBoxBean(ch, dig));
        LOGGER.info("Stage Box aggiunta correttamente.");
    }

    private void removeStageBox(boolean isArtist) {
        if (stageBoxes.isEmpty()) {
            LOGGER.info("Nessuna Stage Box presente nel rider.");
            return;
        }

        if (isArtist) {
            stageBoxes.removeFirst();
            LOGGER.info("Stage Box rimossa dal rider.");
        } else {
            selectAndRemoveStageBox();
        }
    }

    private void selectAndRemoveStageBox() {
        displayStageBoxes();
        int idx = askInt("Inserisci l'indice: ");

        if (idx >= 0 && idx < stageBoxes.size()) {
            stageBoxes.remove(idx);
            LOGGER.info("Stage Box rimossa con successo.");
        } else {
            LOGGER.info("Indice non valido.");
        }
    }

    private void displayStageBoxes() {
        LOGGER.info("--- Seleziona la Stage Box da rimuovere ---");
        for (int i = 0; i < stageBoxes.size(); i++) {
            StageBoxBean sb = stageBoxes.get(i);
            String type;
            if (sb.getDigital() == null) type = "Indifferente";
            else type = sb.getDigital() ? "Digitale" : "Analogica";
            LOGGER.log(Level.INFO, "{0}. Stage Box ({1} canali, {2})", new Object[]{i, sb.getInputChannels(), type});
        }
    }

    // --- LOGICA GESTIONE SET (MICROFONI, DI, MONITOR, ASTE, CAVI) ---

    private void manageMicrophones() {
        LOGGER.info(OP_CHOICE);
        String op = scanner.nextLine();

        // Spostiamo la raccolta dati dentro i rami per non chiedere dati inutili in caso di errore
        switch (op) {
            case "1" -> addMicrophones();
            case "2" -> removeMicrophones();
            default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addMicrophones() {
        Boolean ph = askBoolean(PHANTOM_POWER_QUESTION).orElse(null);
        int qty = askInt(QUANTITY);

        // Cerchiamo se esiste già un set con la stessa caratteristica
        for (MicrophoneSetBean b : mics) {
            if (Objects.equals(b.getNeedsPhantomPower(), ph)) {
                b.setQuantity(b.getQuantity() + qty);
                LOGGER.info("Quantità aggiornata.");
                return;
            }
        }
        // Se non trovato, aggiungiamo nuovo set
        mics.add(new MicrophoneSetBean(qty, ph));
        LOGGER.info("Nuovo set di microfoni aggiunto.");
    }

    private void removeMicrophones() {
        Boolean ph = askBoolean(PHANTOM_POWER_QUESTION).orElse(null);
        int qty = askInt(QUANTITY);

        // Usiamo removeIf come facevi tu, ma isolato per chiarezza
        mics.removeIf(b -> Objects.equals(b.getNeedsPhantomPower(), ph) && isQtyEmptyAfterSub(b, qty));
        LOGGER.info("Rimozione completata (se l'elemento esisteva).");
    }

    private void manageDIBoxes() {
        LOGGER.info(OP_CHOICE);
        String op = scanner.nextLine();

        switch (op) {
            case "1" -> addDIBoxes();
            case "2" -> removeDIBoxes();
            default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addDIBoxes() {
        Boolean act = askBoolean("Attiva?").orElse(null);
        int qty = askInt(QUANTITY);

        for (DIBoxSetBean b : diBoxes) {
            if (Objects.equals(b.getActive(), act)) {
                b.setQuantity(b.getQuantity() + qty);
                LOGGER.info("Quantità DI Box aggiornata.");
                return;
            }
        }
        diBoxes.add(new DIBoxSetBean(qty, act));
        LOGGER.info("Nuovo set DI Box aggiunto.");
    }

    private void removeDIBoxes() {
        Boolean act = askBoolean("Attiva?").orElse(null);
        int qty = askInt(QUANTITY);
        diBoxes.removeIf(b -> Objects.equals(b.getActive(), act) && isQtyEmptyAfterSubDI(b, qty));
    }

    private void manageMonitors() {
        LOGGER.info(OP_CHOICE);
        String op = scanner.nextLine();

        switch (op) {
            case "1" -> addMonitors();
            case "2" -> removeMonitors();
            default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addMonitors() {
        Boolean p = askBoolean("Amplificato?").orElse(null);
        int qty = askInt(QUANTITY);

        for (MonitorSetBean b : monitors) {
            if (Objects.equals(b.getPowered(), p)) {
                b.setQuantity(b.getQuantity() + qty);
                LOGGER.info("Quantità Monitor aggiornata.");
                return;
            }
        }
        monitors.add(new MonitorSetBean(qty, p));
        LOGGER.info("Nuovo set Monitor aggiunto.");
    }

    private void removeMonitors() {
        Boolean p = askBoolean("Amplificato?").orElse(null);
        int qty = askInt(QUANTITY);
        monitors.removeIf(b -> Objects.equals(b.getPowered(), p) && isQtyEmptyAfterSubMon(b, qty));
    }

    // --- GESTIONE ASTE (DIVISA) ---
    private void manageMicStands() {
        LOGGER.info(OP_CHOICE);
        String op = scanner.nextLine();

        switch (op) {
            case "1" -> addMicStands();
            case "2" -> removeMicStands();
            default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addMicStands() {
        Boolean t = askBoolean("Deve essere alta?").orElse(null);
        int qty = askInt(QUANTITY);

        for (MicStandSetBean b : stands) {
            if (Objects.equals(b.getTall(), t)) {
                b.setQuantity(b.getQuantity() + qty);
                return;
            }
        }
        stands.add(new MicStandSetBean(qty, t));
    }

    private void removeMicStands() {
        Boolean t = askBoolean("Deve essere alta?").orElse(null);
        int qty = askInt(QUANTITY);
        stands.removeIf(b -> Objects.equals(b.getTall(), t) && isQtyEmptyAfterSubStand(b, qty));
    }

    // --- GESTIONE CAVI (DIVISA) ---
    private void manageCables() {
        LOGGER.info(OP_CHOICE);
        String op = scanner.nextLine();

        switch (op) {
            case "1" -> addCables();
            case "2" -> removeCables();
            default -> LOGGER.info(NOT_VALID_OPTION);
        }
    }

    private void addCables() {
        CablePurpose cp = askCablePurpose();
        if (cp == null) return;

        int qty = askInt(QUANTITY);
        updateOrAddCable(cp, qty);
    }

    private void removeCables() {
        CablePurpose cp = askCablePurpose();
        if (cp == null) return;

        int qty = askInt(QUANTITY);
        cables.removeIf(b -> b.getPurpose() == cp && isQtyEmptyAfterSubCable(b, qty));
    }

    private void updateOrAddCable(CablePurpose cp, int qty) {
        // 1. Cerchiamo se esiste già un set di cavi con lo stesso scopo
        for (CableSetBean b : cables) {
            if (b.getPurpose() == cp) {
                b.setQuantity(b.getQuantity() + qty);
                LOGGER.log(Level.INFO, "Quantità aggiornata per i cavi {0}", cp);
                return; // Usciamo subito: obiettivo raggiunto
            }
        }

        // 2. Se il ciclo finisce senza ritorni, il set non esiste: lo aggiungiamo
        cables.add(new CableSetBean(qty, cp));
        LOGGER.log(Level.INFO, "Nuovo set di cavi aggiunto ({0}).", cp);
    }

    private CablePurpose askCablePurpose() {
        String availableOptions = Stream.of(CablePurpose.values())
                .map(Enum::toString)
                .collect(Collectors.joining(", "));

        LOGGER.log(Level.INFO, "Scopo ({0}): ", availableOptions);
        try {
            return CablePurpose.valueOf(scanner.nextLine().toUpperCase().trim());
        } catch (IllegalArgumentException _) {
            LOGGER.info("Scopo cavo non riconosciuto.");
            return null;
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

    private Optional<Boolean> askBoolean(String prompt) {
        boolean isArt = Session.getSingletonInstance().getRole() == Session.UserRole.ARTIST;
        String opt = isArt ? "(s = Sì, n = No, i = Indifferente)" : "(s = Sì, n = No)";
        while (true) {
            LOGGER.log(Level.INFO, "{0} {1}: ", new Object[] {prompt, opt});
            String in = scanner.nextLine().toLowerCase().trim();
            if (in.equals("s")) return Optional.of(true);
            if (in.equals("n")) return Optional.of(false);
            if (isArt && in.equals("i")) return Optional.empty();
            LOGGER.info("Input non valido.");
        }
    }

    private int askInt(String prompt) {
        while (true) { // Loop finché l'input non è valido
            LOGGER.info(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException _) {
                LOGGER.info("Errore: devi inserire un numero intero valido.");
            }
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
        LOGGER.log(Level.INFO, "{0}", TechnicalRiderFormatter.format(b, Session.getSingletonInstance().getRole()));
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
        } catch (PersistenceException _) {
            LOGGER.info("Errore caricamento.");
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
