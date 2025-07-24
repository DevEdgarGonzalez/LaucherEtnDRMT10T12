package com.actia.mexico.generic_2018_t10_t12;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import java.io.File;
import java.util.Arrays;


import com.actia.drm.auto_tokens.TokensAutomaticLogic;
import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.utilities.LocaleHelper;
import com.actia.utilities.utilities_external_storage.CheckExternalStorage;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utils_language.UtilsLanguage;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Activity that display Advertising
 *
 * @see ImageWorker, ConfigMasterMGC ,ReadFileExternalStorage
 */

public class AdvertisingActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();
    CheckExternalStorage sdcard;
    String stateSDCard = null;
    File[] AdvertisingFiles;
    int count = 0;
    private ImageWorker w;
    private ImageView img;
    private ImageView mBluetoothImageView;
    private ImageView ivTurnOff;
    private TextView welcome_text_view;
    private TextView touchScreen_text_view;
    private final Handler validationHandler = new Handler();
    private ConfigMasterMGC confiSingleton;
    private TokensAutomaticLogic proceessValidateTokens;

    public static boolean isShowingThisActivity = false;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		setTheme(R.style.Theme_NoBackground);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);
        touchScreen_text_view = findViewById(R.id.touchScreen_text_view);
        img = findViewById(R.id.imageView);
        welcome_text_view = findViewById(R.id.welcome_text_view);
        touchScreen_text_view = findViewById(R.id.touchScreen_text_view);
        mBluetoothImageView = findViewById(R.id.bluetooth_image_view);
        ivTurnOff = findViewById(R.id.ivTurnOff);
        ReadFileExternalStorage external = new ReadFileExternalStorage();
        sdcard = new CheckExternalStorage();
        stateSDCard = sdcard.getExternalStorageState();

        if (stateSDCard.equals(ConstantsApp.STATE_SD_CARD_MOUNTED)) {

            ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();

            confiSingleton = ConfigMasterMGC.getConfigSingleton();
            File config = new File(confiSingleton.getCONFIG_FILE());
            if (external.existDirectory(Config.getCONFIG_DIRECTORY()) && config.exists() && config.length() > 0) {

                File app_directory = new File(confiSingleton.getIMAGE_HOME_PATH_APPS());

                if (app_directory.exists() && app_directory.listFiles(new FileExtensionFilterImages()).length > 0) {
                    File DirAdvertising = new File(confiSingleton.getPathImgAdvertising());

                    if (DirAdvertising.exists() && DirAdvertising.listFiles(new FileExtensionFilterImages()).length > 0) {
                        AdvertisingFiles = DirAdvertising.listFiles(new FileExtensionFilterImages());

                        if (ConstantsApp.SORT_BY_ALPHABETICAL_IMG_ADVERTISING==true){
                            Arrays.sort(AdvertisingFiles);
                        }
                        img = findViewById(R.id.imageView);
                        w = new ImageWorker();
                        if (new File(AdvertisingFiles[0].getPath()).exists()){
                            w.loadBitmap(AdvertisingFiles[0].getPath(), img, getApplicationContext(), 1280, 800);
                            if(AdvertisingFiles[0].getName().contains("_en"))
                                onConfigurationChanged(LocaleHelper.setLocale(this, "en"));
                            else
                                onConfigurationChanged(LocaleHelper.setLocale(this, "es"));
                        }
                        count++;

                        img.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                File v_pub = new File(confiSingleton.getPathAdvertising());

                                onConfigurationChanged(LocaleHelper.setLocale(AdvertisingActivity.this, "es"));

                                if (v_pub.exists() && v_pub.listFiles(new FileExtensionFilterVideo()).length > 0) {
                                    Intent videoIntent = new Intent(AdvertisingActivity.this, VideoInitActivity.class);
                                    videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(videoIntent);
                                    AdvertisingActivity.this.finish();
                                } else {
                                    Intent homeIntent = new Intent(AdvertisingActivity.this, MainActivity.class);
                                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(homeIntent);
                                    AdvertisingActivity.this.finish();
                                }
//                                cancelProcessValidation();
                            }
                        });

                        mBluetoothImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openBluetooth();
                            }
                        });

                        ivTurnOff.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                lock();
                            }
                        });

                        updateProgress();
                    } else {
                        File v_pub = new File(confiSingleton.getPathAdvertising());

                        if (v_pub.exists() && v_pub.listFiles(new FileExtensionFilterVideo()).length > 0) {
                            Intent videoIntent = new Intent(AdvertisingActivity.this, VideoInitActivity.class);
                            videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(videoIntent);
                            AdvertisingActivity.this.finish();
                        } else {
                            Intent homeIntent = new Intent(AdvertisingActivity.this, MainActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            AdvertisingActivity.this.finish();
                        }
                    }

                } else {
                    launchErrorActivity(getString(R.string.error_content_home_aplications));
                }
            } else {
                launchErrorActivity(getString(R.string.error_config_file));
            }
        } else {
            launchErrorActivity(getString(R.string.error_sd_not_mounted) + stateSDCard);
        }

        startProcessValidation();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(AdvertisingActivity.this);
                }
            }
        });
    }

    public void updateProgress() {
        validationHandler.postDelayed(mUpdateTimeTask, 7000);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            img.setImageResource(R.drawable.transparent);
            img.setImageBitmap(null);
            img.destroyDrawingCache();
            if (count >= AdvertisingFiles.length)
                count = 0;
            if (new File(AdvertisingFiles[count].getPath()).exists()){
                w.loadBitmap(AdvertisingFiles[count].getPath(), img, getApplicationContext(), 1280, 800);

                if(AdvertisingFiles[count].getName().contains("_en"))
                    onConfigurationChanged(LocaleHelper.setLocale(AdvertisingActivity.this, "en"));
                else
                    onConfigurationChanged(LocaleHelper.setLocale(AdvertisingActivity.this, "es"));

                Log.e("TAG", "run: " +AdvertisingFiles[count].getName());
            }
            count++;
            updateProgress();
        }
    };

    /**
     * Launch Activity error
     *
     * @param txt Error text
     */

    public void launchErrorActivity(String txt) {
        Intent errorIntent = new Intent(this, ErrorActivity.class);
        errorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        errorIntent.putExtra(ErrorActivity.ARG_STATE, txt);
        startActivity(errorIntent);
//        cancelProcessValidation();
        this.finish();
    }

    private void cancelProcessValidation() {
        if (proceessValidateTokens != null) {
            proceessValidateTokens.cancelTskTokens();
            proceessValidateTokens = null;
        }
        ConstantsApp.isRunningTskTokens = true;        //pone la bandera en true para que no se lanze de nuevo ningun task de tokens
    }


    private void startProcessValidation() {
        File keyDrm = new File(confiSingleton.getPathUserDRM());
        File tokenDir = new File(confiSingleton.getPathTokens());
        File[] tokens =tokenDir.listFiles();

        //fun: valida si  hay tokens y llaveDrm en carpeta
        if (!keyDrm.exists()||tokens== null || tokens.length == 0) {
            Toast.makeText(this, getString(R.string.no_exist_dir_key_tokens), Toast.LENGTH_SHORT).show();
            return;
        }


        if (new File(confiSingleton.getPathTokens()).exists()) {
            TokensAutomaticLogic.initializeDatabase(AdvertisingActivity.this);
            if (proceessValidateTokens == null) {
                proceessValidateTokens = new TokensAutomaticLogic(AdvertisingActivity.this, ConstantsApp.SCREEN_ADVERSITING);
                proceessValidateTokens.startProccesValidateTokens();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isShowingThisActivity = true;
    }



    @Override
    protected void onPause() {
        //fun: ayuda para detener la validacion en segundo plano cuando se cambai de advertisingActivity a MainActivity
        super.onPause();
        if (isFinishing()){
            isShowingThisActivity = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        validationHandler.removeCallbacks(mUpdateTimeTask);
        android.os.Debug.stopMethodTracing();

        cancelProcessValidation();

        isShowingThisActivity = false;
    }

    @Override
    public void onBackPressed() {
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(AdvertisingActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(AdvertisingActivity.this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        welcome_text_view.setText(R.string.welcome);
        touchScreen_text_view.setText(R.string.subtitlewelcome);
        super.onConfigurationChanged(newConfig);
        Log.i("AdvertisingActivity", "onConfigurationChanged: si cambio " + newConfig.locale);
    }
}
