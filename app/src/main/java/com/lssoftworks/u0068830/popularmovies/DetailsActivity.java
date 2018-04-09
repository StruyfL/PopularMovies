package com.lssoftworks.u0068830.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lssoftworks.u0068830.popularmovies.utilities.MovieData;
import com.lssoftworks.u0068830.popularmovies.utilities.MovieDatabaseJsonUtils;
import com.lssoftworks.u0068830.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URL;


public class DetailsActivity extends AppCompatActivity {

    TextView mOriginalTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mRating;
    TextView mRuntime;
    ImageView mDetailsPoster;
    LinearLayout mDetailsLayout;
    LinearLayout mReviewLayout;

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
        mDetailsLayout = findViewById(R.id.ll_trailers);
        mReviewLayout = findViewById(R.id.ll_reviews);

        String id;
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
                    Button trailerButton = (Button) getLayoutInflater().inflate(R.layout.trailer_button, null);
                    trailerButton.setText(getString(R.string.trailer_string, i+1));
                    trailerButton.setTag(trailerUrls[i]);
                    mDetailsLayout.addView(trailerButton);
                }

                String[] reviewAuthors = movieData.getReviewAuthors();
                String[] reviewContent = movieData.getReviewContent();
                int reviewCount = movieData.getReviewCount();

                for(int i = 0; i < reviewCount; i++) {
                    ConstraintLayout reviewLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.review_pane, null);
                    TextView authorView = (TextView) reviewLayout.getChildAt(0);
                    authorView.setText(reviewAuthors[i]);
                    TextView contentView = (TextView) reviewLayout.getChildAt(1);
                    contentView.setText(reviewContent[i]);
                    mReviewLayout.addView(reviewLayout);
                }
            }
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
}
