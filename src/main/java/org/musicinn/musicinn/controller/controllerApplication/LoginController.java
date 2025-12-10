package org.musicinn.musicinn.controller.controllerApplication;

import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.util.DAO.UserDAO;
import org.musicinn.musicinn.util.LoginBean.UserLoginBean;

import java.util.Objects;

public class LoginController {
    public User login(UserLoginBean userLoginBean){
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByIdentifier(userLoginBean.getIdentifier());
        if(user == null){
            System.out.println("username non trovato");
            return null;
        } else {
            if (Objects.equals(userLoginBean.getPassword(), user.getHashedPassword())) {
                return user;
            } else {
                System.out.println("password relativa a " + user.getUsername() + " errata");
                return null;
            }
        }
    }

}
