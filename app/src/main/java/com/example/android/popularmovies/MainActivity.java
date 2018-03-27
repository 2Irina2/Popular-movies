package com.example.android.popularmovies;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.ListPreference;
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

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesDbHelper;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorDisplayLinearLayout = findViewById(R.id.error_message);
        loadingIndicatorProgressBar = findViewById(R.id.loading_indicator);
        recyclerView = findViewById(R.id.movies_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_COLUMNS));

        PopularMoviesDbHelper dbHelper = PopularMoviesDbHelper.getInstance(this);
        mDb = dbHelper.getReadableDatabase();
        setUpSharedPreferences();

    }

    public void setUpSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        queryByCriterion(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.preference_sort_key))){
            recyclerView.setAdapter(null);
            queryByCriterion(sharedPreferences);
        }
    }

    public void queryByCriterion(SharedPreferences sharedPreferences){
        criterion = sharedPreferences.getString(
                getResources().getString(R.string.preference_sort_key),
                getResources().getString(R.string.preference_sort_default));
        if(!criterion.equals("favorites")){
            makeMovieDBSearchQuery(criterion);
        } else {
            makeSqlDBQuery();
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
            TextView errorTv = errorDisplayLinearLayout.findViewById(R.id.error_message_tv);
            errorTv.setText(getResources().getString(R.string.error_internet_connection));
        }
    }

    public void makeSqlDBQuery(){
        Cursor movies = mDb.query(
                PopularMoviesContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                PopularMoviesContract.MovieEntry._ID);

        if(movies.getCount() <= 0){
            errorDisplayLinearLayout.setVisibility(View.VISIBLE);
            TextView errorTv = errorDisplayLinearLayout.findViewById(R.id.error_message_tv);
            errorTv.setText(getResources().getString(R.string.error_database_empty));
        } else {
            Log.e(MainActivity.class.getName(), "DB NOT EMPTY");
            List<Movie> favoriteMovies = new ArrayList<Movie>();
            movies.moveToFirst();
            do{
                String movieName = movies.getString(movies.getColumnIndex(
                        PopularMoviesContract.MovieEntry.COLUMN_MOVIE_TITLE));
                int movieId = movies.getInt(movies.getColumnIndex(
                        PopularMoviesContract.MovieEntry.COLUMN_ID));
                String moviePosterPath = movies.getString(movies.getColumnIndex(
                        PopularMoviesContract.MovieEntry.COLUMN_POSTER));
                String movieSynopsis = movies.getString(movies.getColumnIndex(
                        PopularMoviesContract.MovieEntry.COLUMN_SYNOPSIS));
                String movieRating = movies.getString(movies.getColumnIndex(
                        PopularMoviesContract.MovieEntry.COLUMN_RATING));
                String movieReleaseDate = movies.getString(movies.getColumnIndex(
                        PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE));

                Log.e(MainActivity.class.getName(), movieName + " : " + movieRating);

                favoriteMovies.add(new Movie(movieName,
                        movieId,
                        moviePosterPath,
                        movieSynopsis,
                        movieRating,
                        movieReleaseDate));

            } while(movies.moveToNext());

            finalMovieList = favoriteMovies;
            movies.close();
            Log.e(MainActivity.class.getName(), "FINISHED ITERATING THROUGH CURSOR");
            setUpRecyclerViewAdapter();
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
                returnedMovies = NetworkUtils.parseMoviesJSON(movieDbQueryResult);
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "JSONException");
            }

            return returnedMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(movies != null && !movies.isEmpty()){
                finalMovieList = movies;
                setUpRecyclerViewAdapter();
            }

            loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setUpRecyclerViewAdapter(){
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, finalMovieList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setClickListener(MainActivity.this);
        Log.e(MainActivity.class.getName(), "RECYCLERVIEW ADAPTER SET");
    }

}
