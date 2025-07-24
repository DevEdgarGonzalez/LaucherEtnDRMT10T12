package com.actia.drm.auto_tokens;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.mexico.generic_2018_t10_t12.AdvertisingActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_logs_in_sd.FileWritteLogErrorToken;
import com.actia.utilities.utilities_ui.utilities_dialogs.DialogTokensAutomatic;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UserRegisterTsk extends AsyncTask<Object, Object, Boolean> {
    private Context context = null;
    private ConfigMasterMGC Config;
    private final String TAG = "AsyncRegisterUser";
    private String pathUser = null;
    private final int idUser;
    private final File[] tokens;
    private ProgressDialog progressDialog;
    private TokenRegisterTsk tskAsyncTokensAutomatic;
    private final int tipeActivity;
    PreferencesApp aexaPreferences;


    public UserRegisterTsk(Context context, String pathUser, int idUser, File[] tokens, int tipeActivity) {
        this.context = context;
        this.pathUser = pathUser;
        this.idUser = idUser;
        this.tokens = tokens;
        this.tipeActivity =tipeActivity;
        this.aexaPreferences =  new PreferencesApp(context);
    }

    @Override
    protected void onPreExecute() {

        if (ConstantsApp.isRunningTskTokens == false) {       //Crea dialogo en caso de que la bandera indique que NO hay un tsk  ejecutando
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.dialog_key_token_msg));
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cancel(true);           //La bandera "ConstantesETN.isRunningTskTokens" se queda en true para que no se vuelva a lanzar el servicio

                }
            });




            if (tipeActivity==ConstantsApp.SCREEN_ADVERSITING && AdvertisingActivity.isShowingThisActivity==false){
                //no mostrar dialogo en pantalla de advertising cuando no esta activa
            }else{
                if (isCancelled()==false) {
                    progressDialog.show();
                }
            }


        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        if (ConstantsApp.isRunningTskTokens == true) {
            return null;        //Corta este proceso en caso de que la bandera indique que un tsk esta ejecutando
        }

        ConstantsApp.isRunningTskTokens = true;
        boolean isTheProcessCorrect = false;
        Config = ConfigMasterMGC.getConfigSingleton();
        File userToken = new File(pathUser);

        if (userToken.exists() && userToken.length() > 0) {

            try {
                //Runtime.setProperty(Property.ROOTED_OK,true);
                Runtime.initialize(this.context.getApplicationContext().getDir("wasabi", Context.MODE_PRIVATE)
                        .getAbsolutePath());

                if (!Runtime.isPersonalized())
                    Runtime.personalize();

                String licenseAcquisitionToken = getTokenFromSDCard();

                if (licenseAcquisitionToken == null) {
                    Log.e(TAG, "Could not find action token in the assets directory - exiting");
                    writteInLog("\r" + "Error KeyUser:" + "\t\t" + "token from SDcard null");
                    return isTheProcessCorrect;
                }

                long start = System.currentTimeMillis();
                Runtime.processServiceToken(licenseAcquisitionToken);
                Log.i(TAG, "License successfully acquired in (ms): " + (System.currentTimeMillis() - start));
                isTheProcessCorrect = true;                                                                 //Process correct
                aexaPreferences.setValidationErors(0);


            } catch (NullPointerException e) {
                sendLog(e.getLocalizedMessage(), e.getMessage());
                return isTheProcessCorrect;
            } catch (ErrorCodeException e) {
                sendLog(e.getLocalizedMessage(), e.getMessage());
                return isTheProcessCorrect;
            } catch (Exception e) {
                sendLog(e.getLocalizedMessage(), e.getMessage());
                return isTheProcessCorrect;
            }
        } else {
            Log.e(TAG, "Error KeyUser:"
                    + "Llave no existe");
            writteInLog("\r" + "Error KeyUser:" + "\t\t" + "Llave no existe");

        }
        return isTheProcessCorrect;
    }



    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (tipeActivity==ConstantsApp.SCREEN_ADVERSITING &&AdvertisingActivity.isShowingThisActivity==false){
            return;
        }
        closeDialog();

        if (result == true) {
            ContextApp.statusKey= "Ok";

            tskAsyncTokensAutomatic = new TokenRegisterTsk(context, tokens,tipeActivity);                                //Si la llave se valido bien: se inicia el proceso de validacion de tokens sin cambiar el estatus de la bandera
            tskAsyncTokensAutomatic.execute();
        } else {


            Dialog dialog = DialogTokensAutomatic.createCustomDlgErrorUser((Activity) context,tipeActivity);      //Si no se valido la llave se muestra un Mensaje de error
            dialog.setCancelable(true);

            if (tipeActivity==ConstantsApp.SCREEN_ADVERSITING &&AdvertisingActivity.isShowingThisActivity==false){
                //no mostrar dialogo en pantalla de advertising cuando no esta activa
            }else{
                dialog.show();
            }

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ConstantsApp.isRunningTskTokens = false;
                    aexaPreferences.setValidationErors(aexaPreferences.getValidationErors()+1);
                }
            });
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        closeDialog();
        if (tskAsyncTokensAutomatic != null) {
            if (tskAsyncTokensAutomatic.getStatus().equals(Status.RUNNING)) {
                tskAsyncTokensAutomatic.cancel(true);
            }
        }
    }

    protected String getTokenFromSDCard() {
        BufferedReader myReader = null;
        try {
            File myFile = new File(Config.getPathUserDRM());
            FileInputStream fIn = new FileInputStream(myFile);
            myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
            Log.i(TAG, aBuffer);
            return aBuffer;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (myReader != null) {
                try {
                    myReader.close();
                    myReader = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void closeDialog() {
        if (progressDialog.isShowing()) {
            if (tipeActivity==ConstantsApp.SCREEN_ADVERSITING&&AdvertisingActivity.isShowingThisActivity==false){
                return;
            }
            progressDialog.dismiss();
        }
    }


    private void sendLog(String localizedMessage, String message) {
        Log.e(TAG, "runtime initialization or personalization error: "
                + localizedMessage);
        writteInLog("\r" + "Error KeyUser:" + "\t\t" + message);
    }

    private void writteInLog(String error) {
        FileWritteLogErrorToken.writteInLog(error, context);
    }




}
