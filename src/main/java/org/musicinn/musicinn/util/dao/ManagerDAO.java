package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.model.Manager;

public class ManagerDAO {
    public void create(Manager manager) {
        System.out.println("gestore " + manager.getUsername() + " creato");
    }
}
