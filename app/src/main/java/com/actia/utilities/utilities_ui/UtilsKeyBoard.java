package com.actia.utilities.utilities_ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Edgar Gonzalez on 08/03/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilsKeyBoard {

    public static void showSoftKeyboard(Context context, View view){
        view.requestFocus(); //Asegurar que editText tiene focus
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static  void hideSoftKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);



    }


}
