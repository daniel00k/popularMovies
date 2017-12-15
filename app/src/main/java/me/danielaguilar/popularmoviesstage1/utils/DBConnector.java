package me.danielaguilar.popularmoviesstage1.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by danielaguilar on 15-12-17.
 */

public class DBConnector implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private DBConnectorListener mCallback;
    private String mCallerId;
    private int mloaderId;

    private Uri uri;
    private String[] projection;
    private String selection;
    private String[] selectionArgs;
    private String sortOrder;

    public interface DBConnectorListener<T>{
        void onLoadComplete(T response, String callerId);
    }

    public DBConnector(Context context, DBConnectorListener callback, String callerId, int loaderId, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        mCallback           =   callback;
        mContext            =   context;
        mCallerId           =   callerId;
        mloaderId           =   loaderId;
        this.uri            =   uri;
        this.projection     =   projection;
        this.selection      =   selection;
        this.selectionArgs  =   selectionArgs;
        this.sortOrder      =   sortOrder;
    }

    public void execute(){
        ((FragmentActivity)mContext).getSupportLoaderManager().initLoader(mloaderId, null, this);
    }

    public void restart(){
        ((FragmentActivity)mContext).getSupportLoaderManager().restartLoader(mloaderId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(mContext) {

            Cursor mMovieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load

                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return mContext.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

                } catch (Exception e) {
                    Log.e("TAG", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallback.onLoadComplete(data, mCallerId);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
