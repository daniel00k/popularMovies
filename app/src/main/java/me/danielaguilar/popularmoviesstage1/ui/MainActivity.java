package me.danielaguilar.popularmoviesstage1.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.R;
import me.danielaguilar.popularmoviesstage1.adapters.MoviesAdapter;
import me.danielaguilar.popularmoviesstage1.configurations.Constants;
import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.utils.JSONMovieParser;
import me.danielaguilar.popularmoviesstage1.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieClickListener{

    private ArrayList<Movie> movies = new ArrayList<>();
    private RecyclerView recyclerView;
    public static final String MOVIE_TAG = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.moviesGrid);
        recyclerView.setHasFixedSize(true);
        queryApi(Constants.API_GET_POPULAR);
        setMovieGrid();
    }

    private void setMovieGrid(){
        MoviesAdapter adapter = new MoviesAdapter(movies, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    public void onMovieClicked(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDescriptionActivity.class);
        intent.putExtra(MOVIE_TAG, movie);
        startActivity(intent);
    }

    class ApiConnector extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            final String url = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                try {
                    movies = JSONMovieParser.parseFromString(searchResults);
                    setMovieGrid();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search_by_popular:
                queryApi(Constants.API_GET_POPULAR);
                break;
            case R.id.menu_search_by_top_rated:
                queryApi(Constants.API_GET_TOP_RATED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void queryApi(String queryBy){
        new ApiConnector().execute(queryBy);
    }
}
