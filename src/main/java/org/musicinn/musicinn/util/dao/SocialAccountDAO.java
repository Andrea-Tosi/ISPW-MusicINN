package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.model.SocialAccount;

public class SocialAccountDAO {
    public void save(SocialAccount socialAccount, String username) {
        System.out.println("social account (" + socialAccount.getSocial() + ") di " + username + " creato");
    }
}
