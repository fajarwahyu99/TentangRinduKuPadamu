package com.example.infolabsolution.favoritejardb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import com.example.infolabsolution.favoritejardb.MovieContract;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import static com.example.infolabsolution.favoritejardb.NetworkUtils.createUrl;
import static com.example.infolabsolution.favoritejardb.NetworkUtils.extractResponseFromJson;
import static com.example.infolabsolution.favoritejardb.NetworkUtils.getResponseFromHttpUrl;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public RecyclerView gvMovie;
    public GridView gvFavoriteMovie;
    public MovieParcelable favoriteParcelable;
    public ArrayList<MovieParcelable> listfavoriteParcelable;
    public FavoriteMovieCursorAdapter favAdapter;
    private static final int FAVORITE_MOVIE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(MainActivity.this, getString(R.string.network_connection_error_message),
                    Toast.LENGTH_LONG).show();
        }

        favAdapter = new FavoriteMovieCursorAdapter(this, null);

        gvMovie = (RecyclerView) findViewById(R.id.movie_recycler_view);
        gvMovie.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 3, LinearLayoutManager.VERTICAL, false);
        gvMovie.setLayoutManager(gridLayoutManager);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("favorite_key") != null) {
            Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null, null);
            favAdapter = new FavoriteMovieCursorAdapter(this, cursor);
            gvMovie.setVisibility(View.INVISIBLE);
            gvFavoriteMovie = (GridView) findViewById(R.id.favoriteMovieGridView);
            gvFavoriteMovie.setVisibility(View.VISIBLE);
            gvFavoriteMovie.setNumColumns(3);
            gvFavoriteMovie.setAdapter(favAdapter);
            getFavoriteMovies(cursor);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("favorite_key", listfavoriteParcelable);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        gvFavoriteMovie = (GridView) findViewById(R.id.favoriteMovieGridView);


        if (itemThatWasClickedId == R.id.action_bahasa) {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        }

        if (itemThatWasClickedId == R.id.action_favorite) {
            Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null, null);
            favAdapter = new FavoriteMovieCursorAdapter(this, cursor);
            gvMovie.setVisibility(View.INVISIBLE);
            gvFavoriteMovie.setVisibility(View.VISIBLE);
            gvFavoriteMovie.setNumColumns(3);
            gvFavoriteMovie.setAdapter(favAdapter);
            getFavoriteMovies(cursor);
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<MovieParcelable> getFavoriteMovies(Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                favoriteParcelable = new MovieParcelable(
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER)),
                        Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_RATING))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_ID))));
                listfavoriteParcelable = new ArrayList<>();
                listfavoriteParcelable.add(favoriteParcelable);
                cursor.moveToNext();
            }
        }
        return listfavoriteParcelable;
    }
}
