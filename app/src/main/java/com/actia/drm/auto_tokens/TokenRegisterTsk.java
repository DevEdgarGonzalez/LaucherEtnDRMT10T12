package com.actia.drm.auto_tokens;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.actia.drm.SettingsActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.generic_2018_t10_t12.AdvertisingActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_date.UtilsDate;
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

public class TokenRegisterTsk extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "Tokens";
    private Context context = null;
    private File[] tokens = null;
    ProgressDialog progressDialog;
    int contadorTokens = 0;
    int contTokensOk = 0;
    int tipeActivity;
    TokensMoviesCRUD tokensMoviesCRUD;
    Handler handlerValidateNewTokens ;
    TokensAutomaticLogic tokensAutomaticLogic;


    public TokenRegisterTsk(Context context, File[] tokens, int tipeActivity) {
        this.context = context;
        this.tokens = tokens;
        this.tipeActivity = tipeActivity;
        tokensMoviesCRUD = TokensMoviesCRUD.getInstance(context);
        tokensAutomaticLogic = new TokensAutomaticLogic(context, tipeActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(context.getString(R.string.dialog_token_validate_msg));
        progressDialog.setCancelable(false);
        progressDialog.setProgressNumberFormat("%1d/%2d ");
        progressDialog.setMax(tokens.length);
        progressDialog.setProgress(0);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancel(true);           //La bandera "ConstantesETN.isRunningTskTokens" se queda en true para que no se vuelva a lanzar el servicio
            }
        });


        if (tipeActivity== ConstantsApp.SCREEN_ADVERSITING && AdvertisingActivity.isShowingThisActivity==false){
            //no mostrar dialogo en pantalla de advertising cuando no esta activa
        }else{
            progressDialog.show();
        }

    }

    @Override
    protected Void doInBackground(Void... params) {




        try {
            Runtime.initialize(this.context.getDir("wasabi", Context.MODE_PRIVATE)
                    .getAbsolutePath());

            if (!Runtime.isPersonalized())
                Runtime.personalize();

            Log.i(TAG, "Inicia la validacion de tokens");

        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.i(TAG, "Error: " + e.getMessage());
            return null;
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            Log.i(TAG, "Error: " + e.getMessage());
            return null;
        }

        for (File f : this.tokens) {

            if (!isCancelled()) {

                String licenseAcquisitionToken = getTokenFromSDCard(f.getPath());

                if (licenseAcquisitionToken == null) {

                    return null;
                }

//FIXME
                TokenMovie currentTokenToSend = tokensMoviesCRUD.readOneTokensMovies(f.getName());

                if (currentTokenToSend != null) {
                    currentTokenToSend.setTokenLastUpdateAttemp(UtilsDate.getDatecomplete());

                    if (currentTokenToSend.getRegistrationStatus() != ConstantsApp.OPC_TOKEN_NOT_REGISTERED) {
                        continue;  //Si esta con un estatus diferenete de "No Registrado" pasa al siguiente elemento del arreglo "tokens"
                    }
                } else {
                    currentTokenToSend = new TokenMovie(
                            0,                                              //int id;
                            f.getName(),                                        //String titleMovie
                            ConstantsApp.OPC_TOKEN_NOT_REGISTERED,             //int tokenRegister
                            0,                                    //int numberOfErrors
                            "",                                 //String xmlLastDateUpdate
                            UtilsDate.getDatecomplete()                         //tokenLastUpdateAttemp
                    );
                }
//FIXME

                try {
                    Runtime.processServiceToken(licenseAcquisitionToken);           //envia path de token
                    Log.i(TAG, "pelucula autorizada: " + f.getPath());

                    currentTokenToSend.setRegistrationStatus(ConstantsApp.OPC_TOKEN_OK);
                    tokensMoviesCRUD.updateTokensMovies(currentTokenToSend);
                    contTokensOk++;

                    publishProgress(++contadorTokens);
                    sleep(300);

                } catch (ErrorCodeException e1) {

                    Log.i(TAG, "movie Denegada: " + f.getPath());

                    publishProgress(++contadorTokens);
                    int errors = currentTokenToSend.getNumberOfErrors() + 1;
                    currentTokenToSend.setNumberOfErrors(errors);
                    FileWritteLogErrorToken.writteInLog("\r" + f.getName() + "\t\t" + e1.getMessage(), context);
                    if (errors >= 5) {
                        currentTokenToSend.setRegistrationStatus(ConstantsApp.OPC_TOKEN_WITH_ERROR);
                    }
                    tokensMoviesCRUD.updateTokensMovies(currentTokenToSend);
                    continue;

                }



            } else {
                Log.i(TAG, "Stop for and validate licens");
                SettingsActivity.exitFlag = true;
                break;
            }
        }
        return null;
    }



    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        closeDialogProgress();
        final Dialog dlgTokensSummary = DialogTokensAutomatic.createCustomAlertDialogTokens((Activity) context, contTokensOk, tokens.length);
        try{

            if (tipeActivity==ConstantsApp.SCREEN_ADVERSITING&&AdvertisingActivity.isShowingThisActivity==false){
                //no mostrar dialogo en pantalla de advertising cuando no esta activa
            }else{
                dlgTokensSummary.show();
            }



            if (contTokensOk >= tokens.length) {
                handlerValidateNewTokens = new Handler();
                handlerValidateNewTokens.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        tokensAutomaticLogic.detectNewTokens();
                        int numberRecordRest = tokensMoviesCRUD.readMoviesByStatus(ConstantsApp.OPC_TOKEN_NOT_REGISTERED).length;
                        if (numberRecordRest>0){        //Si hay nuevos tokens se cierra el dialogo sin cambiar la bandera para que en N segundos inicie el proceso
                            if (dlgTokensSummary.isShowing()){
                                dlgTokensSummary.dismiss();
                                handlerValidateNewTokens.removeCallbacks(this);
                            }else{                      //vuelve a lanzar este handler para preguntar de nuevo si hay tokens

                                handlerValidateNewTokens.postDelayed(this, 5000);
                            }


                        }
                    }
                },1000);

            }

        }catch (Exception e){ e.printStackTrace();}


        dlgTokensSummary.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ConstantsApp.isRunningTskTokens = false;
                reloadScreen();
            }
        });


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int val = values[0];
//        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);


    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        closeDialogProgress();
        reloadScreen();
    }

    @Override
    protected void onCancelled(Void result) {
        super.onCancelled(result);
        closeDialogProgress();
    }

    protected String getTokenFromSDCard(String path) {
        BufferedReader myReader = null;
        try {
            File myFile = new File(path);
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
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void sleep(long miliseconds) {
        long timeSleep = System.currentTimeMillis() + miliseconds;
        long now = System.currentTimeMillis();
        while (now < timeSleep) {
            now = System.currentTimeMillis();
        }
    }

    private void closeDialogProgress() {
        try{

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void reloadScreen() {
        if (tipeActivity==ConstantsApp.SCREEN_INFO_TOKENS){
            try{
                Intent intent =  new Intent(context, InfoTokensActivity.class);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.finish();
            } catch (ActivityNotFoundException e){
                e.printStackTrace();
            }
        }


    }


}