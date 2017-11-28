package com.example.android.popularmovie1;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovie1.MovieProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_MOVIE = "movie";

        public static final String ID = "id";
        public static final String RELEASE_DATE = "date";
        public static final String OVERVIEW = "overview";
        public static final String TITLE = "title";
        public static final String VOTE = "vote";
        public static final String POSTER_PATH = "poster";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_MOVIE).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
