package org.musicinn.musicinn.util.LoginBean;

public abstract class UserRegistrationBean extends UserLoginBean{
    protected String email;

    public UserRegistrationBean(String identifier, String password) {
        super(identifier, password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
