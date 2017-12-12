package me.danielaguilar.popularmoviesstage1.data;

import android.provider.BaseColumns;

/**
 * Created by danielaguilar on 10-12-17.
 */

public class MoviesContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME               = "movies";
        public static final String COLUMN_MOVIE_REMOTE_ID   = "remoteId";
        public static final String COLUMN_POSTER            = "poster";
        public static final String COLUMN_OVERVIEW          = "overview";
        public static final String COLUMN_TITLE             = "title";
        public static final String COLUMN_RELEASE_DATE      = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE      = "voteAverage";
    }

    public static final class MovieReviewEntry implements BaseColumns {
        public static final String TABLE_NAME               = "movie_reviews";
        public static final String COLUMN_MOVIE_ID          = "movieId";
        public static final String COLUMN_REMOTE_ID         = "remoteId";
        public static final String COLUMN_AUTHOR            = "author";
        public static final String COLUMN_CONTENT           = "content";
    }

    public static final class MovieTrailerEntry implements BaseColumns {
        public static final String TABLE_NAME               = "movie_trailers";
        public static final String COLUMN_MOVIE_ID          = "movieId";
        public static final String COLUMN_REMOTE_ID         = "remoteId";
        public static final String COLUMN_NAME              = "name";
        public static final String COLUMN_KEY               = "key";
    }
}
