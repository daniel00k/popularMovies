package me.danielaguilar.popularmoviesstage1.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
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
import me.danielaguilar.popularmoviesstage1.data.MoviesDbHelper;
import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.utils.ApiConnector;
import me.danielaguilar.popularmoviesstage1.utils.JSONMovieParser;
import me.danielaguilar.popularmoviesstage1.utils.NetworkUtils;

public class MainActivity extends BaseActivity implements MoviesAdapter.OnMovieClickListener, ApiConnector.OnTaskCompleted<String>{

    private ArrayList<Movie> movies = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar indeterminateBar;
    private static int SELECTED_FILTER              = -1;
    public static final String MOVIE_TAG            = "movie";
    public static final String MOVIES_TAG           = "movies";
    public static final String SELECTED_FILTER_TAG  = "selectedFiler";
    MoviesAdapter adapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
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
                SELECTED_FILTER = R.id.menu_search_by_popular;
                break;
            case R.id.menu_search_by_top_rated:
                queryApi(Constants.API_GET_TOP_RATED);
                SELECTED_FILTER = R.id.menu_search_by_top_rated;
                break;
            case R.id.menu_search_by_favorite:
                getFavorites();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavorites(){
        movies = MoviesDbHelper.findAllMovies(mDb);
        setMovieGrid();
        SELECTED_FILTER = R.id.menu_search_by_favorite;
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
        outState.putInt(SELECTED_FILTER_TAG, SELECTED_FILTER);
        outState.putParcelableArrayList(MOVIES_TAG, movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null ){
            if(savedInstanceState.getInt(SELECTED_FILTER_TAG)==R.id.menu_search_by_favorite){
                getFavorites();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle b = getIntent().getExtras();
        if(b!=null&& b.getInt(SELECTED_FILTER_TAG) == R.id.menu_search_by_favorite){
            getFavorites();
        }
    }

    @Override
    protected void onStop() {
        Bundle b = new Bundle();
        b.putInt(SELECTED_FILTER_TAG, SELECTED_FILTER);
        getIntent().putExtras(b);
        super.onStop();
    }
}
