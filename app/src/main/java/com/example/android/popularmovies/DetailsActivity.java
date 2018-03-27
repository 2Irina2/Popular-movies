package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    TextView movieTitleTextView;
    ImageView moviePosterImageView;
    TextView movieRatingTextView;
    ImageView movieRatingImageView;
    TextView movieDateTextView;
    TextView movieSynopsisTextView;
    ListView movieTrailersListView;
    ListView movieReviewsListView;
    TextView movieTrailersEmptyTextView;
    TextView movieReviewsEmptyTextView;

    TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;
    Movie selectedMovie = null;

    long movieDbId;

    SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PopularMoviesDbHelper dbHelper = PopularMoviesDbHelper.getInstance(this);
        mDb = dbHelper.getWritableDatabase();

        selectedMovie = getIntent().getParcelableExtra("selectedMovie");

        movieTitleTextView = findViewById(R.id.details_title_tv);
        moviePosterImageView = findViewById(R.id.details_poster_iv);
        movieRatingTextView = findViewById(R.id.details_rating_tv);
        movieRatingImageView = findViewById(R.id.details_rating_icon);
        movieDateTextView = findViewById(R.id.details_date_tv);
        movieSynopsisTextView = findViewById(R.id.details_synopsis_tv);
        movieTrailersListView = findViewById(R.id.details_trailers_lv);
        movieReviewsListView = findViewById(R.id.details_reviews_lv);
        movieTrailersEmptyTextView = findViewById(R.id.trailers_empty_tv);
        movieReviewsEmptyTextView = findViewById(R.id.reviews_empty_tv);


        String movieTitle = selectedMovie.getTitle();
        int movieId = selectedMovie.getId();
        String moviePoster = selectedMovie.getPosterPath();
        String movieRating = selectedMovie.getRating();
        String movieDate = selectedMovie.getReleaseDate();
        String movieSynopsis = selectedMovie.getSynopsis();

        movieTitleTextView.setText(movieTitle);
        Picasso.with(this)
                .load(moviePoster)
                .into(moviePosterImageView);
        movieRatingTextView.setText(movieRating);
        float movieRatingInt = Float.parseFloat(movieRating);
        if(movieRatingInt < 5){
            movieRatingImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
        } else if (movieRatingInt < 8){
            movieRatingImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
        } else {
            movieRatingImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_full_black_24dp));
        }
        movieDateTextView.setText(movieDate);
        movieSynopsisTextView.setText(movieSynopsis);

        makeTrailersQuerySearch(movieId);
        makeReviewsQuerySearch(movieId);

        Cursor queryMovieCursor = queryMovie(selectedMovie.getId());
        if(queryMovieCursor != null && queryMovieCursor.getCount() > 0){
            queryMovieCursor.moveToFirst();
            movieDbId = queryMovieCursor.getInt(
                    queryMovieCursor.getColumnIndex(PopularMoviesContract.MovieEntry._ID));
        } else {
            movieDbId = -1;
        }

        Log.e(DetailsActivity.class.getName(), "Elements in table: " + DatabaseUtils.queryNumEntries(mDb, PopularMoviesContract.MovieEntry.TABLE_NAME));
    }

    public void makeTrailersQuerySearch(int id){
        URL trailersUrl = NetworkUtils.buildTrailersURL(id);
        if(NetworkUtils.hasInternetAccess(this)){
            new TrailersQueryTask().execute(trailersUrl);
        } else {
            movieTrailersEmptyTextView.setText(R.string.error_internet_connection);
        }
    }

    public void makeReviewsQuerySearch(int id){
        URL reviewsUrl = NetworkUtils.buildReviewsURL(id);
        if(NetworkUtils.hasInternetAccess(this)) {
            new ReviewsQueryTask().execute(reviewsUrl);
        }  else {
            movieReviewsEmptyTextView.setText(R.string.error_internet_connection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite, menu);

        MenuItem favoriteIcon = menu.getItem(0);
        if(movieDbId != -1){
            favoriteIcon.setIcon(R.drawable.ic_favorite_white_24dp);
        }
        else{
            favoriteIcon.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_favorite:
                if(movieDbId != -1){
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    removeMovie(movieDbId);
                    movieDbId = -1;
                }
                else{
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    movieDbId = addNewMovie(selectedMovie);
                    Log.e(DetailsActivity.class.getName(), Long.toString(movieDbId));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private long addNewMovie(Movie movie){
        ContentValues cv = new ContentValues();

        cv.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_RATING, movie.getRating());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());

        return mDb.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, cv);
    }

    private void removeMovie(long id){
        mDb.delete(PopularMoviesContract.MovieEntry.TABLE_NAME,
                PopularMoviesContract.MovieEntry._ID + " = " + id,
                null);
    }

    private Cursor queryMovie(int id){
        String[] whereIs = {Integer.toString(id)};
        return mDb.query(PopularMoviesContract.MovieEntry.TABLE_NAME,
                null,
                PopularMoviesContract.MovieEntry.COLUMN_ID + " = ? ",
                whereIs,
                null,
                null,
                null);
    }

    public class TrailersQueryTask extends AsyncTask<URL, Void, List<Trailer>>{

        @Override
        protected List<Trailer> doInBackground(URL... urls) {

            URL queryUrl = urls[0];
            String trailerQueryResult = null;
            List<Trailer> returnedTrailers = null;


            try{
                trailerQueryResult = NetworkUtils.getResponseFromHTTPUrl(queryUrl);
            } catch (IOException e){
                Log.e(this.getClass().getName(), "IOException: " + queryUrl);
            }

            try {
                returnedTrailers = NetworkUtils.parseTrailersJSON(trailerQueryResult);
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "JSONException");
            }

            return returnedTrailers;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if(trailers != null & !trailers.isEmpty()){
                trailersAdapter = new TrailersAdapter(getApplicationContext(), trailers);
                movieTrailersListView.setAdapter(trailersAdapter);
                movieTrailersEmptyTextView.setVisibility(View.GONE);


                movieTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Trailer selectedTrailer = (Trailer) adapterView.getItemAtPosition(i);

                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(selectedTrailer.getTrailerPath()));
                        startActivity(youtubeIntent);
                    }
                });
            } else {
                movieTrailersEmptyTextView.setText(R.string.empty_trailers_text_view);
            }
        }
    }

    public class ReviewsQueryTask extends AsyncTask<URL, Void, List<Review>>{

        @Override
        protected List<Review> doInBackground(URL... urls) {

            URL queryUrl = urls[0];
            String reviewQueryResult = null;
            List<Review> returnedReviews = null;


            try{
                reviewQueryResult = NetworkUtils.getResponseFromHTTPUrl(queryUrl);
            } catch (IOException e){
                Log.e(this.getClass().getName(), "IOException: " + queryUrl);
            }

            try {
                returnedReviews = NetworkUtils.parseReviewsJSON(reviewQueryResult);
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "JSONException");
            }

            return returnedReviews;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if(reviews != null && !reviews.isEmpty()){
                reviewsAdapter = new ReviewsAdapter(getApplicationContext(), reviews);
                movieReviewsListView.setAdapter(reviewsAdapter);
                movieReviewsEmptyTextView.setVisibility(View.GONE);
            } else {
                movieReviewsEmptyTextView.setText(R.string.empty_reviews_text_view);
            }
        }
    }
}
