package com.lssoftworks.u0068830.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lssoftworks.u0068830.popularmovies.data.MovieContract;
import com.lssoftworks.u0068830.popularmovies.utilities.MovieData;
import com.lssoftworks.u0068830.popularmovies.utilities.MovieDatabaseJsonUtils;
import com.lssoftworks.u0068830.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;


public class DetailsActivity extends AppCompatActivity {

    TextView mOriginalTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mRating;
    TextView mRuntime;
    Button mFavorites;
    ImageView mDetailsPoster;
    LinearLayout mDetailsLayout;
    LinearLayout mReviewLayout;

    String id;
    String sortOrder;
    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mOriginalTitle = findViewById(R.id.tv_movietitle);
        mReleaseDate = findViewById(R.id.tv_releasedate);
        mRating = findViewById(R.id.tv_rating);
        mRuntime = findViewById(R.id.tv_runtime);
        mOverview = findViewById(R.id.tv_synopsis);
        mFavorites = findViewById(R.id.btn_favorite);
        mDetailsPoster = findViewById(R.id.iv_detailsposter);
        mDetailsLayout = findViewById(R.id.ll_trailers);
        mReviewLayout = findViewById(R.id.ll_reviews);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortOrder = sharedPreferences.getString(getResources().getString(R.string.sortorder_key), "popular");

        Intent mainIntent = getIntent();

        if (mainIntent.hasExtra(Intent.EXTRA_TEXT)) {
            id = mainIntent.getStringExtra(Intent.EXTRA_TEXT);

            //Toast.makeText(this, id, Toast.LENGTH_LONG).show();
            new FetchMovieTask().execute(id, sortOrder);
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Integer, MovieData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieData doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            if (strings[1].equals("popular") || strings[1].equals("top_rated")) {
                try {
                    Socket sock = new Socket();
                    sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                    sock.close();

                    sock.close();

                } catch (IOException e) {
                    publishProgress(0);
                    return null;
                }

                String id = strings[0];
                URL movieTypeRequestUrl = NetworkUtils.buildMovieUrl(id);
                URL movieVideosRequestUrl = NetworkUtils.buildMovieEndPointUrl(id, NetworkUtils.VIDEOS_ENDPOINT);
                URL movieReviewsRequestUrl = NetworkUtils.buildMovieEndPointUrl(id, NetworkUtils.REVIEWS_ENDPOINT);

                try {
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieTypeRequestUrl);
                    String jsonMovieVideosResponse = NetworkUtils.getResponseFromHttpUrl(movieVideosRequestUrl);
                    String jsonMovieReviewsResponse = NetworkUtils.getResponseFromHttpUrl(movieReviewsRequestUrl);

                    return MovieDatabaseJsonUtils.getMovieData(DetailsActivity.this, jsonMovieResponse,
                            jsonMovieVideosResponse, jsonMovieReviewsResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (strings[1].equals("favorites")) {
                try {
                    Toast.makeText(DetailsActivity.this, sortOrder, Toast.LENGTH_LONG).show();
                    String[] columns = {MovieContract.Movies._ID };
                    String[] ids = { strings[0] };
                    Cursor movie = getContentResolver().query(MovieContract.Movies.CONTENT_URI, columns, "_ID = ?", ids, null);
                    MovieData movieData = new MovieData(0, 0);

                    movie.moveToFirst();

                    movieData.setId(movie.getInt(movie.getColumnIndex(MovieContract.Movies._ID)));
                    movieData.setOriginalTitle(movie.getString(movie.getColumnIndex(MovieContract.Movies.COLUMN_NAME_TITLE)));
                    movieData.setVoteAverage(movie.getDouble(movie.getColumnIndex(MovieContract.Movies.COLUMN_NAME_RATING)));
                    movieData.setReleaseDate(movie.getString(movie.getColumnIndex(MovieContract.Movies.COLUMN_NAME_RELEASE_DATE)));
                    movieData.setOverview(movie.getString(movie.getColumnIndex(MovieContract.Movies.COLUMN_NAME_SYNOPSIS)));

                    movie.close();

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

                String[] trailerUrls = movieData.getTrailers();
                int trailerCount = movieData.getTrailerCount();

                for(int i = 0; i < trailerCount; i++) {
                    Button trailerButton = (Button) getLayoutInflater().inflate(R.layout.trailer_button, mDetailsLayout, false);
                    trailerButton.setText(getString(R.string.trailer_string, i+1));
                    trailerButton.setTag(trailerUrls[i]);
                    mDetailsLayout.addView(trailerButton);
                }

                String[] reviewAuthors = movieData.getReviewAuthors();
                String[] reviewContent = movieData.getReviewContent();
                int reviewCount = movieData.getReviewCount();

                for(int i = 0; i < reviewCount; i++) {
                    ConstraintLayout reviewLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.review_pane, mReviewLayout, false);
                    TextView authorView = (TextView) reviewLayout.getChildAt(0);
                    authorView.setText(reviewAuthors[i]);
                    TextView contentView = (TextView) reviewLayout.getChildAt(1);
                    contentView.setText(reviewContent[i]);
                    mReviewLayout.addView(reviewLayout);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            Toast.makeText(DetailsActivity.this, "No internet connection available! Please check your network settings", Toast.LENGTH_LONG).show();
        }
    }

    public void startTrailer(View view) {
        String url = view.getTag().toString();
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void addToFavorites(View view) {
        ContentValues movieValue = new ContentValues();

        if(id.length() != 0) {
            movieValue.put(MovieContract.Movies._ID, id);
            movieValue.put(MovieContract.Movies.COLUMN_NAME_TITLE, mOriginalTitle.getText().toString());
            movieValue.put(MovieContract.Movies.COLUMN_NAME_RATING, mRating.getText().toString());
            movieValue.put(MovieContract.Movies.COLUMN_NAME_RELEASE_DATE, mReleaseDate.getText().toString());
            movieValue.put(MovieContract.Movies.COLUMN_NAME_SYNOPSIS, mOverview.getText().toString());

            Uri uri = getContentResolver().insert(MovieContract.Movies.CONTENT_URI, movieValue);

            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "Wrong ID", Toast.LENGTH_LONG).show();
        }
    }
}
