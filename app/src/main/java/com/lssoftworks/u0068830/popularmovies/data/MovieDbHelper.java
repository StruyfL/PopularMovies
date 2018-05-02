package com.lssoftworks.u0068830.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MovieContract.Movies.TABLE_NAME + " (" +
                MovieContract.Movies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.Movies.COLUMN_NAME_MOVIE_ID + " INT NOT NULL, " +
                MovieContract.Movies.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MovieContract.Movies.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Movies.COLUMN_NAME_RATING + " FLOAT, " +
                MovieContract.Movies.COLUMN_NAME_SYNOPSIS + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DROP_MOVIES_TABLE = "DROP TABLE IF EXISTS " + MovieContract.Movies.TABLE_NAME;
        db.execSQL(SQL_DROP_MOVIES_TABLE);
        onCreate(db);
    }
}
