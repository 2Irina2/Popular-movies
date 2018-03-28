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
    private int mId;
    private String mPosterPath;
    private String mSynopsis;
    private String mRating;
    private String mReleaseDate;

    public Movie(String title, int id, String path, String synopis, String rating, String releaseDate){
        mTitle = title;
        mId = id;
        mPosterPath = path;
        mSynopsis = synopis;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    protected Movie (Parcel in) {
        mTitle = in.readString();
        mId = in.readInt();
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
    public int getId(){ return mId;}
    public String getRawPosterPath() {return mPosterPath;}
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
        parcel.writeInt(mId);
        parcel.writeString(mPosterPath);
        parcel.writeString(mSynopsis);
        parcel.writeString(mRating);
        parcel.writeString(mReleaseDate);
    }
}
