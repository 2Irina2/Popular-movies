package com.example.android.popularmovies;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    TextView movieTitleTextView;
    ImageView moviePosterImageView;
    TextView movieRatingTextView;
    ImageView movieRatingImageView;
    TextView movieDateTextView;
    TextView movieSynopsisTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Movie selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");

        movieTitleTextView = findViewById(R.id.details_title_tv);
        moviePosterImageView = findViewById(R.id.details_poster_iv);
        movieRatingTextView = findViewById(R.id.details_rating_tv);
        movieRatingImageView = findViewById(R.id.details_rating_icon);
        movieDateTextView = findViewById(R.id.details_date_tv);
        movieSynopsisTextView = findViewById(R.id.details_synopsis_tv);


        String movieTitle = selectedMovie.getTitle();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
