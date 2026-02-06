package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentController;
import org.musicinn.musicinn.controller.controller_application.payment_controller.PaymentServiceFactory;
import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.model.Manager;
import org.musicinn.musicinn.model.User;
import org.musicinn.musicinn.model.Venue;
import org.musicinn.musicinn.util.EmailVerifier;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.ArtistDAO;
import org.musicinn.musicinn.util.dao.interfaces.UserDAO;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.bean.login_bean.ArtistRegistrationBean;
import org.musicinn.musicinn.util.bean.login_bean.CredentialsBean;
import org.musicinn.musicinn.util.bean.login_bean.ManagerRegistrationBean;
import org.musicinn.musicinn.util.exceptions.*;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    public User login(CredentialsBean credentialsBean) throws PersistenceException {
        User user;

        if (credentialsBean.getUsername() != null) {
            user = isIdentifierOccupied(credentialsBean.getUsername());
        } else {
            user = isIdentifierOccupied(credentialsBean.getEmail());
        }
        if(user == null){
            LOGGER.fine("username non trovato.");
            return null;
        } else {
            if (Objects.equals(credentialsBean.getPassword(), user.getHashedPassword())) {
                if (user instanceof Artist) {
                    Session.getSingletonInstance().setRole(Session.UserRole.ARTIST);
                } else if(user instanceof Manager) {
                    Session.getSingletonInstance().setRole(Session.UserRole.MANAGER);
                }
                Session.getSingletonInstance().setUser(user);
                return user;
            } else {
                LOGGER.log(Level.FINE, "password relativa a {0} errata", user.getUsername());
                return null;
            }
        }
    }

    private User isIdentifierOccupied(String identifier) throws PersistenceException {
        UserDAO userDAO = DAOFactory.getUserDAO();
        return userDAO.findByIdentifier(identifier);
    }

    public void startSignup(CredentialsBean credentialsBean) throws UsernameAlreadyUsedException, EmailAlreadyUsedException, PersistenceException {
        User userUsername = isIdentifierOccupied(credentialsBean.getUsername());
        User userEmail = isIdentifierOccupied(credentialsBean.getEmail());
        if (userUsername != null) {
            throw new UsernameAlreadyUsedException();
        }
        if (userEmail != null) {
            throw new EmailAlreadyUsedException();
        }

        EmailVerifier.getSingletonInstance().sendCode(credentialsBean.getEmail());

        //TODO: hashing password

        // Usiamo una classe anonima per istanziare User (che è abstract)
        User placeholderUser = new User(credentialsBean.getUsername(), credentialsBean.getEmail(), credentialsBean.getPassword()) {};
        Session.getSingletonInstance().setUser(placeholderUser);
    }

    public boolean checkEnteredCode(String email, String code) {
        return EmailVerifier.getSingletonInstance().checkEnteredCode(email, code);
    }

    public void invalidateVerificationCode(String email) {
        EmailVerifier.getSingletonInstance().invalidateVerificationCode(email);
    }

    public void completeSignup(ArtistRegistrationBean arb) throws PaymentServiceException {
        User temp = Session.getSingletonInstance().getUser();
        Artist artist = new Artist(temp.getUsername(), temp.getEmail(), temp.getHashedPassword());
        artist.setStageName(arb.getStageName());
        artist.setTypeArtist(arb.getTypeArtist());
        artist.setDoesUnreleased(arb.getDoesUnreleased());
        artist.setCity(arb.getCity());
        artist.setAddress(arb.getAddress());
        artist.setGenresList(arb.getGenresList());

        PaymentController pc = PaymentServiceFactory.getPaymentController();
        artist.setPaymentServiceAccountId(pc.createPaymentAccount(artist.getEmail()));

        ArtistDAO artistDAO = DAOFactory.getArtistDAO();
        artistDAO.create(artist);

        Session.getSingletonInstance().setUser(artist);
        Session.getSingletonInstance().setRole(Session.UserRole.ARTIST);
    }

    public void completeSignup(ManagerRegistrationBean mrb) throws PaymentServiceException {
        User temp = Session.getSingletonInstance().getUser();
        Manager manager = new Manager(temp.getUsername(), temp.getEmail(), temp.getHashedPassword());
        Venue venue = new Venue(mrb.getNameVenue(), mrb.getCityVenue(), mrb.getAddressVenue(), mrb.getTypeVenue());
        manager.getVenueList().add(venue);
        manager.setActiveVenue(venue);

        PaymentController pc = PaymentServiceFactory.getPaymentController();
        manager.setPaymentServiceAccountId(pc.createPaymentAccount(manager.getEmail()));

        // salva sia i dati del locale, sia quelli del manager
        VenueDAO venueDAO = DAOFactory.getVenueDAO();
        venueDAO.create(venue, manager);

        Session.getSingletonInstance().setUser(manager);
        Session.getSingletonInstance().setRole(Session.UserRole.MANAGER);
    }
}
