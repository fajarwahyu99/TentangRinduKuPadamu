package com.example.infolabsolution.favoritejardb;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.infolabsolution.favoritejardb.databinding.ActivityFavoriteMoviesBinding;
import com.squareup.picasso.Picasso;

public class FavoriteMoviesActivity extends AppCompatActivity{

    public Intent gvFavorites;
    public ActivityFavoriteMoviesBinding teksBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);
        gvFavorites = getIntent();
        teksBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_movies);

        if (gvFavorites.hasExtra("image")) {
            Picasso.with(this).load(gvFavorites.getStringExtra("image")).into(teksBinding.favoriteImageView);
        }
        if (gvFavorites.hasExtra("overview")) {
            teksBinding.favoriteOverviewTextView.setText(gvFavorites.getStringExtra("overview"));
        }
        if (gvFavorites.hasExtra("title")) {
            teksBinding.favoriteTitleTextView.setText(gvFavorites.getStringExtra("title"));
        }
        if (gvFavorites.hasExtra("date")) {
            teksBinding.favoriteReleaseDateTextView.setText(gvFavorites.getStringExtra("date"));
        }
        if (gvFavorites.hasExtra("rating")) {
            teksBinding.favoriteRatingTextView.setText(gvFavorites.getStringExtra("rating"));
        }
    }
}
