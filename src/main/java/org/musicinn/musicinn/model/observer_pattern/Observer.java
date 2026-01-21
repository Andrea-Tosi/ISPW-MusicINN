package org.musicinn.musicinn.model.observer_pattern;

import org.musicinn.musicinn.util.enumerations.AnnouncementState;

public interface Observer {
    void update(AnnouncementState announcementState);
    int getId();
}
