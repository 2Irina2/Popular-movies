package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        RecyclerViewAdapter.ItemClickListener {

    private int NUMBER_COLUMNS = 2;
    private String criterion;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Movie> finalMovieList;
    private LinearLayout errorDisplayLinearLayout;
    private ProgressBar loadingIndicatorProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorDisplayLinearLayout = findViewById(R.id.error_message);
        loadingIndicatorProgressBar = findViewById(R.id.loading_indicator);
        recyclerView = findViewById(R.id.movies_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_COLUMNS));

        setUpSharedPreferences();
    }

    public void setUpSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        criterion = sharedPreferences.getString(
                getResources().getString(R.string.preference_sort_key),
                getResources().getString(R.string.preference_sort_default));
        makeMovieDBSearchQuery(criterion);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.preference_sort_key))){
            criterion = sharedPreferences.getString(
                    getResources().getString(R.string.preference_sort_key),
                    getResources().getString(R.string.preference_sort_default));
            makeMovieDBSearchQuery(criterion);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void makeMovieDBSearchQuery(String string){
        URL movieDBUrl = NetworkUtils.buildURL(string);
        if(NetworkUtils.hasInternetAccess(this)){
            new MovieDBQueryTask().execute(movieDBUrl);
            errorDisplayLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            errorDisplayLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_sort:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;

            default:
                return super .onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Movie selectedMovie = recyclerViewAdapter.getItem(position);

        Intent startDetailsActivity = new Intent(this, DetailsActivity.class);
        startDetailsActivity.putExtra("selectedMovie", selectedMovie);
        startActivity(startDetailsActivity);
    }

    public class MovieDBQueryTask extends AsyncTask<URL, Void, List<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(URL... urls) {

            URL queryUrl = urls[0];
            String movieDbQueryResult = null;
            List<Movie> returnedMovies = null;

            try{
                movieDbQueryResult = NetworkUtils.getResponseFromHTTPUrl(queryUrl);
            } catch (IOException e){
                Log.e(this.getClass().getName(), "IOException: " + queryUrl);
            }

            try {
                returnedMovies = NetworkUtils.parseJSON(movieDbQueryResult);
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "JSONException");
            }

            return returnedMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(movies != null && !movies.isEmpty()){
                finalMovieList = movies;
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, movies);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.setClickListener(MainActivity.this);
            }

            loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}
