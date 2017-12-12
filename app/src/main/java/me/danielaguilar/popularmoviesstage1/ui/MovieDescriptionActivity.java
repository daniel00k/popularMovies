package me.danielaguilar.popularmoviesstage1.ui;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.R;
import me.danielaguilar.popularmoviesstage1.adapters.ReviewsAdapter;
import me.danielaguilar.popularmoviesstage1.components.TrailerDescriptionComponent;
import me.danielaguilar.popularmoviesstage1.configurations.Constants;
import me.danielaguilar.popularmoviesstage1.data.MoviesDbHelper;
import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.models.MovieReview;
import me.danielaguilar.popularmoviesstage1.models.MovieTrailer;
import me.danielaguilar.popularmoviesstage1.utils.ApiConnector;
import me.danielaguilar.popularmoviesstage1.utils.JSONMovieParser;
import me.danielaguilar.popularmoviesstage1.utils.NetworkUtils;

import static me.danielaguilar.popularmoviesstage1.configurations.Constants.IMAGES_BASE_URL;
import static me.danielaguilar.popularmoviesstage1.ui.MainActivity.MOVIE_TAG;

public class MovieDescriptionActivity extends AppCompatActivity implements ApiConnector.OnTaskCompleted<String>, View.OnClickListener {

    private TextView title, releaseDate, voteAverage, overview;
    private ImageView poster;
    private Button favorite;
    private Movie movie;
    private LinearLayout trailersContainer;
    private ProgressBar indeterminateBar;
    private RecyclerView reviewsContainer;
    private ArrayList<MovieTrailer> trailers = new ArrayList<>();
    private ArrayList<MovieReview> reviews   = new ArrayList<>();
    private SQLiteDatabase mDb;
    public static final String CURRENT_MOVIE_TAG    = "movie";
    public static final String TRAILERS_TAG         = "trailers";
    public static final String REVIEWS_TAG          = "reviews";
    public static final String GET_REVIEWS_TAG      = "getReviews";
    public static final String GET_TRAILERS_TAG     = "getTrailers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);
        findViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        if(savedInstanceState != null){
            movie       = savedInstanceState.getParcelable(CURRENT_MOVIE_TAG);
            trailers    = savedInstanceState.getParcelableArrayList(TRAILERS_TAG);
            reviews     = savedInstanceState.getParcelableArrayList(REVIEWS_TAG);
            setTrailers();
            setReviews();
        }else{
            Bundle bundle = getIntent().getExtras();
            movie = bundle.getParcelable(MOVIE_TAG);
            queryForTrailers(movie);
            queryForReviews(movie);
        }
        updateView(movie);
    }

    private void queryForTrailers(Movie movie){

        if(NetworkUtils.isConnectionAvailable(this)){
            new ApiConnector(this, MovieDescriptionActivity.class.getName()+GET_TRAILERS_TAG).execute(NetworkUtils.buildUrl(movie.getId()+ Constants.API_GET_VIDEOS));
        }else{
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        }

    }

    private void queryForReviews(Movie movie){

        if(NetworkUtils.isConnectionAvailable(this)){
            new ApiConnector(this, MovieDescriptionActivity.class.getName()+GET_REVIEWS_TAG).execute(NetworkUtils.buildUrl(movie.getId()+Constants.API_GET_REVIEWS));
        }else{
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        }

    }

    private void updateView(Movie movie) {
        Picasso.with(this).load(IMAGES_BASE_URL+movie.getPoster()).into(poster);
        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        voteAverage.setText(movie.getVoteAverage()+"");
        setFavoriteText();
    }

    private void setFavoriteText(){
        final String text = isMovieSaved() ? "Favorite" : "No Favorite";
        favorite.setText(text);
    }

    private void findViews(){
        title               = findViewById(R.id.movie_title);
        releaseDate         = findViewById(R.id.release_date);
        voteAverage         = findViewById(R.id.vote_average);
        overview            = findViewById(R.id.overview);
        poster              = findViewById(R.id.movie_poster);
        trailersContainer   = findViewById(R.id.trailers_container);
        reviewsContainer    = findViewById(R.id.reviews_container);
        indeterminateBar    = findViewById(R.id.indeterminateBar);
        favorite            = findViewById(R.id.make_favorite_movie);
        favorite.setOnClickListener(this);
    }

    private void setTrailers() {
        indeterminateBar.setVisibility(View.GONE);
        for (MovieTrailer trailer: trailers){
            TrailerDescriptionComponent tdc = new TrailerDescriptionComponent(this);
            tdc.setNameAndLink(trailer.getUrl().toString(), trailer.getName());
            trailersContainer.addView(tdc);
        }
    }

    private void setReviews() {
        ReviewsAdapter adapter = new ReviewsAdapter(reviews);
        reviewsContainer.setAdapter(adapter);
        reviewsContainer.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onComplete(String result, String klassId) {
        if (klassId.equals(MovieDescriptionActivity.class.getName()+GET_REVIEWS_TAG)
                || klassId.equals(MovieDescriptionActivity.class.getName()+GET_TRAILERS_TAG)){

            try {
                if(klassId.equals(MovieDescriptionActivity.class.getName()+GET_REVIEWS_TAG)){
                    reviews = JSONMovieParser.parseFromString(result, JSONMovieParser.PARSE_REVIEWS);
                    setReviews();
                }else if(klassId.equals(MovieDescriptionActivity.class.getName()+GET_TRAILERS_TAG)){
                    trailers = JSONMovieParser.parseFromString(result, JSONMovieParser.PARSE_TRAILERS);
                    setTrailers();
                }else{
                    //Nothing here
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRAILERS_TAG, trailers);
        outState.putParcelableArrayList(REVIEWS_TAG, reviews);
        outState.putParcelable(CURRENT_MOVIE_TAG, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        movie       = savedInstanceState.getParcelable(CURRENT_MOVIE_TAG);
        trailers    = savedInstanceState.getParcelableArrayList(TRAILERS_TAG);
        reviews     = savedInstanceState.getParcelableArrayList(REVIEWS_TAG);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(favorite)){
            if(isMovieSaved()){
                final boolean deleted = MoviesDbHelper.deleteMovie(mDb, movie);
            }else{
                final long id = MoviesDbHelper.insertMovie(mDb, movie, trailers, reviews);
            }
            setFavoriteText();
        }
    }

    private boolean isMovieSaved(){
        return MoviesDbHelper.findMovie(mDb, String.valueOf(movie.getId()))!=null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
