package me.danielaguilar.popularmoviesstage1.utils;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by danielaguilar on 02-11-17.
 */
public class ApiConnector extends AsyncTask<String, Void, String> {

    public interface OnTaskCompleted<T>{
        void onComplete(T result);
    }

    private OnTaskCompleted listener;

    public ApiConnector(OnTaskCompleted listener){
        this.listener = listener;
    }

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
            listener.onComplete(searchResults);
        }
    }
}