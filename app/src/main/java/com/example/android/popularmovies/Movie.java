package com.example.android.popularmovies;

import java.io.Serializable;

/**
 * Created by irina on 25.02.2018.
 */

public class Movie implements Serializable{

    private static final String IMAGE_URL_BASE = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_URL_SIZE = "w185/";

    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private String mRating;
    private String mReleaseDate;

    public Movie(String title, String path, String synopis, String rating, String releaseDate){
        mTitle = title;
        mPosterPath = path;
        mSynopsis = synopis;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getPosterPath(){
        return IMAGE_URL_BASE + IMAGE_URL_SIZE + mPosterPath;
    }
    public String getSynopsis(){
        return mSynopsis;
    }
    public String getRating(){
        return mRating;
    }
    public String getReleaseDate(){
        return mReleaseDate;
    }

}
