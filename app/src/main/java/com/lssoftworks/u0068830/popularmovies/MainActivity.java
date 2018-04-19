package com.lssoftworks.u0068830.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lssoftworks.u0068830.popularmovies.data.MovieContract;
import com.lssoftworks.u0068830.popularmovies.utilities.MovieData;
import com.lssoftworks.u0068830.popularmovies.utilities.MovieDatabaseJsonUtils;
import com.lssoftworks.u0068830.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int COLUMN_COUNT = 2;
    private RecyclerView mMoviePosters;
    private MovieAdapter mAdapter;
    MovieData[] movieData;
    public static OnViewholderClickListener viewholderClickListener;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviePosters = findViewById(R.id.rv_movieposters);

        // Register the onSharedPreferenceChangedListener to the sharedPreferences object.
        // Read sort order preference from SharedPreferences file and execute AsyncTask with that value.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sharedPreferences.getString(getResources().getString(R.string.sortorder_key), "popular");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        new FetchMoviesTask().execute(sortOrder);

        // Create the layout manager for sorting the movie posters in a 2-column grid.
        // Attach the layoutmanager to the RecyclerView.
        // Attach the adapter to the RecyclerView.
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        mMoviePosters.setLayoutManager(layoutManager);
        mMoviePosters.setHasFixedSize(true);

        mAdapter = new MovieAdapter(movieData);
        mMoviePosters.setAdapter(mAdapter);

        viewholderClickListener = new OnViewholderClickListener();
    }

    class OnViewholderClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            ImageView mMoviePoster = view.findViewById(R.id.iv_movieposter);
            String id = mMoviePoster.getTag().toString();
            intent.putExtra(Intent.EXTRA_TEXT, id);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.settings) {
            Context context = MainActivity.this;
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getResources().getString(R.string.sortorder_key))) {
            String sortOrder = sharedPreferences.getString(getResources().getString(R.string.sortorder_key), "popular");

            new FetchMoviesTask().execute(sortOrder);
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Integer, MovieData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieData[] doInBackground(String... strings) {

            if (strings[0].equals("popular") || strings[0].equals("top_rated")) {
                try {
                    Socket sock = new Socket();
                    sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                    sock.close();

                    sock.close();

                } catch (IOException e) {
                    publishProgress(0);
                    return null;
                }

                if (strings.length == 0) {
                    return null;
                }

                String type = strings[0];
                URL movieTypeRequestUrl = NetworkUtils.buildUrl(type);

                try {
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieTypeRequestUrl);

                    return MovieDatabaseJsonUtils.getAllMovieData(MainActivity.this, jsonMovieResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else if (strings[0].equals("favorites")) {
                try {
                    Cursor movies = getContentResolver().query(MovieContract.Movies.CONTENT_URI, null, null, null, null);
                    MovieData[] movieData = new MovieData[movies.getCount()];

                    movies.moveToFirst();

                    for(int i = 0; i < movies.getCount(); i++) {
                        movieData[i] = new MovieData(0, 0);

                        movieData[i].setId(movies.getInt(movies.getColumnIndex(MovieContract.Movies._ID)));
                        movieData[i].setOriginalTitle(movies.getString(movies.getColumnIndex(MovieContract.Movies.COLUMN_NAME_TITLE)));
                        movieData[i].setVoteAverage(movies.getDouble(movies.getColumnIndex(MovieContract.Movies.COLUMN_NAME_RATING)));
                        movieData[i].setReleaseDate(movies.getString(movies.getColumnIndex(MovieContract.Movies.COLUMN_NAME_RELEASE_DATE)));
                        movieData[i].setOverview(movies.getString(movies.getColumnIndex(MovieContract.Movies.COLUMN_NAME_SYNOPSIS)));

                        movies.moveToNext();
                    }

                    movies.close();

                    return movieData;

                } catch (Exception e) {
                    Log.e(TAG, "Failed to load data from database");
                    e.printStackTrace();
                    return null;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(MovieData[] movieData) {
            if (movieData != null) {
                mAdapter.setMovieData(movieData);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            Toast.makeText(MainActivity.this, "No internet connection available! Please check your network settings", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister onSharedPreferenceChangedListener.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
