package com.actia.utilities;

import android.app.Activity;
import android.content.Intent;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;

public class UtilitiesOpenExternalApps {


    public static void openQuizzFromVideoInit(Activity activity, ConfigMasterMGC singletonConfig, boolean destroyActivity) {
        if (ConstantsApp.enableQuizzInVideoInt) {
            openQuizz(activity,singletonConfig,destroyActivity);
        }
    }

    public static void openQuizz(Activity activity, ConfigMasterMGC singletonConfig, boolean destroyActivity) {

        try {
            Intent quiz = new Intent(Intent.ACTION_VIEW);
            quiz.addCategory(Intent.CATEGORY_LAUNCHER);
            quiz.setClassName(singletonConfig.getApkQuizPackage(), singletonConfig.getApkQuizClass());
            new AsyncIntents(activity, destroyActivity).execute(quiz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
