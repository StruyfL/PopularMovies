package com.lssoftworks.u0068830.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.lssoftworks.u0068830.popularmovies.utilities.MovieData;
import com.lssoftworks.u0068830.popularmovies.utilities.MovieDatabaseJsonUtils;
import com.lssoftworks.u0068830.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class DetailsActivity extends AppCompatActivity {

    TextView mOriginalTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mRating;
    TextView mRuntime;
    ImageView mDetailsPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mOriginalTitle = findViewById(R.id.tv_movietitle);
        mReleaseDate = findViewById(R.id.tv_releasedate);
        mRating = findViewById(R.id.tv_rating);
        mRuntime = findViewById(R.id.tv_runtime);
        mOverview = findViewById(R.id.tv_synopsis);
        mDetailsPoster = findViewById(R.id.iv_detailsposter);

        String id = "";
        Intent mainIntent = getIntent();

        if (mainIntent.hasExtra(Intent.EXTRA_TEXT)) {
            id = mainIntent.getStringExtra(Intent.EXTRA_TEXT);

            new FetchMovieTask().execute(id);
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Void, MovieData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieData doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            String id = strings[0];
            URL movieTypeRequestUrl = NetworkUtils.buildMovieUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieTypeRequestUrl);

                MovieData movieData = MovieDatabaseJsonUtils.getMovieData(DetailsActivity.this, jsonMovieResponse);
                return movieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData movieData) {
            if (movieData != null) {
                URL url = NetworkUtils.buildPosterUrl(movieData.getPosterPath());
                Picasso.get().load(url.toString()).into(mDetailsPoster);
                mOriginalTitle.setText(movieData.getOriginalTitle());
                mReleaseDate.setText(movieData.getReleaseDate().substring(0,4));
                mRating.setText(String.valueOf(movieData.getVoteAverage()));
                mOverview.setText(movieData.getOverview());

                String runtime = getString(R.string.runtime_string, movieData.getRuntime());
                mRuntime.setText(runtime);
            }
        }
    }
}
