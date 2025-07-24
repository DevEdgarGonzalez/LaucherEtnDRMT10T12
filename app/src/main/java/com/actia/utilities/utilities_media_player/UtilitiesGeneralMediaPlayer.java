package com.actia.utilities.utilities_media_player;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.actia.music.MyMediaPlayerService;

/**
 * Created by Edgar Gonzalez on 17/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesGeneralMediaPlayer {

    public static void stopMyMediaPlayerService(Context context){
        if(isMyMediaPlayerServiceRunning(context)){
            Intent intent = new Intent(context, MyMediaPlayerService.class);
            intent.addCategory(MyMediaPlayerService.TAG);
            context.stopService(intent);
        }
    }


    /**
     * Check if media player service is running
     * @return true if media player service is running
     */
    public static boolean isMyMediaPlayerServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyMediaPlayerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
