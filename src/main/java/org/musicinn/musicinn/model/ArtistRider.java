package org.musicinn.musicinn.model;

public class ArtistRider extends TechnicalRider {
    private Mixer fohMixer; // Mixer principale, si occupa di ciò che sente il pubblico e (se non c'è uno stage mixer) di ciò che sentono i musicisti
    private Mixer stageMixer; // Mixer che si occupa solo di ciò che sentono i musicisti
    private StageBox stageBox; // Strumento usato per clonare un segnale (necessario quando si vogliono sia mixer FOH sia mixer di palco)

    public ArtistRider(Mixer foh, Mixer stage, StageBox stageBox) {
        this.fohMixer = foh;
        this.stageMixer = stage;
        this.stageBox = stageBox;
    }

    protected Mixer getFohMixer() {
        return this.fohMixer;
    }

    public void setFohMixer(Mixer fohMixer) {
        this.fohMixer = fohMixer;
    }

    public Mixer getStageMixer() {
        return stageMixer;
    }

    public void setStageMixer(Mixer stageMixer) {
        this.stageMixer = stageMixer;
    }

    public StageBox getStageBox() {
        return stageBox;
    }

    public void setStageBox(StageBox stageBox) {
        this.stageBox = stageBox;
    }

    /**
     * Verifica se il rider dell'artista è logicamente coerente.
     * (Controlla se i mixer richiesti possono gestire il resto della strumentazione)
     */
    public ValidationResult validate() {
        ValidationResult report = new ValidationResult();

        Mixer foh = getFohMixer();
        Mixer stage = getStageMixer();
        StageBox sb = getStageBox();

        // 1. Caso base: artista autonomo
        if (foh == null && stage == null) {
            report.setValid(true);
            return report;
        }

        // 2. Calcolo requisiti minimi (Metodi helper già esistenti)
        int inputs = getTotalInputsNeeded();
        int outputs = getTotalOutputsNeeded();
        boolean phantom = requiresPhantom();

        // 3. Esecuzione dei controlli granulari
        validateFohMixer(report, foh, stage, inputs, outputs, phantom);
        validateStageMixer(report, stage, inputs, outputs);
        validateStageBox(report, sb, inputs);
        validateSystemCoherence(report, foh, stage, sb);

        report.setValid(report.isEmpty());
        return report;
    }

    private void validateFohMixer(ValidationResult report, Mixer foh, Mixer stage, int inputs, int outputs, boolean phantom) {
        if (foh == null) return;

        if (foh.getInputChannels() < inputs) {
            report.addError(String.format("Il mixer FOH ha solo %d canali, ne servono %d", foh.getInputChannels(), inputs));
        }

        if (phantom && !Boolean.TRUE.equals(foh.getHasPhantomPower())) {
            report.addError("Il mixer FOH non supporta la Phantom Power necessaria.");
        }

        // Se il mixer FOH è l'unico, deve gestire anche le uscite (spie)
        if (stage == null && foh.getAuxSends() < outputs) {
            report.addError(String.format("Il mixer FOH (unico) ha solo %d mandate aux, ne servono %d per le spie.", foh.getAuxSends(), outputs));
        }
    }

    private void validateStageMixer(ValidationResult report, Mixer stage, int inputs, int outputs) {
        if (stage == null) return;

        if (stage.getInputChannels() < inputs) {
            report.addError(String.format("Il mixer di Stage ha solo %d canali, ne servono %d", stage.getInputChannels(), inputs));
        }

        if (stage.getAuxSends() < outputs) {
            report.addError(String.format("Il mixer di Stage ha solo %d mandate per %d mix ausiliari.", stage.getAuxSends(), outputs));
        }
    }

    private void validateStageBox(ValidationResult report, StageBox sb, int inputs) {
        if (sb != null && sb.getInputChannels() < inputs) {
            report.addError(String.format("La Stage Box ha solo %d canali, ne servono %d", sb.getInputChannels(), inputs));
        }
    }

    private void validateSystemCoherence(ValidationResult report, Mixer foh, Mixer stage, StageBox sb) {
        // Caso specifico: doppio mixer senza splitter fisico
        if (foh != null && stage != null && sb == null) {
            report.addError("Attenzione: configurazione a due mixer senza Stage Box/Splitter rilevata.");
        }
    }
}
//TODO modellare eccezioni da lanciare in caso di mancata validazione del rider