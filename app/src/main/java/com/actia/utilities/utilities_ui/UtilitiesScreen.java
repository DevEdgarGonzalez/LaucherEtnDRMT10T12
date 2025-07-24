package com.actia.utilities.utilities_ui;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;


/**
 * Created by Edgar Gonzalez on 15/12/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesScreen {

    /**
     * Tamaños de pantallas
     *
     * Tactil 12 plg
     *      width 		1366
     *      height		768
     *
     * Tactil 10plg
     *      width 		1280
     *      height		800
     *
     * Tactil Basic2
     *      width       1024
     *      height      600
     *
     * Tactil 7 plg
     *      width       1024
     *      height      552
     *
     */


    /**
     * este metodo identifica si es un tactil de 7 plg o de 10 plg
     * guarda en el contexto el tipo de pantalla para que en algunas pantallas las adecue respecto al tamaño de screen
     *
     * @param activity se utiliza para obtener la configuracion de la pantalla
     */
    public static void getTypeScreen(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (width<1050 ){
            ContextApp.TYPE_SCREEN = ConstantsApp.TYPE_SCREEN_7PLG;
        }else if (width>=1050 &&width<1300){
            ContextApp.TYPE_SCREEN = ConstantsApp.TYPE_SCREEN_10PLG;
        }else {
            ContextApp.TYPE_SCREEN = ConstantsApp.TYPE_SCREEN_12PLG;
        }



    }
//    public static int getTypeScreen(){
//
//
//        Display display = getWindowManager().getDefaultDisplay();
//        String displayName = display.getName();  // minSdkVersion=17+
//        Log.i(TAG, "displayName  = " + displayName);
//
//// display size in pixels
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//        Log.i(TAG, "width        = " + width);
//        Log.i(TAG, "height       = " + height);
//
//// pixels, dpi
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int heightPixels = metrics.heightPixels;
//        int widthPixels = metrics.widthPixels;
//        int densityDpi = metrics.densityDpi;
//        float xdpi = metrics.xdpi;
//        float ydpi = metrics.ydpi;
//        Log.i(TAG, "widthPixels  = " + widthPixels);
//        Log.i(TAG, "heightPixels = " + heightPixels);
//        Log.i(TAG, "densityDpi   = " + densityDpi);
//        Log.i(TAG, "xdpi         = " + xdpi);
//        Log.i(TAG, "ydpi         = " + ydpi);
//
//// deprecated
//        int screenHeight = display.getHeight();
//        int screenWidth = display.getWidth();
//        Log.i(TAG, "screenHeight = " + screenHeight);
//        Log.i(TAG, "screenWidth  = " + screenWidth);
//
//// orientation (either ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT)
//        int orientation = getResources().getConfiguration().orientation;
//        Log.i(TAG, "orientation  = " + orientation);
//
//
//
//
//
//    }


}
