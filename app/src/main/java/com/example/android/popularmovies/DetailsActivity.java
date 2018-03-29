package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        TrailersAdapter.ItemClickListener{

    @BindView(R.id.details_title_tv) TextView movieTitleTextView;
    @BindView(R.id.details_poster_iv) ImageView moviePosterImageView;
    @BindView(R.id.details_rating_tv) TextView movieRatingTextView;
    @BindView(R.id.details_rating_icon) ImageView movieRatingImageView;
    @BindView(R.id.details_date_tv) TextView movieDateTextView;
    @BindView(R.id.details_synopsis_tv) TextView movieSynopsisTextView;
    @BindView(R.id.details_trailers_rv) RecyclerView movieTrailersRecyclerView;
    @BindView(R.id.details_reviews_lv) ListView movieReviewsListView;
    @BindView(R.id.trailers_empty_tv) TextView movieTrailersEmptyTextView;
    @BindView(R.id.reviews_empty_tv) TextView movieReviewsEmptyTextView;

    private TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;
    Movie selectedMovie = null;

    long movieDbId;
    private static final int DETAILS_LOADER_ID = 10;

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

        ButterKnife.bind(this);
        movieTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false));

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

        getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);

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
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER, movie.getRawPosterPath());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_RATING, movie.getRating());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(PopularMoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());

        Uri uri = getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, cv);
        String id = uri.getPathSegments().get(1);

        return Long.parseLong(id);
    }

    private void removeMovie(long id){
        String stringId = Long.toString(id);
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    String[] whereIs = {Integer.toString(selectedMovie.getId())};
                    return getContentResolver().query(PopularMoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            PopularMoviesContract.MovieEntry.COLUMN_ID + " = ? ",
                            whereIs,
                            null);
                } catch (Exception e){
                    Log.e(MainActivity.class.getName(), "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }


            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if(data != null && data.getCount() > 0){
            data.moveToFirst();
            movieDbId = data.getInt(
                    data.getColumnIndex(PopularMoviesContract.MovieEntry._ID));
        } else {
            movieDbId = -1;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

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
                movieTrailersRecyclerView.setAdapter(trailersAdapter);
                movieTrailersEmptyTextView.setVisibility(View.GONE);
                trailersAdapter.setClickListener(DetailsActivity.this);

            } else {
                movieTrailersEmptyTextView.setText(R.string.empty_trailers_text_view);
            }
        }
    }
    @Override
    public void onItemClick(View view, int position) {
        Trailer selectedTrailer = trailersAdapter.getItem(position);

        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(selectedTrailer.getTrailerPath()));
        startActivity(youtubeIntent);
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
