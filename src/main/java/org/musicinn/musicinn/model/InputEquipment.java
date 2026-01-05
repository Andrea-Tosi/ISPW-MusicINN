package org.musicinn.musicinn.model;

public abstract class InputEquipment implements Equipment {
    public abstract int getQuantity();
    public abstract boolean requiresPhantomPower();
}
