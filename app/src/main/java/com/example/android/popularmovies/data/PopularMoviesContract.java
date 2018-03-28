package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by irina on 21.03.2018.
 */

public class PopularMoviesContract {

    public static final String AUTHORITY =  "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIE = "movie";


    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_POSTER = "moviePoster";
        public static final String COLUMN_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_RATING = "movieRating";
        public static final String COLUMN_RELEASE_DATE = "movieReleaseDate";
    }
}
