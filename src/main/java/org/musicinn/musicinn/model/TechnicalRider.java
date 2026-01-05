package org.musicinn.musicinn.model;

import java.util.List;

public abstract class TechnicalRider {
    protected int minLengthStage;
    protected int minWidthStage;
    protected List<InputEquipment> inputs;
    protected List<OutputEquipment> outputs;
    protected List<OtherEquipment> others;
//    protected List<Equipment> equipments;

    public int getMinLengthStage() {
        return minLengthStage;
    }

    public void setMinLengthStage(int minLengthStage) {
        this.minLengthStage = minLengthStage;
    }

    public int getMinWidthStage() {
        return minWidthStage;
    }

    public void setMinWidthStage(int minWidthStage) {
        this.minWidthStage = minWidthStage;
    }

//    public List<Equipment> getEquipments() {
//        return equipments;
//    }
//
//    public void setEquipments(List<Equipment> equipments) {
//        this.equipments = equipments;
//    }
//
//    public void addEquipment(Equipment equipment) {
//        if (getEquipments() == null) {
//            setEquipments(new ArrayList<>());
//        }
//        getEquipments().add(equipment);
//    }

    public List<InputEquipment> getInputs() {
        return inputs;
    }

    public void setInputs(List<InputEquipment> inputs) {
        this.inputs = inputs;
    }

    public List<OutputEquipment> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputEquipment> outputs) {
        this.outputs = outputs;
    }

    public List<OtherEquipment> getOthers() {
        return others;
    }

    public void setOthers(List<OtherEquipment> others) {
        this.others = others;
    }

    //    protected Mixer getFOHMixer() {
//        for(Equipment equipment : getEquipments()) {
//            if (equipment instanceof Mixer mixer) {
//                if (mixer.isFOH()) return mixer;
//            }
//        }
//        return null;
//    }
//
//    protected Mixer getStageMixer() {
//        for(Equipment equipment : getEquipments()) {
//            if (equipment instanceof Mixer mixer) {
//                if (!mixer.isFOH()) return mixer;
//            }
//        }
//        return null;
//    }

    /**
     * Calcola il totale dei canali di ingresso necessari sul mixer.
     * Somma la quantità di ogni set di microfoni o DI Box.
     */
    public int getTotalInputsNeeded() {
        return inputs.stream()
                .mapToInt(InputEquipment::getQuantity)
                .sum();
    }

    /**
     * Calcola il totale delle linee (AUX/Bus) necessarie sul mixer.
     * Somma le linee richieste (non la quantità fisica dei monitor).
     */
    public int getTotalOutputsNeeded() {
        return outputs.stream()
                .mapToInt(OutputEquipment::getLinesRequired)
                .sum();
    }

//    protected int countAvailableMonitors(boolean artistWantsPowered) {
//        return equipments.stream()
//                .filter(e -> e instanceof MonitorSet)
//                .map(e -> (MonitorSet) e)
//                .filter(m -> {
//                    if (artistWantsPowered) {
//                        // Se l'artista li vuole attivi, il locale DEVE averli attivi
//                        return m.isPowered();
//                    } else {
//                        // Se l'artista li vuole passivi, al locale vanno bene entrambi (attivi o passivi)
//                        return true;
//                    }
//                })
//                .mapToInt(MonitorSet::getQuantity)
//                .sum();
//    }

    /**
     * Verifica se almeno un elemento del rider richiede la Phantom Power.
     */
    public boolean requiresPhantom() {
        return inputs.stream().anyMatch(InputEquipment::requiresPhantomPower);
    }

//    /**
//     * Conta i microfoni disponibili nel locale filtrati per tipo (Phantom o no).
//     */
//    public int countMicrophones(boolean needsPhantom) {
//        return equipments.stream()
//                .filter(e -> e instanceof MicrophoneSet)
//                .map(e -> (MicrophoneSet) e)
//                .filter(m -> m.isNeedsPhantomPower() == needsPhantom)
//                .mapToInt(MicrophoneSet::getQuantity)
//                .sum();
//    }

//    public boolean contains(TechnicalRider artistRider) {
//        // --- 1. CROSS-CHECK DEI MIXER (Check A e B) ---
//        Mixer artistRiderFOHMixer = artistRider.getFOHMixer();
//        Mixer artistRiderStageMixer = artistRider.getStageMixer();
//
//        // Se l'artista chiede un mixer, il locale deve averne uno che lo "soddisfi"
//        if (artistRiderFOHMixer != null) {
//            Mixer venueFoh = getFOHMixer();
//            if (venueFoh == null || !venueFoh.satisfiesQuality(artistRiderFOHMixer)) return false;
//
//            // Check B: Il mixer del locale ha abbastanza canali per TUTTO ciò che chiede l'artista?
//            if (venueFoh.getInputChannels() < artistRider.countTotalInputsRequired()) return false;
//
//            // Check A: Il mixer del locale supporta la Phantom se serve all'artista?
//            if (artistRider.checkIfPhantomIsRequired() && !venueFoh.canProvidePhantom()) return false;
//        }
//
//        if (artistRiderStageMixer != null) {
//            Mixer venueStage = this.getStageMixer();
//            if (venueStage == null || !venueStage.satisfiesQuality(artistRiderStageMixer)) return false;
//
//            // Check B (Input e Monitor Sends per lo Stage Mixer)
//            if (venueStage.getInputChannels() < artistRider.countTotalInputsRequired()) return false;
//            if (venueStage.getAuxSends() < artistRider.countTotalMonitorsRequired()) return false;
//        }
//
//        // --- 2. CONFRONTO ANALITICO DELLE ALTRE ATTREZZATURE ---
//        for (Equipment requested : artistRider.getEquipmentList()) {
//            // I mixer li abbiamo già controllati sopra, li saltiamo nel ciclo generico
//            if (requested instanceof Mixer) continue;
//
//            // Per MicrophoneSet, MonitorSet e DIBoxSet usiamo una logica di aggregazione
//            // perché il locale potrebbe avere 10 microfoni sparsi in 3 oggetti diversi
//            if (requested instanceof MicrophoneSet) {
//                if (this.countAvailableMicrophones(((MicrophoneSet) requested).isNeedsPhantomPower()) < ((MicrophoneSet) requested).getQuantity())
//                    return false;
//            }
//            else if (requested instanceof DIBoxSet) {
//                if (this.countAvailableDIBoxes(((DIBoxSet) requested).isActive()) < ((DIBoxSet) requested).getQuantity())
//                    return false;
//            }
//            else if (requested instanceof MonitorSet) {
//                if (this.countAvailableMonitors(((MonitorSet) requested).isPowered()) < ((MonitorSet) requested).getQuantity())
//                    return false;
//            }
//            else {
//                // Per Aste e Cavi usiamo il satisfies standard
//                boolean found = this.equipmentList.stream()
//                        .anyMatch(venueEq -> venueEq.getClass().equals(requested.getClass()) && venueEq.satisfies(requested));
//                if (!found) return false;
//            }
//        }
//
//        return true; // Tutto confermato!
//    }

//    // Metodo per verificare se il locale ha un mixer che soddisfa la band
//    public boolean hasCompatibleMixer(Mixer requestedMixer) {
//        for (Equipment available : equipments) {
//            if (available instanceof Mixer availableMixer) {
//                // Se ALMENO UN mixer del locale passa il test, abbiamo un match!
//                if (availableMixer.satisfies(requestedMixer)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
