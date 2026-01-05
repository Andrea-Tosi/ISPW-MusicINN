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

        Mixer fohMixer = getFohMixer();
        Mixer stageMixer = getStageMixer();
        StageBox stageBox = getStageBox();

        // Se l'artista non chiede mixer, è "autonomo": il rider è valido.
        if (fohMixer == null && stageMixer == null) {
            report.setValid(true);
            return report;
        }

        int inputsNeeded = getTotalInputsNeeded();
        int outputsNeeded = getTotalOutputsNeeded();
        boolean phantomNeeded = requiresPhantom();

        // Check sul Mixer FOH
        if (fohMixer != null) {
            if (fohMixer.getInputChannels() < inputsNeeded) {
                report.addError("Il mixer FOH richiesto ha solo " + fohMixer.getInputChannels() +
                        " canali, ma ne servono " + inputsNeeded);
            }
            if (phantomNeeded && !fohMixer.getHasPhantomPower()) {
                report.addError("Il mixer FOH richiesto non supporta la Phantom Power necessaria.");
            }
            if (stageMixer == null) {
                if (fohMixer.getAuxSends() < outputsNeeded) {
                    report.addError("Il mixer FOH (unico mixer) ha solo " + fohMixer.getAuxSends() + " mandate aux, ma ne servono " + outputsNeeded + " per le spie.");
                }
            }
        }

        // Check sul Mixer di Stage
        if (stageMixer != null) {
            if (stageMixer.getInputChannels() < inputsNeeded) {
                report.addError("Il mixer FOH richiesto ha solo " + stageMixer.getInputChannels() +
                        " canali, ma ne servono " + inputsNeeded);
            }
            if (stageMixer.getAuxSends() < outputsNeeded) {
                report.addError("Il mixer di Stage richiesto ha solo " + stageMixer.getAuxSends() +
                        " mandate per " + outputsNeeded + " configurazioni di mix ausiliarie.");
            }
        }

        // Check sullo Stage Box
        if (stageBox != null) {
            if (stageBox.getInputChannels() < inputsNeeded) {
                report.addError("La Stage Box selezionata ha solo " + stageBox.getInputChannels() +
                        " canali, ma ne servono " + inputsNeeded);
            }
        }

        // Se ci sono due mixer, la Stage Box è caldamente raccomandata/obbligatoria
        if (fohMixer != null && stageMixer != null && stageBox == null) {
            report.addError("Attenzione: hai selezionato due mixer ma nessuna Stage Box/Splitter.");
        }

        report.setValid(report.isEmpty());
        return report;
    }
}
//TODO modellare eccezioni da lanciare in caso di mancata validazione del rider