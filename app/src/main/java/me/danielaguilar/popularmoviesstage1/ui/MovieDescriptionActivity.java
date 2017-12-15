package me.danielaguilar.popularmoviesstage1.ui;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
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
import me.danielaguilar.popularmoviesstage1.data.MoviesContract;
import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.models.MovieReview;
import me.danielaguilar.popularmoviesstage1.models.MovieTrailer;
import me.danielaguilar.popularmoviesstage1.utils.ApiConnector;
import me.danielaguilar.popularmoviesstage1.utils.DBConnector;
import me.danielaguilar.popularmoviesstage1.utils.JSONMovieParser;
import me.danielaguilar.popularmoviesstage1.utils.NetworkUtils;

import static me.danielaguilar.popularmoviesstage1.configurations.Constants.IMAGES_BASE_URL;
import static me.danielaguilar.popularmoviesstage1.ui.MainActivity.MOVIE_TAG;

public class MovieDescriptionActivity extends AppCompatActivity implements ApiConnector.OnTaskCompleted<String>, View.OnClickListener, DBConnector.DBConnectorListener<Cursor> {

    private static final int TRAILER_LOADER_ID = 2;
    private static final int REVIEW_LOADER_ID = 3;
    private TextView title, releaseDate, voteAverage, overview, reviewsTxt;
    private ImageView poster;
    private TextView favorite;
    private Movie movie;
    private LinearLayout trailersContainer;
    private ProgressBar indeterminateBar;
    private RecyclerView reviewsContainer;
    private ArrayList<MovieTrailer> trailers = new ArrayList<>();
    private ArrayList<MovieReview> reviews   = new ArrayList<>();
    public static final String CURRENT_MOVIE_TAG    = "movie";
    public static final String TRAILERS_TAG         = "trailers";
    public static final String REVIEWS_TAG          = "reviews";
    public static final String GET_REVIEWS_TAG      = "getReviews";
    public static final String GET_TRAILERS_TAG     = "getTrailers";
    private Uri reviewsUri;
    private Uri trailersUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);
        findViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState != null){
            movie       = savedInstanceState.getParcelable(CURRENT_MOVIE_TAG);
            trailers    = savedInstanceState.getParcelableArrayList(TRAILERS_TAG);
            reviews     = savedInstanceState.getParcelableArrayList(REVIEWS_TAG);
            setUris();
            setTrailers();
            setReviews();
        }else{
            Bundle bundle = getIntent().getExtras();
            movie = bundle.getParcelable(MOVIE_TAG);
            setUris();
            queryForTrailers(movie);
            queryForReviews(movie);
        }
        updateView(movie);
    }

    private void setUris(){
        trailersUri     = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(movie.getId())).appendPath(MoviesContract.PATH_TRAILERS).build();
        reviewsUri      = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(movie.getId())).appendPath(MoviesContract.PATH_REVIEWS).build();
    }

    private void queryForTrailers(Movie movie){
        if(isMovieSaved()){
            new DBConnector(this, this, MovieDescriptionActivity.class.getName()+GET_TRAILERS_TAG,
                    TRAILER_LOADER_ID,
                    trailersUri,
                            new String[] {  MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID,
                                    MoviesContract.MovieTrailerEntry.COLUMN_REMOTE_ID,
                                    MoviesContract.MovieTrailerEntry.COLUMN_NAME,
                                    MoviesContract.MovieTrailerEntry.COLUMN_KEY},
                            null,
                            null,
                            null).execute();
        }else{
            if(NetworkUtils.isConnectionAvailable(this)){
                new ApiConnector(this, MovieDescriptionActivity.class.getName()+GET_TRAILERS_TAG).execute(NetworkUtils.buildUrl(movie.getId()+ Constants.API_GET_VIDEOS));
            }else{
                Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        }

    }

    private void queryForReviews(Movie movie){
        if(isMovieSaved()){
            new DBConnector(this, this, MovieDescriptionActivity.class.getName()+GET_REVIEWS_TAG,
                    REVIEW_LOADER_ID,
                    reviewsUri,
                    new String[] {  MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID,
                    MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID,
                    MoviesContract.MovieReviewEntry.COLUMN_CONTENT,
                    MoviesContract.MovieReviewEntry.COLUMN_AUTHOR},
                    null,
                    null,
                    null).execute();
        }else {
            if (NetworkUtils.isConnectionAvailable(this)) {
                new ApiConnector(this, MovieDescriptionActivity.class.getName() + GET_REVIEWS_TAG).execute(NetworkUtils.buildUrl(movie.getId() + Constants.API_GET_REVIEWS));
            } else {
                Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
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
        final String text = isMovieSaved() ? getString(R.string.star_full) : getString(R.string.star_empty);
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
        reviewsTxt          = findViewById(R.id.reviews);
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
        if(reviews.size()>0){
            reviewsTxt.setVisibility(View.VISIBLE);
        }
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
                getContentResolver().delete(ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI,movie.getId()), null, null);
            }else{
                ContentValues movieCV = new ContentValues();
                movieCV.put(MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID, movie.getId());
                movieCV.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                movieCV.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getPoster());
                movieCV.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                movieCV.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                movieCV.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                final Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieCV);

                if(uri != null){
                    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                    for(MovieReview review:reviews){
                        ContentValues reviewCV = new ContentValues();
                        reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID, movie.getId());
                        reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                        reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_CONTENT, review.getContent());
                        reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID, review.getId());
                        operations.add(ContentProviderOperation.newInsert(reviewsUri)
                                .withValues(reviewCV).build());
                    }

                    for(MovieTrailer trailer:trailers){
                        ContentValues trailerCV = new ContentValues();
                        trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID, movie.getId());
                        trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_KEY, trailer.getKey());
                        trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_NAME, trailer.getName());
                        trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_REMOTE_ID, trailer.getId());
                        operations.add(ContentProviderOperation.newInsert(trailersUri)
                                .withValues(trailerCV).build());
                    }

                    try {
                        getContentResolver().applyBatch(MoviesContract.AUTHORITY, operations);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }
            }
            setFavoriteText();
        }
    }

    private boolean isMovieSaved(){
        Movie storedMovie = null;
        Cursor data = getContentResolver().query(ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, movie.getId()),
                new String[] {  MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID,
                        MoviesContract.MovieEntry.COLUMN_POSTER,
                        MoviesContract.MovieEntry.COLUMN_OVERVIEW,
                        MoviesContract.MovieEntry.COLUMN_TITLE,
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                        MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE},
                null,
                null,
                null);
        if (data != null && data.getCount()>0){
            data.moveToFirst();
            storedMovie = new Movie(  Integer.parseInt(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID))),
                    data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER)),
                    data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)),
                    data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                    data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                    Float.parseFloat(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE))));

        }
        return storedMovie != null;
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

    @Override
    public void onLoadComplete(Cursor cursor, String callerId) {
        if(callerId.equals(MovieDescriptionActivity.class.getName()+GET_TRAILERS_TAG)){
            if (cursor != null && cursor.getCount()>0){
                trailers =  new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        MovieTrailer movieTrailer = new MovieTrailer(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_REMOTE_ID)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_KEY)));
                        trailers.add(movieTrailer);
                    } while (cursor.moveToNext());
                }
                setTrailers();
            }
        }else if(callerId.equals(MovieDescriptionActivity.class.getName()+GET_REVIEWS_TAG)){
            reviews =   new ArrayList<>();
            if (cursor != null && cursor.getCount()>0){
                if (cursor.moveToFirst()) {
                    do {
                        MovieReview movieReview = new MovieReview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_AUTHOR)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_CONTENT)));
                        reviews.add(movieReview);
                    } while (cursor.moveToNext());
                }
                setReviews();
            }
        }
    }
}
