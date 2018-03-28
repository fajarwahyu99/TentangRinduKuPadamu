package com.example.infolabsolution.favoritejardb;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.infolabsolution.favoritejardb.MovieContract.MovieEntry;
import com.example.infolabsolution.favoritejardb.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.infolabsolution.favoritejardb.NetworkUtils.createUrl;
import static com.example.infolabsolution.favoritejardb.NetworkUtils.extractReviewFromJson;
import static com.example.infolabsolution.favoritejardb.NetworkUtils.getResponseFromHttpUrl;

public class MovieDetailActivity extends AppCompatActivity {

    String reviewUrlString;
    ImageButton mFavoriteImageButton;
    RecyclerView  reviewListView;

    int mId, mMovieQueryId;
    Intent intentFromMainActivity;
    int mFavoriteButtonUnclicked = R.drawable.favorite_white_24px;
    int mFavoriteButtonClicked = R.drawable.favorite_border_white_24px;
    public ShareActionProvider mShareActionProvider;
    ActivityMovieDetailBinding teksBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        intentFromMainActivity = getIntent();
        teksBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        mFavoriteImageButton = (ImageButton) findViewById(R.id.favoriteButton);

        if (intentFromMainActivity.hasExtra("image")) {
            Picasso.with(this).load(intentFromMainActivity.getStringExtra("image")).into(teksBinding.movieImageView);
        }
        if (intentFromMainActivity.hasExtra("overview")) {
            teksBinding.movieOverviewTextView.setText(intentFromMainActivity.getStringExtra("overview"));
        }
        if (intentFromMainActivity.hasExtra("title")) {
            teksBinding.movieTitleTextView.setText(intentFromMainActivity.getStringExtra("title"));
        }
        if (intentFromMainActivity.hasExtra("date")) {
            teksBinding.movieRelaseDateTextView.setText(intentFromMainActivity.getStringExtra("date"));
        }
        if (intentFromMainActivity.hasExtra("rating")) {
            teksBinding.movieRatingTextView.setText(String.valueOf(intentFromMainActivity.getDoubleExtra("rating", 0)));
        }
        if (intentFromMainActivity.hasExtra("id")) {
            mId = intentFromMainActivity.getIntExtra("id", 0);
            reviewUrlString = "http://api.themoviedb.org/3/movie/" + mId + "/reviews?api_key=92fc8095e11194d676367347621d94c0";
            ReviewAsyncTask taskForReview = new ReviewAsyncTask();
            taskForReview.execute();
        }

        Cursor favoriteMovieCursor = getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null, null);
        mFavoriteImageButton.setImageResource(mFavoriteButtonUnclicked);
        mFavoriteImageButton.setTag(mFavoriteButtonUnclicked);
        if (favoriteMovieCursor != null) {
            favoriteMovieCursor.moveToFirst();
            try {
                while (!favoriteMovieCursor.isAfterLast()) {
                    mMovieQueryId = favoriteMovieCursor.getInt(favoriteMovieCursor.getColumnIndexOrThrow("id"));
                    if (mMovieQueryId != mId) {
                        favoriteMovieCursor.moveToNext();
                    } else {
                        mFavoriteImageButton.setImageResource(mFavoriteButtonClicked);
                        mFavoriteImageButton.setTag(mFavoriteButtonClicked);
                        break;
                    }
                }
            } finally {
                favoriteMovieCursor.close();
            }
        }

        mFavoriteImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Uri newUri;

                if (Integer.parseInt(mFavoriteImageButton.getTag().toString()) == mFavoriteButtonUnclicked) {
                    ContentValues values = new ContentValues();
                    values.put(MovieEntry.COLUMN_MOVIE_NAME, intentFromMainActivity.getStringExtra("title"));
                    values.put(MovieEntry.COLUMN_MOVIE_POSTER, intentFromMainActivity.getStringExtra("image"));
                    values.put(MovieEntry.COLUMN_MOVIE_RATING, String.valueOf(intentFromMainActivity.getDoubleExtra("rating", 0)));
                    values.put(MovieEntry.COLUMN_MOVIE_DATE, intentFromMainActivity.getStringExtra("date"));
                    values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, intentFromMainActivity.getStringExtra("overview"));
                    values.put(MovieEntry.COLUMN_MOVIE_ID, intentFromMainActivity.getIntExtra("id", 0));
                    newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);
                    if (newUri == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.add_favorite_fail_message),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mFavoriteImageButton.setImageResource(mFavoriteButtonClicked);
                        mFavoriteImageButton.setTag(mFavoriteButtonClicked);
                        Toast.makeText(getApplicationContext(), getString(R.string.add_favorite_success_message),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Cursor favoriteCursor = getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null, null);
                    int rowId = 1;
                    if (favoriteCursor != null) {
                        favoriteCursor.moveToFirst();
                        rowId = favoriteCursor.getInt(favoriteCursor.getColumnIndexOrThrow(MovieEntry._ID));
                        try {
                            while (!favoriteCursor.isAfterLast()) {
                                int movieQueryId = favoriteCursor.getInt(favoriteCursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_ID));
                                if (movieQueryId != mId) {
                                    favoriteCursor.moveToNext();
                                    rowId = favoriteCursor.getInt(favoriteCursor.getColumnIndexOrThrow(MovieEntry._ID));
                                } else {
                                    break;
                                }
                            }
                        } finally {
                            favoriteCursor.close();
                        }
                    }

                    Uri movieToDeleteUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, rowId);
                    showDeleteConfirmationDialog(movieToDeleteUri);
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(final Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this movie?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                if (uri != null) {
                    int rowsDeleted = getContentResolver().delete(uri, null, null);
                    if (rowsDeleted == 0) {
                        Toast.makeText(getApplicationContext(), "Error with deleting movie",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mFavoriteImageButton.setImageResource(mFavoriteButtonUnclicked);
                        mFavoriteImageButton.setTag(mFavoriteButtonUnclicked);
                        Toast.makeText(getApplicationContext(), "Movie deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private class ReviewAsyncTask extends AsyncTask<URL, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(URL... urls) {
            URL reviewUrl = createUrl(reviewUrlString);
            String jsonResponse = " ";
            try {
                jsonResponse = getResponseFromHttpUrl(reviewUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> reviews = extractReviewFromJson(jsonResponse);
            return reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<String> reviews) {
            ReviewAdapter adapter = new ReviewAdapter(getApplicationContext(), reviews);
            reviewListView = (RecyclerView) findViewById(R.id.review_recycler_view);
            reviewListView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            reviewListView.setLayoutManager(linearLayoutManager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(myShareIntent);
        return true;
    }
}
