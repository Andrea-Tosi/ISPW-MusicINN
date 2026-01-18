package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.util.dao.interfaces.ManagerDAO;

public class ManagerDAOMemory implements ManagerDAO {
    @Override
    public void create(Manager manager) {
        System.out.println("gestore " + manager.getUsername() + " creato");
    }
}
