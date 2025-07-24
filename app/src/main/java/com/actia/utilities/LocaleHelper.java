package com.actia.utilities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper {

    // the method is used to set the language at runtime
    public static Configuration setLocale(Context context, String language) {
        //persist(context, language);

        Log.i("localeHelper", "setLocale: " + language + " " + context.toString());

        // updating the language for devices above android nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        // for devices having lower version of android os
        return updateResourcesLegacy(context, language);
    }

    /*private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }
*/
    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    @TargetApi(Build.VERSION_CODES.N)
    public static Configuration updateResources(Context context, String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        configuration.locale = locale;
        res.updateConfiguration(configuration, dm);

        Log.i("LocaleHelper", "updateResources: " + configuration.toString());

        return configuration;
    }


    @SuppressWarnings("deprecation")
    private static Configuration updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);

            Log.i("LocaleHelper", "updateResourcesLegacy: " + configuration.toString() + " version:" + Build.VERSION.SDK_INT);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());


        Log.i("LocaleHelper", "updateResourcesLegacy: " + configuration.toString());

        return configuration;
    }
}