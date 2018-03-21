package com.lssoftworks.u0068830.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Created by u0068830 on 13/03/2018.
 */

public class NetworkUtils {

    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String SIZE_PARAM = "w185/";
    private static final String API_KEY = "api_key";
    // Don't forget to add your own API key if you want to contact the Movie Database!!!
    private static final String API_KEY_VALUE = "8a2531a32865958e464858f0c902327d";


    public static URL buildUrl(String sortType) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + sortType).buildUpon()
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildPosterUrl (String posterPath) {
        Uri builtUri = Uri.parse(MOVIE_POSTER_BASE_URL + SIZE_PARAM + posterPath).buildUpon()
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildMovieUrl(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + id).buildUpon()
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
