package me.danielaguilar.popularmoviesstage1.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

/**
 * Created by danielaguilar on 02-11-17.
 */
public class ApiConnector extends AsyncTask<URL, Void, String> {

    public interface OnTaskCompleted<T>{
        void onComplete(T result, String klassId);
    }

    private OnTaskCompleted listener;
    private String klassId;

    public ApiConnector(OnTaskCompleted listener, final String klassId){
        this.listener = listener;
        this.klassId  = klassId;
    }

    @Override
    protected String doInBackground(URL... urls) {
        final URL url = urls[0];
        String searchResults = null;
        try {
            searchResults = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    @Override
    protected void onPostExecute(String searchResults) {
        if (searchResults != null && !searchResults.equals("")) {
            listener.onComplete(searchResults, klassId);
        }
    }
}