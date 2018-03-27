package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by irina on 21.03.2018.
 */

public class PopularMoviesContract {

    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_POSTER = "moviePoster";
        public static final String COLUMN_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_RATING = "movieRating";
        public static final String COLUMN_RELEASE_DATE = "movieReleaseDate";
    }
}
