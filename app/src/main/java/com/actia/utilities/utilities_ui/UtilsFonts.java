package com.actia.utilities.utilities_ui;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Edgar Gonzalez on 08/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilsFonts {

    static String fontGaretBook = "fonts/Garet-Book.otf";
    static String fontGaretHeavy = "fonts/Garet-Heavy.otf";
    static String fontGaretBold = "fonts/Garet Bold.ttf";
    static String fontGaretRegular = "fonts/Garet Regular.ttf";




    //FONTS MAIN ACTIVITY
    public static Typeface getTypefaceMainActivity(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontGaretBold);
    }

    //FONTS MUSIC
    public static Typeface getTypefaceCategoryMusic(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontGaretBold);
    }

    public static Typeface getTypefacePlayMusic(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontGaretRegular);
    }
    public static Typeface getTypefaceTextNormal(Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontGaretBold);
    }
}
