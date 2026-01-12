package org.musicinn.musicinn.util.dao;

import java.time.LocalDateTime;

public class ApplicationDAO {
    public void save(LocalDateTime localDateTime) {
        System.out.println("candidatura che ha come data di soundcheck '" + localDateTime + "' salvata");
    }
}
