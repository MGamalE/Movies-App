package com.example.android.popularmovie1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class MovieDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MovieDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_MOVIE + "(" +
                MovieContract.MovieEntry._ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + MovieContract.MovieEntry.ID + " INTEGER," +
                MovieContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.POSTER_PATH +
                " TEXT NOT NULL, " +
                MovieContract.MovieEntry.VOTE +
                " REAL NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_MOVIE);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MovieContract.MovieEntry.TABLE_MOVIE + "'");

        onCreate(sqLiteDatabase);
    }
}
