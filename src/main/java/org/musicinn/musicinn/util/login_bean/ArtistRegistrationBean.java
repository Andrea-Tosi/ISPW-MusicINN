package org.musicinn.musicinn.util.login_bean;

import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.util.List;

public class ArtistRegistrationBean extends UserRegistrationBean{
    private String stageName;
    private TypeArtist typeArtist;
    private Boolean doesUnreleased;
    private String city;
    private String address;
    private List<MusicalGenre> genresList;
    private String urlInstagram;
    private String urlSpotify;

    public ArtistRegistrationBean(String identifier, String password) {
        super(identifier, password);
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

    public String getUrlInstagram() {
        return urlInstagram;
    }

    public void setUrlInstagram(String urlInstagram) {
        this.urlInstagram = urlInstagram;
    }

    public String getUrlSpotify() {
        return urlSpotify;
    }

    public void setUrlSpotify(String urlSpotify) {
        this.urlSpotify = urlSpotify;
    }
}
