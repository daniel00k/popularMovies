package me.danielaguilar.popularmoviesstage1.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by danielaguilar on 12-12-17.
 */

public class FontManager {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "FontAwesome.otf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}