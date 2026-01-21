package org.musicinn.musicinn.util.bean;

import org.musicinn.musicinn.util.bean.technical_rider_bean.TechnicalRiderBean;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationBean {
    // Dati reali che hai
    private int id;
    private String artistStageName;
    private List<MusicalGenre> artistGenres;
    private LocalDateTime requestedSoundcheck;

    // Dati per l'interfaccia (per ora gestiti con default)
    private double totalScore;
    private double matchGenresPercentage;
    private double popularityPercentage = 0.0; // Default
    private double reviewsPercentage = 0.0;   // Default
    private String averageStars = "N/D";
    private int numReviews = 0;
    private int followersCount = 0;
    private TechnicalRiderBean riderBean;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtistStageName() {
        return artistStageName;
    }

    public void setArtistStageName(String artistStageName) {
        this.artistStageName = artistStageName;
    }

    public List<MusicalGenre> getArtistGenres() {
        return artistGenres;
    }

    public void setArtistGenres(List<MusicalGenre> artistGenres) {
        this.artistGenres = artistGenres;
    }

    public LocalDateTime getRequestedSoundcheck() {
        return requestedSoundcheck;
    }

    public void setRequestedSoundcheck(LocalDateTime requestedSoundcheck) {
        this.requestedSoundcheck = requestedSoundcheck;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getMatchGenresPercentage() {
        return matchGenresPercentage;
    }

    public void setMatchGenresPercentage(double matchGenresPercentage) {
        this.matchGenresPercentage = matchGenresPercentage;
    }

    public double getPopularityPercentage() {
        return popularityPercentage;
    }

    public void setPopularityPercentage(double popularityPercentage) {
        this.popularityPercentage = popularityPercentage;
    }

    public double getReviewsPercentage() {
        return reviewsPercentage;
    }

    public void setReviewsPercentage(double reviewsPercentage) {
        this.reviewsPercentage = reviewsPercentage;
    }

    public String getAverageStars() {
        return averageStars;
    }

    public void setAverageStars(String averageStars) {
        this.averageStars = averageStars;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public TechnicalRiderBean getRiderBean() {
        return riderBean;
    }

    public void setRiderBean(TechnicalRiderBean riderBean) {
        this.riderBean = riderBean;
    }
}
