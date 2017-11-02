package me.danielaguilar.popularmoviesstage1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.R;
import me.danielaguilar.popularmoviesstage1.models.Movie;

import static me.danielaguilar.popularmoviesstage1.configurations.Constants.IMAGES_BASE_URL;

/**
 * Created by danielaguilar on 01-11-17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    public interface OnMovieClickListener{
        void onMovieClicked(Movie movie);
    }

    private int size;
    private ArrayList<Movie> movies;
    private Context context;
    private OnMovieClickListener listener;

    public MoviesAdapter(ArrayList<Movie> movies, Context context, OnMovieClickListener listener){
        this.size       = movies.size();
        this.movies     = movies;
        this.context    = context;
        this.listener   = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_movies_item, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position).getId(), context);

    }


    @Override
    public int getItemCount() {
        return size;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView poster;
        private TextView number;

        public MovieViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.grid_image);
            number = itemView.findViewById(R.id.grid_item_number);
            itemView.setOnClickListener(this);
        }

        public void bind(int position, Context context){
            number.setText(position+"");
            Picasso.with(context).load(IMAGES_BASE_URL+movies.get(getAdapterPosition()).getPoster()).into(poster);

        }

        @Override
        public void onClick(View view) {
            listener.onMovieClicked(movies.get(getAdapterPosition()));
        }
    }
}
