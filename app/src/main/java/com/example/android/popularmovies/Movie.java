package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by irina on 25.02.2018.
 */

public class Movie implements Parcelable{

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

    protected Movie (Parcel in) {
        mTitle = in.readString();
        mPosterPath = in.readString();
        mSynopsis = in.readString();
        mRating = in.readString();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mRating);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mSynopsis);
    }
}
