package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.util.ArrayList;
import java.util.List;

public class Artist extends User{
    private String stageName;
    private TypeArtist typeArtist;
    private Boolean doesUnreleased;
    private String city;
    private String address;
    private List<MusicalGenre> genresList;
    private List<SocialAccount> socialAccounts;
    private ArtistRider rider;
    private List<Application> applications;

    public Artist(String username, String email, String password) {
        super(username, email, password);
        this.genresList = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    public Artist(String username, String email, String password, String paymentServiceAccountId) {
        super(username, email, password, paymentServiceAccountId);
        this.genresList = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    public Artist() {
        super();
        this.genresList = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public TypeArtist getTypeArtist() {
        return typeArtist;
    }

    public void setTypeArtist(TypeArtist typeArtist) {
        this.typeArtist = typeArtist;
    }

    public Boolean getDoesUnreleased() {
        return doesUnreleased;
    }

    public void setDoesUnreleased(Boolean doesUnreleased) {
        this.doesUnreleased = doesUnreleased;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<MusicalGenre> getGenresList() {
        return genresList;
    }

    public void setGenresList(List<MusicalGenre> genresList) {
        this.genresList = genresList;
    }

    public List<SocialAccount> getSocialAccounts() {
        return socialAccounts;
    }

    public void setSocialAccounts(List<SocialAccount> socialAccounts) {
        this.socialAccounts = socialAccounts;
    }

    public void addSocialAccount(SocialAccount socialAccount) {
        if (getSocialAccounts() == null) {
            setSocialAccounts(new ArrayList<>());
        }
        getSocialAccounts().add(socialAccount);
    }

    public ArtistRider getRider() {
        return rider;
    }

    public void setRider(ArtistRider rider) {
        this.rider = rider;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
