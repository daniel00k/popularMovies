package me.danielaguilar.popularmoviesstage1.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.models.Movie;

/**
 * Created by danielaguilar on 01-11-17.
 */

public class JSONMovieParser {
    public static ArrayList<Movie> parseFromString(String json) throws JSONException {
        ArrayList<Movie> movies = new ArrayList<>();
        JSONObject jObject = new JSONObject(json);
        JSONArray jArray = jObject.getJSONArray("results");
        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject object   = jArray.getJSONObject(i);
                // Pulling items from the array
                int id              =   object.getInt("id");
                String poster       =   object.getString("poster_path");
                String overview     =   object.getString("overview");
                String title        =   object.getString("title");
                String releaseDate  =   object.getString("release_date");
                float voteAverage   =   Float.valueOf(object.getString("vote_average"));
                movies.add(new Movie(id, poster, overview, title, releaseDate, voteAverage));

            } catch (JSONException e) {
                // Oops
            }
        }
        return movies;
    }
}
