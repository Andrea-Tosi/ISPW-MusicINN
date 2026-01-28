package org.musicinn.musicinn.controller.controller_application;

import com.stripe.exception.StripeException;
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
import org.musicinn.musicinn.util.exceptions.EmailAlreadyUsedException;
import org.musicinn.musicinn.util.exceptions.UsernameAlreadyUsedException;

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
                Session.getSingletonInstance().setUser(user);
                return user;
            } else {
                System.out.println("password relativa a " + user.getUsername() + " errata");
                return null;
            }
        }
    }

    private User isIdentifierOccupied(String identifier) {
        UserDAO userDAO = DAOFactory.getUserDAO();
        return userDAO.findByIdentifier(identifier);
    }

    public void startSignup(CredentialsBean credentialsBean) throws UsernameAlreadyUsedException, EmailAlreadyUsedException {
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
        cb = credentialsBean;
    }

    public Boolean checkEnteredCode(String email, String code) {
        return EmailVerifier.getSingletonInstance().checkEnteredCode(email, code);
    }

    public void invalidateVerificationCode(String email) {
        EmailVerifier.getSingletonInstance().invalidateVerificationCode(email);
    }

    public void completeSignup(ArtistRegistrationBean arb) throws StripeException {
        Artist artist = new Artist(cb.getUsername(), cb.getEmail(), cb.getPassword(), arb.getStageName(), arb.getTypeArtist(), arb.getDoesUnreleased(), arb.getCity(), arb.getAddress());
        artist.setGenresList(arb.getGenresList());

        PaymentController pc = PaymentServiceFactory.getPaymentController();
        artist.setPaymentServiceAccountId(pc.createPaymentAccount(artist.getEmail()));

        ArtistDAO artistDAO = DAOFactory.getArtistDAO();
        artistDAO.create(artist);

        Session.getSingletonInstance().setUsername(cb.getUsername());
        Session.getSingletonInstance().setUser(artist);
        Session.getSingletonInstance().setRole(Session.UserRole.ARTIST);
    }

    public void completeSignup(ManagerRegistrationBean mrb) throws StripeException {
        Manager manager = new Manager(cb.getUsername(), cb.getEmail(), cb.getPassword());
        Venue venue = new Venue(mrb.getNameVenue(), mrb.getCityVenue(), mrb.getAddressVenue(), mrb.getTypeVenue());
        manager.getVenueList().add(venue);
        manager.setActiveVenue(venue);

        PaymentController pc = PaymentServiceFactory.getPaymentController();
        manager.setPaymentServiceAccountId(pc.createPaymentAccount(manager.getEmail()));

        // salva sia i dati del locale, sia quelli del manager
        VenueDAO venueDAO = DAOFactory.getVenueDAO();
        venueDAO.create(venue, manager);

        Session.getSingletonInstance().setUsername(cb.getUsername());
        Session.getSingletonInstance().setUser(manager);
        Session.getSingletonInstance().setRole(Session.UserRole.MANAGER);
    }
}
