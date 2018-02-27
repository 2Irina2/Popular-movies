package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.BuildConfig;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by irina on 25.02.2018.
 */

public class NetworkUtils {

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String PARAM_API_KEY = "api_key";
    private static final String API_KEY = com.example.android.popularmovies.BuildConfig.API_KEY;

    public static URL buildURL(String criterion){
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(criterion)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e(NetworkUtils.class.getName(), "Malformed URL: " + url);
        }

        return url;
    }

    public static String getResponseFromHTTPUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.e(NetworkUtils.class.getName(), Integer.toString(urlConnection.getResponseCode()));
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Movie> parseJSON(String string) throws JSONException {
        List<Movie> finalMovieList = new ArrayList<Movie>();
        JSONObject result = new JSONObject(string);

        if(result.has("results")){
            JSONArray movieList = result.getJSONArray("results");

            if(movieList.length() != 0){
                for(int i = 0; i < movieList.length(); i++){
                    JSONObject movieJsonObject = movieList.getJSONObject(i);

                    String movieTitle = "Unknown";
                    if(movieJsonObject.has("title")){
                        if(!movieJsonObject.getString("title").equals("")){
                            movieTitle = movieJsonObject.getString("title");
                        }
                    }

                    String moviePosterPath = "N/A";
                    if(movieJsonObject.has("poster_path")){
                        if(!movieJsonObject.getString("poster_path").equals("")){
                            moviePosterPath = movieJsonObject.getString("poster_path");
                        }
                    }

                    String movieSynopsis = "None";
                    if(movieJsonObject.has("overview")){
                        if(!movieJsonObject.getString("overview").equals("")){
                            movieSynopsis = movieJsonObject.getString("overview");
                        }
                    }

                    String movieRating = "No rating yet";
                    if(movieJsonObject.has("vote_average")){
                        if(!movieJsonObject.getString("vote_average").equals("")){
                            movieRating = movieJsonObject.getString("vote_average");
                        }
                    }

                    String movieReleaseDate = "Not released yet";
                    if(movieJsonObject.has("release_date")){
                        if(!movieJsonObject.getString("release_date").equals("")){
                            movieReleaseDate = movieJsonObject.getString("release_date");
                        }
                    }

                    finalMovieList.add(new Movie(movieTitle,
                            moviePosterPath,
                            movieSynopsis,
                            movieRating,
                            movieReleaseDate));
                }
            }
        }

        return finalMovieList;
    }

    public static boolean hasInternetAccess(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
