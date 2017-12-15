package me.danielaguilar.popularmoviesstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by danielaguilar on 14-12-17.
 */

public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES          = 100;
    public static final int MOVIE_WITH_ID   = 101;
    public static final int REVIEWS         = 200;
    public static final int TRAILERS        = 300;

    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    protected MoviesDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MoviesDbHelper(context);
        return true;
    }

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#/"+MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#/"+MoviesContract.PATH_TRAILERS, TRAILERS);

        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case MOVIES:
                retCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                retCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID + "=?",
                        new String[] { id },
                        null,
                        null,
                        sortOrder,
                        "1");
                break;
            case REVIEWS:
                String movieId = uri.getPathSegments().get(1);
                retCursor = db.query(MoviesContract.MovieReviewEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID+ "=?",
                        new String[] { movieId },
                        null,
                        null,
                        sortOrder);
                break;
            case TRAILERS:
                String mId = uri.getPathSegments().get(1);
                retCursor = db.query(MoviesContract.MovieTrailerEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID+ "=?",
                        new String[] { mId },
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        long id=-1;
        switch (match) {
            case MOVIES:
                // Insert new values into the database
                // Inserting values into tasks table
                id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case REVIEWS:
                // Insert new values into the database
                // Inserting values into tasks table
                id = db.insert(MoviesContract.MovieReviewEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieReviewEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TRAILERS:
                // Insert new values into the database
                // Inserting values into tasks table
                id = db.insert(MoviesContract.MovieTrailerEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieTrailerEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        // COMPLETED (1) Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int movieDeleted; // starts as 0

        // COMPLETED (2) Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case MOVIE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                movieDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID+"=?", new String[]{id});
                db.delete(MoviesContract.MovieTrailerEntry.TABLE_NAME,MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID+"=?", new String[]{id});
                db.delete(MoviesContract.MovieReviewEntry.TABLE_NAME,MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID+"=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // COMPLETED (3) Notify the resolver of a change and return the number of items deleted
        if (movieDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
