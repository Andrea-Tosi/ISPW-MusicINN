package org.musicinn.musicinn.util.login_bean;

public abstract class UserRegistrationBean extends UserLoginBean{
    protected String email;

    protected UserRegistrationBean(String identifier, String password) {
        super(identifier, password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
