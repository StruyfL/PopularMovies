package com.lssoftworks.u0068830.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.lssoftworks.u0068830.popularmovies.data.MovieContract.AUTHORITY;
import static com.lssoftworks.u0068830.popularmovies.data.MovieContract.PATH_MOVIES;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;
    private static final int ALL_MOVIES = 1;
    private static final int MOVIE_ID = 2;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Uri for complete movie table
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES, ALL_MOVIES);
        // Uri for one row in movie table
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES + "/#", MOVIE_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor result;

        switch (match) {
            case ALL_MOVIES:
                result = db.query(MovieContract.Movies.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case MOVIE_ID:
                result = db.query(MovieContract.Movies.TABLE_NAME, projection, null, null, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case ALL_MOVIES:
                String[] columns = {MovieContract.Movies._ID };
                String[] ids = { MovieContract.Movies._ID };
                Cursor movie = query(MovieContract.Movies.CONTENT_URI, columns, "_ID = ?", ids, null);

                if(movie.getCount() == 0) {
                    long id = db.insert(MovieContract.Movies.TABLE_NAME, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(MovieContract.Movies.CONTENT_URI, id);
                    } else {
                        throw new SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
