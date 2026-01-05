package org.musicinn.musicinn.model;

public interface Equipment {
    boolean satisfiesQuality(Equipment other);
    int getQuantity();
}
