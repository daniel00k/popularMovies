package me.danielaguilar.popularmoviesstage1.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

import me.danielaguilar.popularmoviesstage1.utils.NetworkUtils;

/**
 * Created by danielaguilar on 10-12-17.
 */

public class MovieTrailer implements Parcelable{
    private String id;
    private String name;
    private String key;
    private URL url;

    public MovieTrailer(String id, String name, String key){
        this.id     =   id;
        this.name   =   name;
        this.key    =   key;
        this.url    =   NetworkUtils.buildUrlForYoutube(key);
    }

    protected MovieTrailer(Parcel in) {
        id = in.readString();
        name = in.readString();
        key = in.readString();
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(key);
    }
}
