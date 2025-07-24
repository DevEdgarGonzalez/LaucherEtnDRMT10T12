package com.actia.utilities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.actia.home_categories.MainActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utils_language.UtilsLanguage;

public class DialogLanguage {
    private static final String TAG = "DialogLanguage";

    private static final String languageEnglish = "en";
    private static final String languageSpanish = "es";

    private final Context context;

    public DialogLanguage(Context context) {
        this.context = context;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewDlg = LayoutInflater.from(context).inflate(R.layout.dlg_language, null, false);

        final View llytEnglish = viewDlg.findViewById(R.id.llytEnglish);
        final View llytSpanish = viewDlg.findViewById(R.id.llytSpanish);

        if(UtilsLanguage.isAppInEnglish()){
            llytEnglish.setSelected(true);
        }else{
            llytSpanish.setSelected(true);
        }

        llytEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage(languageEnglish);
                llytEnglish.setSelected(true);
                llytSpanish.setSelected(false);
            }
        });
        llytSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage(languageSpanish);
                llytEnglish.setSelected(false);
                llytSpanish.setSelected(true);
            }
        });

        builder.setView(viewDlg);
        builder.create().show();

    }


    private void setLanguage(String language) {
        UtilsLanguage.setLanguage(language,context);
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }



}
