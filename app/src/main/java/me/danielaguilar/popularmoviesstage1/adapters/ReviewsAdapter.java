package me.danielaguilar.popularmoviesstage1.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.danielaguilar.popularmoviesstage1.R;
import me.danielaguilar.popularmoviesstage1.models.MovieReview;

/**
 * Created by danielaguilar on 10-12-17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private int size;
    private ArrayList<MovieReview> reviews;

    public ReviewsAdapter(final ArrayList<MovieReview> reviews){
        this.size       = reviews.size();
        this.reviews    = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ReviewViewHolder extends ViewHolder{

        private TextView author;
        private TextView content;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            author  = itemView.findViewById(R.id.review_author);
            content = itemView.findViewById(R.id.review_content);
        }

        public void bind(MovieReview review){
            author.setText(review.getAuthor());
            content.setText(review.getContent());
        }
    }
}
