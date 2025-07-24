package com.actia.utilities.utilities_ui;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

public class HideSystemNavBar {

    public static void hideNavigationBar(Activity activity) {

            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION               //API LEVEL 14
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN             //API LEVEL 16
                    | View.SYSTEM_UI_FLAG_FULLSCREEN                    //API LEVEL 16
                    | SYSTEM_UI_FLAG_LAYOUT_STABLE                      //API LEVEL 16
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION        //API LEVEL 16
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );

    }
}
