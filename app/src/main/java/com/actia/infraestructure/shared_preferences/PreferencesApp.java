package com.actia.infraestructure.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class PreferencesApp {
    private final String ARG_NAME_SHARED_PREFERENCES = "Generic2018_Preferences";

    private final String ARG_DATE_MODIFICATIONS = "dateMod";
    private final String ARG_ERRORS = "numberErrors";
    private final String ARG_SHOW_SYSTEM_BAR = "showSystemBar";
    private final String ARG_TIME_TURN_OFF = "timeTurnOff";


    SharedPreferences sharedPreferences;


    public PreferencesApp(Context context) {
        sharedPreferences = context.getSharedPreferences(ARG_NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getModificationDateXml() {
        return sharedPreferences.getString(ARG_DATE_MODIFICATIONS, "");
    }

    public void setModificationDateXml(String modificationDateXml) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ARG_DATE_MODIFICATIONS, modificationDateXml);
        editor.commit();
    }

    public int getValidationErors() {
        return sharedPreferences.getInt(ARG_ERRORS, 0);
    }

    public void setValidationErors(int validationErors) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ARG_ERRORS, validationErors);
        editor.commit();
    }

    public boolean getIsShowingSystemBar() {
        return sharedPreferences.getBoolean(ARG_SHOW_SYSTEM_BAR, false);
    }

    public void setIsShowingSystemBar(boolean showSystemBar) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ARG_SHOW_SYSTEM_BAR, showSystemBar);
        editor.commit();
    }

    public boolean existStatusSystemBarInShared() {
        return sharedPreferences.contains(ARG_SHOW_SYSTEM_BAR);
    }



    public void setMillisTurnOff(int millis){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ARG_TIME_TURN_OFF, millis);
        editor.commit();
    }


    public int getMillisTurnOf (){
        return sharedPreferences.getInt(ARG_TIME_TURN_OFF,60000);
    }



}
