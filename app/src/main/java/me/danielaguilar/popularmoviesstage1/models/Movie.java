package me.danielaguilar.popularmoviesstage1.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by danielaguilar on 01-11-17.
 */

public class Movie implements Parcelable{

    private int id;
    private String poster;
    private String overview;
    private String title;
    private String releaseDate;
    private float voteAverage;


    public Movie(int id, String poster, String overview, String title, String releaseDate, float voteAverage){
        this.id             = id;
        this.poster         = poster;
        this.overview       = overview;
        this.title          = title;
        this.releaseDate    = releaseDate;
        this.voteAverage    = voteAverage;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        overview = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(poster);
        parcel.writeString(overview);
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeFloat(voteAverage);
    }
}
