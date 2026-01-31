package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.ManagerDAO;

import java.sql.SQLException;

public class ManagerDAOMemory implements ManagerDAO {
    @Override
    public void create(Manager manager) {
        try {
            DAOFactory.getUserDAO().insertBaseUser(manager, null);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
