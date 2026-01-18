package org.musicinn.musicinn.model;

public abstract class User {
    protected String username;
    protected String email;
    protected String hashedPassword;

    protected User(String username, String email, String hashedPassword){
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
