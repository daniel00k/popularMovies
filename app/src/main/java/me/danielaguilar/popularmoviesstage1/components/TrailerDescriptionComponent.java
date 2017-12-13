package me.danielaguilar.popularmoviesstage1.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.danielaguilar.popularmoviesstage1.R;

/**
 * Created by danielaguilar on 10-12-17.
 */

public class TrailerDescriptionComponent extends LinearLayout implements View.OnClickListener{
    private TextView watchTrailer;
    private TextView trailerName;
    private ImageView shareTrailer;
    private String link;

    public TrailerDescriptionComponent(Context context) {
        super(context);
        init();
    }

    public TrailerDescriptionComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrailerDescriptionComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.trailer_description_component, this, true);
        watchTrailer    = findViewById(R.id.watch_trailer);
        trailerName     = findViewById(R.id.trailer_name);
        shareTrailer    = findViewById(R.id.share_trailer);
        watchTrailer.setOnClickListener(this);
        shareTrailer.setOnClickListener(this);
    }

    public void setNameAndLink(final String link, final String text){
        this.link = link;
        trailerName.setText(text);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(watchTrailer)){
            //Launch intent
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            getContext().startActivity(intent);
        }else if(view.equals(shareTrailer)){
            Intent shareIntent = ShareCompat.IntentBuilder.from((Activity) getContext())
                    .setType("text/plain")
                    .setText(link)
                    .getIntent();
            getContext().startActivity(shareIntent);
        }
    }
}
