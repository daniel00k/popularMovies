package me.danielaguilar.popularmoviesstage1.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import me.danielaguilar.popularmoviesstage1.configurations.Constants;

import static me.danielaguilar.popularmoviesstage1.configurations.Constants.API_BASE_PATH;
import static me.danielaguilar.popularmoviesstage1.configurations.Constants.API_KEY;

/**
 * Created by danielaguilar on 01-11-17.
 */

public class NetworkUtils {

    public static final String PARAM_API_KEY = "api_key";

    public static URL buildUrl(final String orderBy) {
        Uri builtUri = Uri.parse(Constants.API_BASE_URL+API_BASE_PATH+orderBy).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
