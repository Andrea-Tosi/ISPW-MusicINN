package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.EmailVerifier;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.ArtistDAO;
import org.musicinn.musicinn.util.dao.ManagerDAO;
import org.musicinn.musicinn.util.dao.UserDAO;
import org.musicinn.musicinn.util.dao.VenueDAO;
import org.musicinn.musicinn.util.bean.login_bean.ArtistRegistrationBean;
import org.musicinn.musicinn.util.bean.login_bean.CredentialsBean;
import org.musicinn.musicinn.util.bean.login_bean.ManagerRegistrationBean;

import java.util.Objects;

public class LoginController {
    private CredentialsBean cb;

    private LoginController() {}

    private static class SingletonContainer{
        public static final LoginController singletonInstance = new LoginController();
    }

    public static LoginController getSingletonInstance() {
        return SingletonContainer.singletonInstance;
    }

    public User login(CredentialsBean credentialsBean){
        User user;

        if (credentialsBean.getUsername() != null) {
            user = isIdentifierOccupied(credentialsBean.getUsername());
        } else {
            user = isIdentifierOccupied(credentialsBean.getEmail());
        }
        if(user == null){
            System.out.println("username non trovato");
            return null;
        } else {
            if (Objects.equals(credentialsBean.getPassword(), user.getHashedPassword())) {
                if (user instanceof Artist) {
                    Session.getSingletonInstance().setRole(Session.UserRole.ARTIST);
                } else if(user instanceof Manager) {
                    Session.getSingletonInstance().setRole(Session.UserRole.MANAGER);
                }
                Session.getSingletonInstance().setUsername(credentialsBean.getUsername());
                return user;
            } else {
                System.out.println("password relativa a " + user.getUsername() + " errata");
                return null;
            }
        }
    }

    private User isIdentifierOccupied(String identifier) {
        UserDAO userDAO = new UserDAO();
        return userDAO.findByIdentifier(identifier);
    }

    public void startSignup(CredentialsBean credentialsBean) {
        User userUsername = isIdentifierOccupied(credentialsBean.getUsername());
        User userEmail = isIdentifierOccupied(credentialsBean.getEmail());
        if (userUsername != null) {
            //TODO: lancia l'eccezione (ancora da modellare) usernameAlreadyUsed
        }
        if (userEmail != null) {
            //TODO: lancia l'eccezione (ancora da modellare) emailAlreadyUsed
        }

        EmailVerifier.getSingletonInstance().sendCode(credentialsBean.getEmail());

        //TODO: hashing password
        cb = credentialsBean;
    }

    public Boolean checkEnteredCode(String email, String code) {
        return EmailVerifier.getSingletonInstance().checkEnteredCode(email, code);
    }

    public void invalidateVerificationCode(String email) {
        EmailVerifier.getSingletonInstance().invalidateVerificationCode(email);
    }

    public void completeSignup(ArtistRegistrationBean arb) {
        Artist artist = new Artist(cb.getUsername(), cb.getEmail(), cb.getPassword(), arb.getStageName(), arb.getTypeArtist(), arb.getDoesUnreleased(), arb.getCity(), arb.getAddress());
        artist.setGenresList(arb.getGenresList());

        ArtistDAO artistDAO = new ArtistDAO();
        artistDAO.create(artist);

        Session.getSingletonInstance().setUsername(cb.getUsername());
        Session.getSingletonInstance().setRole(Session.UserRole.ARTIST);
    }

    public void completeSignup(ManagerRegistrationBean mrb) {
        Manager manager = new Manager(cb.getUsername(), cb.getEmail(), cb.getPassword());
        Venue venue = new Venue(mrb.getNameVenue(), mrb.getCityVenue(), mrb.getAddressVenue(), mrb.getTypeVenue());
        manager.getVenueList().add(venue);
        manager.setActiveVenue(venue);

        ManagerDAO managerDAO = new ManagerDAO();
        managerDAO.create(manager);
        VenueDAO venueDAO = new VenueDAO();
        venueDAO.create(venue);

        Session.getSingletonInstance().setUsername(cb.getUsername());
        Session.getSingletonInstance().setRole(Session.UserRole.MANAGER);
    }
}
