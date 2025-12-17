package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.Social;

public class SocialAccount {
    private final Social social;
    private String url;
    private String externalID;
    private int follower;

    public SocialAccount(Social social, String url) {
        this.social = social;
        this.url = url;
    }

    public Social getSocial() {
        return social;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }
}
