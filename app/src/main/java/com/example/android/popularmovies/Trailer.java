package com.example.android.popularmovies;

/**
 * Created by irina on 28.02.2018.
 */

public class Trailer {
    private static final String TRAILER_URL_BASE = "https://www.youtube.com/watch?v=";

    private String mName;
    private String mPath;

    public Trailer(String name, String path){
        mName = name;
        mPath = path;
    }

    public String getTrailerName(){ return mName; };
    public String getTrailerPath(){ return TRAILER_URL_BASE + mPath; };
}
