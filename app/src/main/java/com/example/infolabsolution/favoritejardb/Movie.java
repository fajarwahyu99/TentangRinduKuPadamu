package com.example.infolabsolution.favoritejardb;

public class Movie {
    private String teksTitle;
    private String teksOverview;
    private String teksReleaseDate;
    private String mMovieImage;
    private double teksRating;
    private int teksMovieId;

    public Movie(String title, String overview, String releaseDate, String image, double rating, int id) {
        teksTitle = title;
        teksOverview = overview;
        teksReleaseDate = releaseDate;
        mMovieImage = image;
        teksRating = rating;
        teksMovieId = id;
    }

    public String getMovieTitle() {
        return teksTitle;
    }

    public String getMovieOverview() {
        return teksOverview;
    }

    public String getMovieReleaseDate() {
        return teksReleaseDate;
    }

    public String getMovieImage() {
        return mMovieImage;
    }

    public double getMovieRating() {
        return teksRating;
    }

    public int getMovieId() {
        return teksMovieId;
    }

}
