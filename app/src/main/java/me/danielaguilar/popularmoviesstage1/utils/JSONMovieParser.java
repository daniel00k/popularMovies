package me.danielaguilar.popularmoviesstage1.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.models.MovieReview;
import me.danielaguilar.popularmoviesstage1.models.MovieTrailer;

/**
 * Created by danielaguilar on 01-11-17.
 */

public class JSONMovieParser {
    public static final int PARSE_MOVIES    = 1;
    public static final int PARSE_TRAILERS  = 2;
    public static final int PARSE_REVIEWS   = 3;

    public static ArrayList parseFromString(String json, final int parsingType) throws JSONException {
        JSONObject jObject = new JSONObject(json);
        switch (parsingType){
            case PARSE_MOVIES:
                return parseMovies(jObject);
            case PARSE_TRAILERS:
                return parseTrailers(jObject);
            case PARSE_REVIEWS:
                return parseReviews(jObject);
            default:
                return new ArrayList();
        }
    }

    private static ArrayList<Movie> parseMovies(JSONObject jObject) throws JSONException{
        ArrayList<Movie> movies = new ArrayList<>();

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

    private static ArrayList<MovieTrailer> parseTrailers(JSONObject jObject) throws JSONException{
        ArrayList<MovieTrailer> trailers = new ArrayList<>();

        JSONArray jArray = jObject.getJSONArray("results");

        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject object   = jArray.getJSONObject(i);
                // Pulling items from the array
                String id           =   object.getString("id");
                String name         =   object.getString("name");
                String key          =   object.getString("key");
                trailers.add(new MovieTrailer(id, name, key));

            } catch (JSONException e) {
                // Oops
            }
        }
        return trailers;
    }

    private static ArrayList<MovieReview> parseReviews(JSONObject jObject) throws JSONException{
        ArrayList<MovieReview> reviews = new ArrayList<>();

        JSONArray jArray = jObject.getJSONArray("results");

        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject object   = jArray.getJSONObject(i);
                // Pulling items from the array
                String id           =   object.getString("id");
                String author       =   object.getString("author");
                String content      =   object.getString("content");
                reviews.add(new MovieReview(id, author, content));

            } catch (JSONException e) {
                // Oops
            }
        }
        return reviews;
    }
}
