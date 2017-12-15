package me.danielaguilar.popularmoviesstage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by danielaguilar on 10-12-17.
 */

public class MoviesContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "me.danielaguilar.popularmoviesstage1";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // This is the path for the "movies" directory
    public static final String PATH_MOVIES      = "movies";
    public static final String PATH_REVIEWS     = "reviews";
    public static final String PATH_TRAILERS    = "trailers";

    public static final class MovieEntry implements BaseColumns {

        // URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME               = "movies";
        public static final String COLUMN_MOVIE_REMOTE_ID   = "remoteId";
        public static final String COLUMN_POSTER            = "poster";
        public static final String COLUMN_OVERVIEW          = "overview";
        public static final String COLUMN_TITLE             = "title";
        public static final String COLUMN_RELEASE_DATE      = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE      = "voteAverage";
    }

    public static final class MovieReviewEntry implements BaseColumns {
        // URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("#").appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME               = "movie_reviews";
        public static final String COLUMN_MOVIE_ID          = "movieId";
        public static final String COLUMN_REMOTE_ID         = "remoteId";
        public static final String COLUMN_AUTHOR            = "author";
        public static final String COLUMN_CONTENT           = "content";
    }

    public static final class MovieTrailerEntry implements BaseColumns {
        // URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("#").appendPath(PATH_TRAILERS).build();

        public static final String TABLE_NAME               = "movie_trailers";
        public static final String COLUMN_MOVIE_ID          = "movieId";
        public static final String COLUMN_REMOTE_ID         = "remoteId";
        public static final String COLUMN_NAME              = "name";
        public static final String COLUMN_KEY               = "key";
    }
}
