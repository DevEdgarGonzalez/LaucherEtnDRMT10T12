package com.actia.utilities.utilities_ui;

import android.content.Context;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

/**
 * Created by Edgar Gonzalez on 16/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesCardView {
    public static void setTransparentBackgroundCardView(CardView cardView, Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            cardView.getBackground().setAlpha(0);
        }else
        {
            cardView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }




}
