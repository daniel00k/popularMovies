package me.danielaguilar.popularmoviesstage1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.models.Movie;
import me.danielaguilar.popularmoviesstage1.models.MovieReview;
import me.danielaguilar.popularmoviesstage1.models.MovieTrailer;

/**
 * Created by danielaguilar on 10-12-17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "movies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createMoviesTable(sqLiteDatabase);
        createMovieReviewsTable(sqLiteDatabase);
        createMovieTrailersTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Not sure about this
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieTrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void createMoviesTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    public void createMovieReviewsTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_MOVIE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.MovieReviewEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.MovieReviewEntry.COLUMN_CONTENT+ " TEXT NOT NULL, " +
                MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
    }

    public void createMovieTrailersTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_MOVIE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.MovieTrailerEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieTrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MoviesContract.MovieTrailerEntry.COLUMN_NAME+ " TEXT NOT NULL, " +
                MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TRAILERS_TABLE);
    }
/*
    public static long insertMovie(SQLiteDatabase db,
                                  final Movie movie,
                                  final ArrayList<MovieTrailer> trailers,
                                  final ArrayList<MovieReview> reviews){
        long movieId = -1;
        if(db == null){
            return movieId;
        }

        ContentValues movieCV = new ContentValues();
        movieCV.put(MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID, movie.getId());
        movieCV.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        movieCV.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getPoster());
        movieCV.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieCV.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        movieCV.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

        try
        {
            db.beginTransaction();
            movieId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, movieCV);
            insertReviews(db, reviews, movie.getId());
            insertTrailers(db, trailers, movie.getId());
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }finally {
            db.endTransaction();
        }

        return movieId;
    }

    private static long insertTrailers(SQLiteDatabase db,
                                final ArrayList<MovieTrailer> trailers,
                                final long movieId){
        try
        {
            for (MovieTrailer trailer:trailers) {
                ContentValues trailerCV = new ContentValues();
                trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID, movieId);
                trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_KEY, trailer.getKey());
                trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_NAME, trailer.getName());
                trailerCV.put(MoviesContract.MovieTrailerEntry.COLUMN_REMOTE_ID, trailer.getId());
                db.insert(MoviesContract.MovieTrailerEntry.TABLE_NAME, null, trailerCV);
            }
        }
        catch (SQLException e) {
            //too bad :(
        }
        return 0;
    }

    private static long insertReviews(SQLiteDatabase db,
                               final ArrayList<MovieReview> reviews,
                               final long movieId){
        try
        {
            for (MovieReview review:reviews) {
                ContentValues reviewCV = new ContentValues();
                reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_CONTENT, review.getContent());
                reviewCV.put(MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID, review.getId());
                db.insert(MoviesContract.MovieReviewEntry.TABLE_NAME, null, reviewCV);
            }
        }
        catch (SQLException e) {
            //too bad :(
        }
        return 0;
    }

    public static Movie findMovie(SQLiteDatabase db, String id) {

        Movie movie = null;
        try
        {
            db.beginTransaction();

            Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                    new String[] {  MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID,
                            MoviesContract.MovieEntry.COLUMN_POSTER,
                            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
                            MoviesContract.MovieEntry.COLUMN_TITLE,
                            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE},
                    MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID + "=?",
                    new String[] { id },
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.getCount()>0){
                cursor.moveToFirst();
                movie = new Movie(  Integer.parseInt(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID))),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE))));

            }

            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }finally {
            db.endTransaction();
        }

        return movie;
    }

    public static boolean deleteMovie(SQLiteDatabase db, Movie movie){
        return db.delete(MoviesContract.MovieEntry.TABLE_NAME, MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID + "=" + movie.getId(), null) > 0
                && db.delete(MoviesContract.MovieReviewEntry.TABLE_NAME, MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID + "=" + movie.getId(), null) > 0
                && db.delete(MoviesContract.MovieTrailerEntry.TABLE_NAME, MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID + "=" + movie.getId(), null) > 0;
    }

    public static ArrayList<Movie> findAllMovies(SQLiteDatabase db) {

        ArrayList<Movie> movies = new ArrayList<>();
        try
        {
            db.beginTransaction();

            Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                    new String[] {  MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID,
                            MoviesContract.MovieEntry.COLUMN_POSTER,
                            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
                            MoviesContract.MovieEntry.COLUMN_TITLE,
                            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.getCount()>0){
                if (cursor.moveToFirst()) {
                    do {
                        Movie movie = new Movie(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_REMOTE_ID))),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE))));
                        movies.add(movie);
                    } while (cursor.moveToNext());
                }
            }

            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }finally {
            db.endTransaction();
        }

        return movies;
    }

    public static ArrayList<MovieReview> findMovieReviews(SQLiteDatabase db, Movie movie) {

        ArrayList<MovieReview> reviews = new ArrayList<>();
        try
        {
            db.beginTransaction();

            Cursor cursor = db.query(MoviesContract.MovieReviewEntry.TABLE_NAME,
                    new String[] {  MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID,
                            MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID,
                            MoviesContract.MovieReviewEntry.COLUMN_CONTENT,
                            MoviesContract.MovieReviewEntry.COLUMN_AUTHOR},
                    MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID + "=?",
                    new String[] { String.valueOf(movie.getId())},
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.getCount()>0){
                if (cursor.moveToFirst()) {
                    do {
                        MovieReview movieReview = new MovieReview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_REMOTE_ID)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_AUTHOR)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_CONTENT)));
                        reviews.add(movieReview);
                    } while (cursor.moveToNext());
                }
            }

            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }finally {
            db.endTransaction();
        }

        return reviews;
    }

    public static ArrayList<MovieTrailer> findMovieTrailers(SQLiteDatabase db, Movie movie) {

        ArrayList<MovieTrailer> trailers = new ArrayList<>();
        try
        {
            db.beginTransaction();

            Cursor cursor = db.query(MoviesContract.MovieTrailerEntry.TABLE_NAME,
                    new String[] {  MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID,
                            MoviesContract.MovieTrailerEntry.COLUMN_REMOTE_ID,
                            MoviesContract.MovieTrailerEntry.COLUMN_NAME,
                            MoviesContract.MovieTrailerEntry.COLUMN_KEY},
                    MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID + "=?",
                    new String[] { String.valueOf(movie.getId())},
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.getCount()>0){
                if (cursor.moveToFirst()) {
                    do {
                        MovieTrailer movieTrailer = new MovieTrailer(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_REMOTE_ID)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_KEY)));
                        trailers.add(movieTrailer);
                    } while (cursor.moveToNext());
                }
            }

            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }finally {
            db.endTransaction();
        }

        return trailers;
    }*/

}
