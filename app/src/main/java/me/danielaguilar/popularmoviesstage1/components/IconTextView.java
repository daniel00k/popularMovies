package me.danielaguilar.popularmoviesstage1.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import me.danielaguilar.popularmoviesstage1.utils.FontManager;

/**
 * Created by danielaguilar on 12-12-17.
 */

public class IconTextView extends android.support.v7.widget.AppCompatTextView {

    private Context context;

    public IconTextView(Context context) {
        super(context);
        this.context = context;
        createView();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    private void createView(){
        setGravity(Gravity.CENTER);
        setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));
    }
}