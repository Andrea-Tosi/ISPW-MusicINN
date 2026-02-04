package org.musicinn.musicinn.util;

import org.musicinn.musicinn.model.User;

public class Session {
    public enum UserRole{ARTIST, MANAGER}
    private UserRole role;
    private User user;
    public enum PersistenceType { DATABASE, MEMORY, FILE }
    public enum InterfaceType { GUI, CLI }
    private PersistenceType persistenceType = PersistenceType.DATABASE; // Default
    private InterfaceType interfaceType = InterfaceType.GUI; // Default
    public enum CLIView {LOGIN, ARTIST_HOME, MANAGER_HOME, MANAGE_RIDER, PUBLISH_ANNOUNCEMENT, APPLY_EVENT, ACCEPT_APPLICATION, MANAGE_PAYMENTS, EXIT}
    private CLIView currentCLIView = CLIView.LOGIN;

    private Session() {}

    private static class SingletonContainer{
        public static final Session singletonInstance = new Session();
    }

    public static Session getSingletonInstance() {
        return Session.SingletonContainer.singletonInstance;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
    }

    public CLIView getCurrentCLIView() {
        return currentCLIView;
    }

    public void setCurrentCLIView(CLIView currentCLIView) {
        this.currentCLIView = currentCLIView;
    }
}
