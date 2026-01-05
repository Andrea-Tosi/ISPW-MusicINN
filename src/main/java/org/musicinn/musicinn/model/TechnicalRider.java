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

    /**
     * Verifica se almeno un elemento del rider richiede la Phantom Power.
     */
    public boolean requiresPhantom() {
        return inputs.stream().anyMatch(InputEquipment::requiresPhantomPower);
    }

}
