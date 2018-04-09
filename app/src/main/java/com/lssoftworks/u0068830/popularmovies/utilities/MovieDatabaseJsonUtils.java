package com.lssoftworks.u0068830.popularmovies.utilities;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by u0068830 on 13/03/2018.
 */

public class MovieDatabaseJsonUtils {

    private final static String MDB_RESULT = "results";

    // Constants for movie information
    private final static String MDB_POSTER_PATH = "poster_path";
    private final static String MDB_ID = "id";
    private final static String MDB_TITLE = "original_title";
    private final static String MDB_OVERVIEW = "overview";
    private final static String MDB_RELEASE_DATE = "release_date";
    private final static String MDB_USER_RATING = "vote_average";
    private final static String MDB_RUNTIME = "runtime";

    // Constants for review information
    private final static String MDB_AUTHOR = "author";
    private final static String MDB_CONTENT = "content";

    // Constants for video information
    private final static String MDB_TYPE = "type";
    private final static String MDB_NAME = "name";
    private final static String MDB_SITE = "site";
    private static final String MDB_KEY = "key";

    public static MovieData[] getAllMovieData (Context context, String movieJsonStr) throws JSONException {

        final String MDB_MESSAGE_CODE = "cod";

        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(MDB_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(MDB_MESSAGE_CODE);

            switch(errorCode) {
                case HttpURLConnection.HTTP_OK:
                    Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Toast.makeText(context, "NOT FOUND", Toast.LENGTH_SHORT).show();
                    return null;
                default:
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(MDB_RESULT);
        MovieData[] movieData = new MovieData[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++) {
            movieData[i] = new MovieData(0);
            JSONObject movieObject = movieArray.getJSONObject(i);

            movieData[i].setId(movieObject.getInt(MDB_ID));
            movieData[i].setPosterPath(movieObject.getString(MDB_POSTER_PATH));
        }

        return movieData;
    }

    public static MovieData getMovieData (Context context, String movieJsonStr, String movieVideosJsonString,
                                          String movieReviewsJsonString) throws JSONException {

        final String MDB_MESSAGE_CODE = "cod";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONObject movieVideoJson = new JSONObject(movieVideosJsonString);
        JSONObject movieReviewJson = new JSONObject(movieReviewsJsonString);

        if (movieJson.has(MDB_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(MDB_MESSAGE_CODE);

            switch(errorCode) {
                case HttpURLConnection.HTTP_OK:
                    Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Toast.makeText(context, "NOT FOUND", Toast.LENGTH_SHORT).show();
                    return null;
                default:
                    return null;
            }
        }

        int id = movieJson.getInt(MDB_ID);
        String movieTitle = movieJson.getString(MDB_TITLE);
        String overview = movieJson.getString(MDB_OVERVIEW);
        String posterPath = movieJson.getString(MDB_POSTER_PATH);
        double rating = movieJson.getDouble(MDB_USER_RATING);
        String releaseDate = movieJson.getString(MDB_RELEASE_DATE);
        int runtime = movieJson.getInt(MDB_RUNTIME);
        JSONArray movieReviews = movieReviewJson.getJSONArray(MDB_RESULT);
        JSONArray movieTrailers = movieVideoJson.getJSONArray(MDB_RESULT);
        int trailerCount = 0;
        int reviewCount = 0;
        ArrayList<String> trailerUrls = new ArrayList<>();
        String[] trailerUrlsArray;

        for(int i = 0; i < movieTrailers.length(); i++) {
            JSONObject movieTrailer = movieTrailers.getJSONObject(i);
            if(movieTrailer.getString(MDB_TYPE).equals("Trailer")) {
                trailerCount++;
                trailerUrls.add("http://www.youtube.com/watch?v=" + movieTrailer.getString(MDB_KEY));
            }
        }

        trailerUrlsArray = new String[trailerCount];

        MovieData movieData = new MovieData(trailerCount);

        movieData.setId(id);
        movieData.setOriginalTitle(movieTitle);
        movieData.setOverview(overview);
        movieData.setPosterPath(posterPath);
        movieData.setVoteAverage(rating);
        movieData.setReleaseDate(releaseDate);
        movieData.setRuntime(runtime);
        movieData.setTrailers(trailerUrls.toArray(trailerUrlsArray));


        return movieData;
    }
}
