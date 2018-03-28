package com.example.infolabsolution.favoritejardb;


import android.os.Parcel;
import android.os.Parcelable;

public class MovieParcelable implements Parcelable {

    String teksTitle;
    String teksReleaseDate;
    String teksOverview;
    double teksRating;
    String imgMovies;
    int teksMovieId;

    public MovieParcelable(String title, String overview, String date,
                           String imagePath, double rating, int id) {
        teksTitle = title;
        teksReleaseDate = date;
        teksOverview = overview;
        teksRating = rating;
        imgMovies = imagePath;
        teksMovieId = id;
    }

    protected MovieParcelable(Parcel in) {
        teksTitle = in.readString();
        teksReleaseDate = in.readString();
        teksOverview = in.readString();
        teksRating = in.readDouble();
        imgMovies = in.readString();
        teksMovieId = in.readInt();
    }

    public static final Creator<MovieParcelable> CREATOR = new Creator<MovieParcelable>() {
        @Override
        public MovieParcelable createFromParcel(Parcel in) {
            return new MovieParcelable(in);
        }

        @Override
        public MovieParcelable[] newArray(int size) {
            return new MovieParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel texts, int flags) {
        texts.writeString(teksTitle);
        texts.writeString(teksReleaseDate);
        texts.writeString(teksOverview);
        texts.writeDouble(teksRating);
        texts.writeString(imgMovies);
        texts.writeInt(teksMovieId);
    }
}
