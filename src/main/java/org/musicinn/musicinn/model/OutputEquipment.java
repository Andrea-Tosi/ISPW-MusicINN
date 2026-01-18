package org.musicinn.musicinn.model;

public abstract class OutputEquipment implements Equipment {
    protected int linesRequired;

    public int getLinesRequired() {
        return linesRequired;
    }

    public void setLinesRequired(int linesRequired) {
        this.linesRequired = linesRequired;
    }
}
//TODO supportare le linee richieste a partire dall'interfaccia grafica (modale dei monitor), per arrivare al controller e al database