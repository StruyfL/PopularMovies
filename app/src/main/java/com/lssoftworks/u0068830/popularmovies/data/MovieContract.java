package com.lssoftworks.u0068830.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {

    public static final String AUTHORITY = "com.lssoftworks.u0068830.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movie";


    private MovieContract() { }

    public static class Movies implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_NAME_MOVIE_ID = "movieid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_RELEASE_DATE = "releasedate";
        public static final String COLUMN_NAME_SYNOPSIS = "synopsis";
    }
}
