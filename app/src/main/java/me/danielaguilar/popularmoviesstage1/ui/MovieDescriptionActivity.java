package me.danielaguilar.popularmoviesstage1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.danielaguilar.popularmoviesstage1.R;
import me.danielaguilar.popularmoviesstage1.models.Movie;

import static me.danielaguilar.popularmoviesstage1.configurations.Constants.IMAGES_BASE_URL;
import static me.danielaguilar.popularmoviesstage1.ui.MainActivity.MOVIE_TAG;

public class MovieDescriptionActivity extends AppCompatActivity {

    private TextView title, releaseDate, voteAverage, overview;
    private ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);
        Bundle bundle = getIntent().getExtras();
        Movie movie = bundle.getParcelable(MOVIE_TAG);
        findViews();
        updateView(movie);
    }

    private void updateView(Movie movie) {
        Picasso.with(this).load(IMAGES_BASE_URL+movie.getPoster()).into(poster);
        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        voteAverage.setText(movie.getVoteAverage()+"");
    }

    private void findViews(){
        title       = findViewById(R.id.movie_title);
        releaseDate = findViewById(R.id.release_date);
        voteAverage = findViewById(R.id.vote_average);
        overview    = findViewById(R.id.overview);
        poster      = findViewById(R.id.movie_poster);
    }
}
