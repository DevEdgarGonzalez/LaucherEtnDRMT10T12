package com.actia.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by Edgar Gonzalez on 26/03/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesComunicationServiceActia {



    public static void sendLogBroadcast(Context context){
        Intent intentBroadcast = new Intent();
        Bundle b = new Bundle();
        b.putBoolean("showdialog", false);
        intentBroadcast.putExtra("msg", "sendlog");
        intentBroadcast.putExtras(b);
        intentBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentBroadcast.setAction("com.actia.launcher");
        context.sendBroadcast(intentBroadcast);



    }

    public static void createLogMovieBannersBroadcast(Context context, Bundle bundleWithInfoMoviesAndBanners){
        Intent intentBroadcast = new Intent();
        intentBroadcast.putExtra("msg","log");
        intentBroadcast.putExtras(bundleWithInfoMoviesAndBanners);
        intentBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentBroadcast.setAction("com.actia.launcher");
        context.sendBroadcast(intentBroadcast);
        Log.d("LogMoviesBaseAct","Se crea: "+bundleWithInfoMoviesAndBanners.toString());
    }

    public static  void updateMultimediaBroadcast(Context context){
        Intent intentBoradcast = new Intent();
        intentBoradcast.putExtra("msg", "updatemultimedia");
        intentBoradcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentBoradcast.setAction("com.actia.launcher");
        context.sendBroadcast(intentBoradcast);

    }

    public static  void installApkBroadcast(Context context){
        Intent intentBoradcast = new Intent();
        intentBoradcast.putExtra("msg","installapk");
        intentBoradcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentBoradcast.setAction("com.actia.launcher");
        context.sendBroadcast(intentBoradcast);
    }
}
