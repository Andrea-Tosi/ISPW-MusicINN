package org.musicinn.musicinn.model;

import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;

import java.util.List;

public class Artist extends User{
    private String stageName;
    private TypeArtist typeArtist;
    private Boolean doesUnreleased;
    private String city;
    private String address;
    private List<MusicalGenre> genresList;

    public Artist(String username, String email, String password, String stageName, TypeArtist typeArtist, boolean b, String city, String address) {
        super(username, email, password);
        this.stageName = stageName;
        this.typeArtist = typeArtist;
        this.doesUnreleased = b;
        this.city = city;
        this.address = address;
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
}
