package com.lssoftworks.u0068830.popularmovies.utilities;

/**
 * Created by u0068830 on 16/03/2018.
 */

public class MovieData {
    private int id;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private int runtime;
    private int trailerCount;
    private String[] trailers;
    private int reviewCount;
    private String[] reviewAuthors;
    private String[] reviewContent;


    public MovieData(int trailerCount, int reviewCount) {
        trailers = new String[trailerCount];
        this.trailerCount = trailerCount;
        this.reviewCount = reviewCount;
    }

    public int getTrailerCount() {
        return trailerCount;
    }

    public void setTrailerCount(int trailerCount) {
        this.trailerCount = trailerCount;
    }

    public String[] getTrailers() {
        return trailers;
    }

    public void setTrailers(String[] trailers) {
        this.trailers = trailers;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String[] getReviewAuthors() {
        return reviewAuthors;
    }

    public String[] getReviewContent() {
        return reviewContent;
    }

    public void setReviews(String[] reviewAuthors, String[] reviewContent) {
        this.reviewAuthors = reviewAuthors;
        this.reviewContent = reviewContent;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return this.id;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

}
