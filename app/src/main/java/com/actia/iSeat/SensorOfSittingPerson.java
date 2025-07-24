package com.actia.iSeat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.actia.infraestructure.shared_preferences.PreferencesApp;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Edgar Gonzalez on 27/11/2018.
 * Actia de Mexico S.A. de C.V..
 */
public class SensorOfSittingPerson {
    private final String TAG = "SeatLightControlLog";
    private final Context context;
    private final PowerManager mPowerManager;
    private final PreferencesApp preferencesApp;

    private final int minMillisSleep = 300;
    private final int defaultMillisSleep = 60000;
    private boolean isPassengerSitting = true;


    public SensorOfSittingPerson(Context context) {
        this.context = context;
        preferencesApp = new PreferencesApp(context);
        this.mPowerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        int millisSystemSleep = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defaultMillisSleep);
        int millisPreferences = preferencesApp.getMillisTurnOf();

        if (millisSystemSleep != minMillisSleep && millisSystemSleep != defaultMillisSleep) {
            preferencesApp.setMillisTurnOff(millisSystemSleep);
        }
        if (millisSystemSleep==minMillisSleep){
            setOriginalTimeTurnOff();
        }
    }


    private void turnOffDevice() {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, minMillisSleep);
    }


    private void setOriginalTimeTurnOff() {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, preferencesApp.getMillisTurnOf());
    }


    private void unlockScreen() {
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wl = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        wl.acquire();
        if (wl.isHeld()) {
            wl.release();
        }
//        PowerManager.WakeLock w2 = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");
//        w2.acquire();
        Log.d(TAG, "unlockScreen: ");
    }


    public void applyActionSafetyBelt(boolean isSafetyBeltPlaced) {

        Log.d(TAG, "isSafetyBeltPlaced: " + isSafetyBeltPlaced);

        //fun: valida si se cambio el tiempo de forma manual a traves de settings android (se debe de incluir en menu mantenimiento)
        int current = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defaultMillisSleep);
        if (current > minMillisSleep && current != preferencesApp.getMillisTurnOf()) {
            preferencesApp.setMillisTurnOff(current);
        }

        if (isPassengerSitting==isSafetyBeltPlaced){
            return;
        }else{
            isPassengerSitting=isSafetyBeltPlaced;
        }


        if (isSafetyBeltPlaced) {
            setOriginalTimeTurnOff();
            unlockScreen();
            Log.d(TAG, "Action: unlock");
        } else {
            turnOffDevice();
            Log.d(TAG, "Actiom: turnoff: ");
        }

        Log.d(TAG, "currentTimeTurnOff: " + Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defaultMillisSleep));
    }


}

