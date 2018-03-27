package com.example.android.popularmovies;

/**
 * Created by irina on 13.03.2018.
 */

public class Review {

    private String mUser;
    private String mContent;

    public Review(String user, String content){
        mUser = user;
        mContent = content;
    }

    public String getUser(){
        return mUser;
    }

    public String getContent(){
        return mContent;
    }
}
