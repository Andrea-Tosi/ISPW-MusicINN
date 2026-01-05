package org.musicinn.musicinn.util;

public class Session {
    public enum UserRole{ARTIST, MANAGER}
    private UserRole role;

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
}
