package org.musicinn.musicinn.model;

public abstract class User {
    protected String username;
    protected String email;
    protected String hashedPassword;
    protected String paymentServiceAccountId;

    protected User(String username, String email, String password, String paymentServiceAccountId){
        this.username = username;
        this.email = email;
        this.hashedPassword = password;
        this.paymentServiceAccountId = paymentServiceAccountId;
    }

    protected User() {
    }

    protected User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.hashedPassword = password;
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

    public String getPaymentServiceAccountId() {
        return paymentServiceAccountId;
    }

    public void setPaymentServiceAccountId(String paymentServiceAccountId) {
        this.paymentServiceAccountId = paymentServiceAccountId;
    }
}
