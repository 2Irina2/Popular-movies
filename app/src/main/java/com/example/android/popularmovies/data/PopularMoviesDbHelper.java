package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;

/**
 * Created by irina on 21.03.2018.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static PopularMoviesDbHelper sInstance;
    private static final String DATABASE_NAME = "popularmovies.db";
    private static final int DATABASE_VERSION = 1;

    private PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_POPULARMOVIES_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " Text NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_POPULARMOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public static synchronized PopularMoviesDbHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new PopularMoviesDbHelper(context.getApplicationContext());
        }

        return sInstance;
    }
}
