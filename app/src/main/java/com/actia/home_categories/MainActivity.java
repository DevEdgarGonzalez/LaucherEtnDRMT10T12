package com.actia.home_categories;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.actia.encuesta.EnvioEncuestaLoader;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.mapas.SingletonConfig;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.menu_maintance.MenuMaintanceDialog;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.MyMediaPlayerService;
import com.actia.utilities.ShadowHelpFragment;
import com.actia.utilities.dialogs.DialogLanguage;
import com.actia.utilities.utilities_file.MediaFileFunctions;
import com.actia.utilities.utilities_internet.AsynckCheckInternetConn;
import com.actia.utilities.utilities_ui.AdminReceiver;
import com.actia.utilities.utilities_ui.UtilsImageView;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * This activity contain the home Launcher.It's represented by a GridView.
 */

public class MainActivity extends BaseActivity implements OnHomeCategoryListener{


    private final String TAG = this.getClass().getSimpleName();
    private final int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    private static final String ENCUESTA_REQUEST_URL_SERVER = SingletonConfig.getConfigSingleton().getIP_SERVER_ACTIES();
    private static final String ENCUESTA_REQUEST_URL_REPOSITORY = SingletonConfig.getConfigSingleton().getENCUESTA_UPLOAD_URL();

    private static final int ENVIO_LOADER_ID = 2;


    private String rutaEncuesta = ConfigMasterMGC.getConfigSingleton().getENCUESTAS_DIR();
    ConfigMasterMGC configSingleton;
    private String sourceFile = "";
    private ImageView btnMenuHome;
    private View decorView;
    private ImageView imgvLanguageMainAct;
    private View llytContainerLanguageMainAct;
    private ImageView ivTurnOff;
    private ImageView bright;
    private ImageView imgvBluetoothMainAct;
    private ImageView ivSubMenu;
    public static boolean isStp100;

    static Context context;

    private static boolean showingDialog = true;
    public static UDP_Broadcast udpBroadcast;
    public static WifiManager wifi;
    public static Integer iPortRoute = 5002;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        startElementsUI();
        loadApplicationCategories();
        backupTactilInfo();

        sendBroadcastNewInstanceApp();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifi.getConnectionInfo();

        File archivos = new File(rutaEncuesta);
        LoaderManager loaderManager = getSupportLoaderManager();

        AsynckCheckInternetConn stp100connection = new AsynckCheckInternetConn(this);

        try {
            isStp100 = stp100connection.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(isStp100){
                if(archivos.exists()){
                    sourceFile = "";
                    sourceFile = rutaEncuesta;
                    loaderManager.initLoader(ENVIO_LOADER_ID, null, loaderCallbacksEnvio);

                }
            }

        if (showingDialog)
            showShadowHelpDialog();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    hideNavigationBar(MainActivity.this);
                }
            }
        });

    }

    private void loadApplicationCategories() {
        if (ContextApp.categoriesApp == null) {
            int count = 0;
            configSingleton = ConfigMasterMGC.getConfigSingleton();
            configSingleton.setPageServer("http://192.168.1.2");
            configSingleton.getJSONConfig();
            HashMap<Integer, ItemsHome> categoriesApp = new HashMap<>();
            ArrayList<ItemsHome> applications = configSingleton.getAppHome("home");
            for (ItemsHome category : applications) {
                String className = category.getClassName();
                if (category.getClassName() != null && className.length() > 6) {
                    categoriesApp.put(count++, category);
                }
            }

            ContextApp.categoriesApp = categoriesApp;
        }

        if (ContextApp.subCategoriesApp == null) {
            int count = 0;
            configSingleton = ConfigMasterMGC.getConfigSingleton();
            configSingleton.setPageServer("http://192.168.1.2");
            configSingleton.getJSONConfig();
            ArrayList<ItemsHome> subCategoriesApp = new ArrayList<>();
            ArrayList<ItemsHome> applications = configSingleton.geSubMenuHome("home");
            for (ItemsHome subCategory : applications) {
                String className = subCategory.getClassName();
                if (subCategory.getClassName() != null && className.length() > 6) {
                    subCategoriesApp.add(subCategory);
                }
            }
            ContextApp.subCategoriesApp = subCategoriesApp;

            if(ContextApp.subCategoriesApp.size() > 0){
                Bitmap bmp = BitmapFactory.decodeFile(ContextApp.subCategoriesApp.get(0).getPathImg());
                ivSubMenu.setImageBitmap(bmp);
            }
        } else {
            if(ContextApp.subCategoriesApp.size() > 0){
                Bitmap bmp = BitmapFactory.decodeFile(ContextApp.subCategoriesApp.get(0).getPathImg());
                ivSubMenu.setImageBitmap(bmp);
            }
        }
    }

    private void startElementsUI() {

        btnMenuHome = findViewById(R.id.btnMenuHome);

        llytContainerLanguageMainAct = findViewById(R.id.llytContainerLanguageMainAct);
        imgvLanguageMainAct = findViewById(R.id.imgvLanguageMainAct);
        bright = findViewById(R.id.bright);
        imgvBluetoothMainAct = findViewById(R.id.imgvBluetoothMainAct);
        ivSubMenu = findViewById(R.id.ivSubMenu);

        ivTurnOff = findViewById(R.id.ivTurnOff);

        //tvTitulo.setTypeface(UtilsFonts.getTypefaceMainActivity(getApplicationContext()));

        btnMenuHome.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MenuMaintanceDialog(MainActivity.this, getSupportFragmentManager()).showMenu(false);
                return false;
            }
        });

        llytContainerLanguageMainAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogLanguage(v.getContext()).show();
            }
        });

        bright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrightnessDialog();
            }
        });

        imgvBluetoothMainAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBluetooth();
            }
        });

        if (UtilsLanguage.isAppInEnglish()) {
            imgvLanguageMainAct.setImageDrawable(getResources().getDrawable(R.drawable.flag_english));
        } else {
            imgvLanguageMainAct.setImageDrawable(getResources().getDrawable(R.drawable.flag_spanish));
        }

        ivTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lock();
            }
        });

        ivSubMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent();
                    intent.setClassName(context, ContextApp.subCategoriesApp.get(0).getClassName());
                    intent.putExtra(ConstantsApp.ARG_SUBCAT, ContextApp.subCategoriesApp.get(0).getTitle());
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * This activity is launched when the system is shutdown.
     */
    private void backupTactilInfo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
                File f = new File(Config.getXML_TACTIL_INFO());       ///mnt/sdcard/tactil_info.xml

                if (!f.exists()) {
                    File renF = new File(Config.getXML_TACTIL_INFO_EXTSD());      ///mnt/extsd/config/tactil_info.xml
                    if (renF.exists() && renF.length() > 0) {
                        MediaFileFunctions fun = new MediaFileFunctions();
                        fun.copyFile(renF, f);
                        fun.deleteViaContentProvider(getApplicationContext(), renF.getPath());
                    }
                }
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "On Destroy MainActivity");

    }

    @Override
    public void onCategorySelected(ItemsHome category) {
        Gson gson = new Gson();

        try {
            String categoryInJson = gson.toJson(category);


            Intent intent = new Intent();
            intent.setClassName(this, category.getClassName());
            intent.putExtra(ConstantsApp.ARG_CATEGORY, categoryInJson);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public static class ShutDownActivity extends Activity {


        private View decorView;

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_shut_down);
            if (isMyMediaPlayerServiceRunning()) {
                Intent intent = new Intent(ShutDownActivity.this, MyMediaPlayerService.class);
                intent.addCategory(MyMediaPlayerService.TAG);
                ShutDownActivity.this.stopService(intent);
            }

            hideNavigationBar(this);

            decorView = getWindow().getDecorView();

            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility == 0) {
                        hideNavigationBar(ShutDownActivity.this);
                    }
                }
            });

        }

        /**
         * Check if media player service is running
         *
         * @return true if media player service is running
         */
        private boolean isMyMediaPlayerServiceRunning() {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MyMediaPlayerService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        protected void onResume() {
            super.onResume();
            hideNavigationBar(ShutDownActivity.this);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            hideNavigationBar(ShutDownActivity.this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        new MenuMaintanceDialog(MainActivity.this, getSupportFragmentManager()).showMenu(false);
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Este emtodo envia broadcast cuando la app esta en pantalla principal para que todos los procesos necesarios se detengan
     * ej: musica, abooks, etc
     * esto evita que
     */
    private void sendBroadcastNewInstanceApp() {
        Intent intent = new Intent(ConstantsApp.ARG_NEW_INSTANCE_APP);
        sendBroadcast(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onResume() {
        super.onResume();
        checkWiFiConnection();
        hideNavigationBar(MainActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("WrongConstant")
    private void showShadowHelpDialog() {

        ShadowHelpFragment shadowHelpFragment = new ShadowHelpFragment();
        shadowHelpFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen);
        shadowHelpFragment.show(getSupportFragmentManager(), "DialogFragment");

        showingDialog = false;
    }

    static Handler mHandler = new Handler();

    public static void restartBroadcast() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopBroadcast();
                startBroadcast();
            }
        });
    }

    public static void startBroadcast() {

        udpBroadcast = new UDP_Broadcast(wifi, iPortRoute);
        udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, context);
        udpBroadcast.start(true);
    }

    public static void stopBroadcast() {
        if (udpBroadcast != null && udpBroadcast.isAlive()) {
            udpBroadcast.stopBroadcast();
        }
    }

    public void checkWiFiConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            stopBroadcast();
        } else {
            if (udpBroadcast == null)
                restartBroadcast();
            else {
                udpBroadcast.setListener(null, null);
                udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Integer> loaderCallbacksEnvio = new LoaderManager.LoaderCallbacks<Integer>() {

        @NonNull
        public Loader<Integer> onCreateLoader(int i, @Nullable Bundle bundle) {
            return new EnvioEncuestaLoader(MainActivity.this, ENCUESTA_REQUEST_URL_SERVER + ENCUESTA_REQUEST_URL_REPOSITORY, sourceFile, true);
        }

        public void onLoadFinished(@NonNull Loader<Integer> loader, Integer result) {
            Log.e(TAG, "onLoadFinished: Carga de archivos completa");
        }

        public void onLoaderReset(@NonNull Loader<Integer> loader) {
        }
    };

}

