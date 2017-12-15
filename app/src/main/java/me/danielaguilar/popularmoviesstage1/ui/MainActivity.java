package me.danielaguilar.popularmoviesstage1.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.R;
import me.danielaguilar.popularmoviesstage1.adapters.MoviesAdapter;
import me.danielaguilar.popularmoviesstage1.configurations.Constants;
import me.danielaguilar.popularmoviesstage1.data.MoviesContract;
import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.utils.ApiConnector;
import me.danielaguilar.popularmoviesstage1.utils.DBConnector;
import me.danielaguilar.popularmoviesstage1.utils.JSONMovieParser;
import me.danielaguilar.popularmoviesstage1.utils.NetworkUtils;

public class MainActivity extends BaseActivity implements MoviesAdapter.OnMovieClickListener, ApiConnector.OnTaskCompleted<String>, DBConnector.DBConnectorListener<Cursor>{

    private ArrayList<Movie> movies = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar indeterminateBar;
    public static final int MOVIE_LOADER_ID         = 0;
    public static final String MOVIE_TAG            = "movie";
    public static final String MOVIES_TAG           = "movies";
    public static final String SELECTED_FILTER_TAG  = "selectedFiler";
    private int selectedFilter                      = -1;
    MoviesAdapter adapter;
    private DBConnector dbConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView        = findViewById(R.id.moviesGrid);
        indeterminateBar    = findViewById(R.id.indeterminateBar);

        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null){
            movies = savedInstanceState.getParcelableArrayList(MOVIES_TAG);
        }else{
            queryApi(Constants.API_GET_POPULAR);
        }
        setMovieGrid();

    }

    private void setMovieGrid(){
        indeterminateBar.setVisibility(View.GONE);
        adapter = new MoviesAdapter(movies, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    public void onMovieClicked(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDescriptionActivity.class);
        intent.putExtra(MOVIE_TAG, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        indeterminateBar.setVisibility(View.VISIBLE);
        switch (item.getItemId()){
            case R.id.menu_search_by_popular:
                queryApi(Constants.API_GET_POPULAR);
                selectedFilter =   R.id.menu_search_by_popular;
                break;
            case R.id.menu_search_by_top_rated:
                queryApi(Constants.API_GET_TOP_RATED);
                selectedFilter =   R.id.menu_search_by_top_rated;
                break;
            case R.id.menu_search_by_favorite:
                getFavorites();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavorites(){
        selectedFilter =   R.id.menu_search_by_favorite;
        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */dbConnector = new DBConnector(this, this, MainActivity.class.getName(), MOVIE_LOADER_ID, MoviesContract.MovieEntry.CONTENT_URI,
                new String[] {  MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID,
                        MoviesContract.MovieEntry.COLUMN_POSTER,
                        MoviesContract.MovieEntry.COLUMN_OVERVIEW,
                        MoviesContract.MovieEntry.COLUMN_TITLE,
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                        MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE},
                null,
                null,
                null);
        dbConnector.execute();

    }

    private void queryApi(String queryBy){
        if(NetworkUtils.isConnectionAvailable(this)){
            new ApiConnector(this, MainActivity.class.getName()).execute(NetworkUtils.buildUrl(queryBy));
        }else{
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onComplete(String result, String klassId) {
        if (klassId.equals(MainActivity.class.getName())){
            try {
                movies = JSONMovieParser.parseFromString(result, JSONMovieParser.PARSE_MOVIES);
                setMovieGrid();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*********************************************************************************************************************
    *
    * State callbacks
    **********************************************************************************************************************
    */


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_FILTER_TAG, selectedFilter);
        outState.putParcelableArrayList(MOVIES_TAG, movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle b = getIntent().getExtras();
        if(b!=null){
            selectedFilter = b.getInt(SELECTED_FILTER_TAG);
        }
        // re-queries for all tasks
        if(dbConnector != null){
            dbConnector.restart();
        }
    }

    @Override
    protected void onStop() {
        Bundle b = new Bundle();
        b.putInt(SELECTED_FILTER_TAG, selectedFilter);
        getIntent().putExtras(b);
        super.onStop();

    }

    /*********************************************************************************************************************
     *
     * Loader
     **********************************************************************************************************************
     */

    @Override
    public void onLoadComplete(Cursor data, String callerId) {
            if(callerId.equals(MainActivity.class.getName()) && selectedFilter==R.id.menu_search_by_favorite) {
            if (data != null && data.getCount() > 0) {
                movies = new ArrayList<>();
                if (data.moveToFirst()) {
                    do {
                        Movie movie = new Movie(Integer.parseInt(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID))),
                                data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER)),
                                data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)),
                                data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                                data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                                Float.parseFloat(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE))));
                        movies.add(movie);
                    } while (data.moveToNext());
                }
            }
            setMovieGrid();
        }
    }
}
