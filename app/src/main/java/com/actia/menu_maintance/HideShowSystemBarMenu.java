package com.actia.menu_maintance;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.actia.infraestructure.ContextApp;
import com.actia.mexico.generic_2018_t10_t12.LoadSDCardActivity;
import com.actia.utilities.utilities_root.UtilsRoot;

/**
 * Created by Edgar Gonzalez on 11/05/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class HideShowSystemBarMenu {


    public static void setStatusBar(boolean isChecked){
        if (ContextApp.deviceIsRoot) {
            if (isChecked) {
                showSystemBarRoot();
            } else {
                hideSystemBarRoot();
            }
        } else {
//            if (isChecked){
//                unlockSystemBar();
//        }     else{
//            unlockSystemBar();
            lockSystemBar();
//        }

        }
    }


    //********** unlock and lock system bar **********
    private static void unlockSystemBar() {
//      dialogmaintenance.dismiss();

        Handler handler = new Handler(Looper.getMainLooper(), LoadSDCardActivity.handler);
//         UICallback hand = MainActivity.handler;4
        Message msgEnd = new Message();
        msgEnd.obj = "Abriendo";
        msgEnd.what = 0;
        handler.sendMessage(msgEnd);

    }

    private static void lockSystemBar() {
//      dialogmaintenance.dismiss();

        Handler handler = new Handler(Looper.getMainLooper(), LoadSDCardActivity.handler);
//         Handler hand = MainActivity.handler;
        Message msgEnd = new Message();
        msgEnd.obj = "Cerrando";
        msgEnd.what = 1;

        handler.sendMessage(msgEnd);
    }

    /**
     * Show system bar
     */
    private static void showSystemBarRoot() {
        String commandStr = "am startservice -n com.android.systemui/.SystemUIService";
        UtilsRoot.runAsRoot(commandStr);
    }


    /**
     * Hide system bar
     */
    private static void hideSystemBarRoot() {
        try {
            //REQUIRES ROOT
//			Build.VERSION_CODES vc = new Build.VERSION_CODES();
//			Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; //HONEYCOMB AND OLDER

            //v.RELEASE  //4.0.3
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }

            String commandStr = "service call activity " +
                    ProcID + " s16 com.android.systemui";
            UtilsRoot.runAsRoot(commandStr);
        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }


}
