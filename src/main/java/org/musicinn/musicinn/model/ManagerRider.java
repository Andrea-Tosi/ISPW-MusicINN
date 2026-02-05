package org.musicinn.musicinn.model;

import java.util.ArrayList;
import java.util.List;

public class ManagerRider extends TechnicalRider {
    private List<Mixer> mixers;
    private List<StageBox> stageBoxes;

    public ManagerRider(List<Mixer> mixers, List<StageBox> stageBoxes) {
        this.mixers = mixers;
        this.stageBoxes = stageBoxes;
    }

    public ManagerRider() {
        super();
        this.mixers = new ArrayList<>();
        this.stageBoxes = new ArrayList<>();
    }

    public List<Mixer> getMixers() {
        return mixers;
    }

    public void setMixers(List<Mixer> mixers) {
        this.mixers = mixers;
    }

    public List<StageBox> getStageBoxes() {
        return stageBoxes;
    }

    public void setStageBoxes(List<StageBox> stageBoxes) {
        this.stageBoxes = stageBoxes;
    }

    public ValidationResult contains(ArtistRider artistRider) {
        ValidationResult report = new ValidationResult();

        if (this.minLengthStage < artistRider.getMinLengthStage() || this.minWidthStage < artistRider.getMinWidthStage()) {
            report.addError("Il palco del locale (" + minLengthStage + "m x" + minWidthStage + "m) è più piccolo del minimo richiesto (" +
                    artistRider.getMinLengthStage() + "x" + artistRider.getMinWidthStage() + ")");
            report.setValid(false);
        }

        // Check Mixer (Logica di assegnazione univoca)
        checkMixerCompatibility(artistRider, report);

        // Check Stage Box
        checkStageBoxCompatibility(artistRider, report);

        // Check Input Equipment (Microfoni, DI)
        if (inventoryCheck(getInputs(), artistRider.getInputs())) {
            report.addError("Il locale non ha abbastanza strumenti di input che soddisfano le esigenze che hai esposto nel rider");
            report.setValid(false);
        }

        // Check Output Equipment (Monitor)
        if (inventoryCheck(getOutputs(), artistRider.getOutputs())) {
            report.addError("Il locale non ha abbastanza strumenti di output che soddisfano le esigenze che hai esposto nel rider");
            report.setValid(false);
        }

        // Check Other (Cavi, Aste)
        if (inventoryCheck(getOthers(), artistRider.getOthers())) {
            report.addError("Il locale non ha abbastanza accessori che soddisfano le esigenze che hai esposto nel rider");
            report.setValid(false);
        }

        return report;
    }

    private void checkMixerCompatibility(ArtistRider artistRider, ValidationResult report) {
        List<Mixer> tempInventory = new ArrayList<>(getMixers());

        // Trova FOH
        Mixer foh = findAndRemove(tempInventory, artistRider.getFohMixer());
        if (artistRider.getFohMixer() != null && foh == null) {
            report.addError("Il locale non ha un mixer adatto a te");
            report.setValid(false);
        }

        // Trova Stage (se richiesto)
        if (artistRider.getStageMixer() != null) {
            Mixer stage = findAndRemove(tempInventory, artistRider.getStageMixer());
            if (stage == null) {
                report.addError("Il locale non ha un secondo mixer adatto a te");
                report.setValid(false);
            }
        }
    }

    private Mixer findAndRemove(List<Mixer> inventory, Mixer requested) {
        Mixer found = inventory.stream()
                .filter(m -> (requested == null || m.satisfiesQuality(requested)))
                .findFirst().orElse(null);

        if (found != null) inventory.remove(found);
        return found;
    }

    private void checkStageBoxCompatibility(ArtistRider artistRider, ValidationResult report) {
        StageBox requested = artistRider.getStageBox();
        if (requested == null) return;

        // Trova Stage Box
        StageBox stageBox = getStageBoxes().stream()
                .filter(s -> s.satisfiesQuality(requested))
                .findFirst().orElse(null);

        if (stageBox == null) {
            report.addError("Il locale non ha una stage box adatto a te");
        }
    }

    private <T extends Equipment> boolean inventoryCheck(List<T> venueList, List<T> artistList) {
        for (T req : artistList) {
            // Sommiamo le quantità di tutti i set nel locale che sono tecnicamente compatibili
            int totalAvailable = venueList.stream()
                    .filter(venueSet -> venueSet.satisfiesQuality(req))
                    .mapToInt(T::getQuantity)
                    .sum();

            if (totalAvailable < req.getQuantity()) {
                return true; // La somma totale non basta
            }
        }
        return false;
    }
}
