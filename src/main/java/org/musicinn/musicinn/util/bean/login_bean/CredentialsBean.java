package org.musicinn.musicinn.util.bean.login_bean;

public class CredentialsBean {
    protected String username;
    protected String email;
    protected String password;

    public CredentialsBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public CredentialsBean(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }
}
