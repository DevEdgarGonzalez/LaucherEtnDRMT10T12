package com.actia.utilities.utilities_ui.utilities_dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.mexico.generic_2018_t10_t12.AdvertisingActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class DialogTokensAutomatic {
    public interface OperationsTwoOptions {
        void acept();

        void cancel();

    }

    public interface OperationOneOption {
        void acept();


    }


    /**
     * Crea el dialogo que se mostrara cuando el proceso de validacion de TOKENS termine
     * pinta el fondo del dialogo y el mensaje dependiendo de la catidad de tokens
     *
     * @param activity    pantalla donde se visualizara el dialogo
     * @param tokensOk    can
     * @param totalTokens
     * @return
     */
    public static Dialog createCustomAlertDialogTokens(Activity activity, int tokensOk, int totalTokens) {

        final Context context = activity.getApplicationContext();
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tokens);
        String detailTokens = tokensOk + "/" + totalTokens;

        LinearLayout lytParentDlgTokens = dialog.findViewById(R.id.lytParentDlgTokens);
        TextView lblTitleDlgTokens = dialog.findViewById(R.id.lblTitleDlgTokens);
        TextView lblMsgDlgTokens = dialog.findViewById(R.id.lblMsgDlgTokens);
        Button btnAceptDlgTokens = dialog.findViewById(R.id.btnAceptDlgTokens);

        if (tokensOk >= totalTokens) {
            lytParentDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.dlg_green));
            btnAceptDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.dlg_green));
            lblTitleDlgTokens.setText(context.getResources().getString(R.string.dlg_title_tkns_ok));
            lblMsgDlgTokens.setText(context.getResources().getString(R.string.dlg_msg_tkns_msg_ok) + "  " + detailTokens);

        } else if (tokensOk > 0) {
            lytParentDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.blue));
            btnAceptDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.blue));
            lblTitleDlgTokens.setText(context.getResources().getString(R.string.dlg_title_tkns_incomplete));
            lblMsgDlgTokens.setText(context.getResources().getString(R.string.dlg_msg_tkns_msg_incomplete) + detailTokens);
            closeDialogPostDelay(dialog, activity);
        } else if (tokensOk == 0) {
            lytParentDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.dlg_red));
            btnAceptDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.dlg_red));
            lblTitleDlgTokens.setText(context.getResources().getString(R.string.dlg_title_tkns_error));
            lblMsgDlgTokens.setText(context.getResources().getString(R.string.dlg_msg_tkns_msg_error) + ":   " + detailTokens);
            closeDialogPostDelay(dialog, activity);
        }

        btnAceptDlgTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        return dialog;


    }


    /**
     * Este metodo crea el dialogo al validar el USUARIO
     *
     * @param activity     Pantalla en la que se mostrara el dialogo
     * @param tipeActivity Distintivo para saber que mensaje mostrar
     * @return regresa el dialogo personalizado
     */
    public static Dialog createCustomDlgErrorUser(Activity activity, int tipeActivity) {
        final Context context = activity.getApplicationContext();
        PreferencesApp aexaPreferences = new PreferencesApp(context);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tokens);
        LinearLayout lytParentDlgTokens = dialog.findViewById(R.id.lytParentDlgTokens);
        TextView lblTitleDlgTokens = dialog.findViewById(R.id.lblTitleDlgTokens);
        TextView lblMsgDlgTokens = dialog.findViewById(R.id.lblMsgDlgTokens);
        Button btnAceptDlgTokens = dialog.findViewById(R.id.btnAceptDlgTokens);

        lytParentDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.dlg_red));
        btnAceptDlgTokens.setBackgroundColor(context.getResources().getColor(R.color.dlg_red));
        lblTitleDlgTokens.setText(context.getResources().getString(R.string.dlg_title_User_error));
        if (tipeActivity == ConstantsApp.SCREEN_ADVERSITING) {
            lblMsgDlgTokens.setText(context.getResources().getString(R.string.dlg_msg_user_error) + "  \nIntento " + (aexaPreferences.getValidationErors() + 1) + " de " + ConstantsApp.MAX_ERRORS_KEY_WASABI);
        } else {
            lblMsgDlgTokens.setText(context.getResources().getString(R.string.dlg_msg_user_error));
        }

        btnAceptDlgTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        closeDialogPostDelay(dialog, activity);


        return dialog;


    }


    private static void closeDialogPostDelay(final Dialog dlg, final Activity activity) {
        Handler handler = new Handler();
        final String nameactivity = AdvertisingActivity.class.getSimpleName();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dlg != null && dlg.isShowing()) {
//                    if (activity.getLocalClassName().contains(nameactivity)  && AdvertisingActivity.isShowingThisActivity==false){
                    if (activity instanceof AdvertisingActivity && AdvertisingActivity.isShowingThisActivity == false) {
                        //fun: do nothing
                    } else {

                        dlg.dismiss();
                    }

                }

            }
        }, ConstantsApp.DURATION_SECONDS_DIALOG);
    }


}
