package org.musicinn.musicinn.util;

public class Session {
    public enum UserRole{ARTIST, MANAGER}
    private UserRole role;
    private String username;

    public enum PersistenceType { DATABASE, MEMORY, FILE }
    public enum InterfaceType { GUI, CLI }
    private PersistenceType persistenceType = PersistenceType.DATABASE; // Default
    private InterfaceType interfaceType = InterfaceType.GUI; // Default

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
