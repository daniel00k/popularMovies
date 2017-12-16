package me.danielaguilar.popularmoviesstage1.configurations;

import me.danielaguilar.popularmoviesstage1.BuildConfig;

/**
 * Created by danielaguilar on 01-11-17.
 */

public class Constants {
    public static final String API_KEY              = BuildConfig.API_KEY;//TODO add api_key in gradle.properties
    public static final String API_BASE_URL         = "https://api.themoviedb.org";
    public static final String IMAGES_BASE_URL      = "http://image.tmdb.org/t/p/w500";//"http://image.tmdb.org/t/p/w185";
    public static final String API_BASE_PATH        = "/3/movie/";
    public static final String API_GET_POPULAR      = "popular";
    public static final String API_GET_TOP_RATED    = "top_rated";
    public static final String API_GET_VIDEOS       = "/videos";
    public static final String API_GET_REVIEWS      = "/reviews";
}
