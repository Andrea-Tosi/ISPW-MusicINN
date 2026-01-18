package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.SocialAccount;

public class SocialAccountDAOMemory {
    public void create(SocialAccount socialAccount, String username) {
        System.out.println("social account (" + socialAccount.getSocial() + ") di " + username + " creato");
    }
}
