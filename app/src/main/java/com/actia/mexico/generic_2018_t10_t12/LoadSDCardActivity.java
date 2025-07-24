package com.actia.mexico.generic_2018_t10_t12;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.actia.utilities.utilities_external_storage.UtilitiesFile.setExtSdCard;
import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import com.actia.home_categories.MainActivity;
import com.actia.iSeat.udp_read.ReadUDPDataService;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.menu_maintance.ConfigDevice;
import com.actia.menu_maintance.HideShowSystemBarMenu;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.utilities.ReceiverServiceActia;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_ui.UtilitiesScreen;
import com.actia.utilities.utilities_external_storage.CheckExternalStorage;
import com.actia.utilities.utilities_ui.CustomViewGroup;
import com.stericson.RootTools.RootTools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * This Activity always is Launching when the app start.
 * The activity check if the external sdcard is present and mount.
 * If the sdcard is not mount, the activity can wait 14 seg to mount the external sdcard.
 */
public class LoadSDCardActivity extends BaseActivity {


    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 1;
    private String stateSDCard;
    private CheckExternalStorage sdcard;
    private final Handler sdcardHandler = new Handler();
    int count = 0;
    private WindowManager manager;
    private CustomViewGroup view;
    public static UICallback handler;
    @SuppressWarnings("unused")
    private boolean isClosed;
    private View decorView;

    private Set<BluetoothDevice> devices = null;
    public static BluetoothAdapter mBlueAdapter;

    private ConfigDevice ConfManager = null;

    private final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            android.Manifest.permission.EXPAND_STATUS_BAR,
            android.Manifest.permission.GET_TASKS,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
            android.Manifest.permission.WAKE_LOCK
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(LoadSDCardActivity.this);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_sdcard);

        if (!hasPermissions(this, PERMISSIONS)) {
            takePermissions();
        } else {


            ConfManager = new ConfigDevice();
            ConfManager.setContext(this);
            sdcard = new CheckExternalStorage();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //se guarda en shared la opcion de mostrar/ocultar la barra superior que se declara en configMaster
                    initializeContextoGeneric2018();
                    initializeControllerBarSystem();
                    init();
                }
            }, 2000);


            pushNewBrigthness(255);


            if (ConstantsApp.iSeatEnable) {
                ConfigDevice ConfManager = new ConfigDevice();
                ConfManager.setContext(LoadSDCardActivity.this);
                ConfManager.getStatusConfigAsynk(null);

                Intent intentUDP = new Intent(getApplicationContext(), ReadUDPDataService.class);
                startService(intentUDP);
            }

            try {

                mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBlueAdapter != null) {
                    if (!mBlueAdapter.isEnabled()) {
                        Log.e("LoadSDCardActivity", "Turning On Bluetooth...");
                        //intent to on bluetooth
                        mBlueAdapter.enable();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            devices = mBlueAdapter.getBondedDevices();
                            Log.e("LoadSDCardActivity", "devices: " + devices.size());


                            getStatusConfig();
                        }
                    }, 5000);
                }

            } catch (Exception ex) {
                Log.e("LoadSDCardActivity", "Something is wrong whith Bluetooth: ", ex);
            }

        }

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    hideNavigationBar(LoadSDCardActivity.this);
                }
            }
        });

    }

    private void initializeContextoGeneric2018() {
        UtilitiesScreen.getTypeScreen(this);
        ContextApp.deviceIsRoot = RootTools.isRootAvailable();


        PreferencesApp prefGeneric = new PreferencesApp(LoadSDCardActivity.this);
        if (!prefGeneric.existStatusSystemBarInShared()) {
            prefGeneric.setIsShowingSystemBar(ConfigMasterMGC.getConfigSingleton().isBarDisplay);
        }

        HideShowSystemBarMenu.setStatusBar(prefGeneric.getIsShowingSystemBar());
    }

    public void init() {

        if (count < 8) {
            setExtSdCard(this);
            prefExtSd = getPrefExtSd();
            stateSDCard = sdcard.getExternalStorageState();
            if (stateSDCard.equals(ConstantsApp.STATE_SD_CARD_MOUNTED)) {
                Intent i = new Intent();
                i.setComponent(new ComponentName("com.actia.serviceactia", "com.actia.serviceactia.ServiceMaster"));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    startForegroundService(i);
                } else {
                    startService(i);
                }

                ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
                if (!Config.isFirtTime()) {
                    Config.setFirtTime(true);
                    Intent intent = new Intent(this, AdvertisingActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                this.finish();
            } else {
                Toast.makeText(LoadSDCardActivity.this, Integer.toString(count), Toast.LENGTH_SHORT).show();
                updateProgress();
            }
        } else {
            Intent errorIntent = new Intent(this, ErrorActivity.class);
            errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            errorIntent.putExtra(ErrorActivity.ARG_STATE, getString(R.string.memory_not_mounted) + stateSDCard);
            this.finish();
            startActivity(errorIntent);
        }
        count++;
    }


    /**
     * Update the progress to mount the external sdcard
     */
    public void updateProgress() {
        sdcardHandler
                .postDelayed(mUpdateTimeTask, 3700);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            init();
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        sdcardHandler.removeCallbacks(mUpdateTimeTask);
        android.os.Debug.stopMethodTracing();

    }

    /**
     * Bloquea la barra superior de sistema invalidando la apertura de menu android al deslizar dedo
     * inicializa el handler  estatico  que se encargara de administrar la manipulacion de la barra, este handler tambien se manipula desde "MenuMaintanceDialog"
     */
    private void initializeControllerBarSystem() {
        handler = new UICallback();
        manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (25 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        view = new CustomViewGroup(LoadSDCardActivity.this);

        manager.addView(view, localLayoutParams);
        isClosed = true;
    }


    /**
     * Clase que se encargara de manipular la barra de sistema bloqueandola o desbloqueandola dependiendo del mensaje que se reciba
     * este metodo esta atado a esta actividad ya que la variable view se toma de este activity ya que es la pantalla inicial de la app
     */
    public final class UICallback implements Handler.Callback {
        static final int UNLOCK_SYSTEM_BAR = 0;
        private static final int LOCK_SYSTEM_BAR = 1;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UICallback.LOCK_SYSTEM_BAR: {

                    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
                    localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                    localLayoutParams.gravity = Gravity.TOP;
                    localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                            // this is to enable the notification to recieve touch events
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                            // Draws over status bar
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

                    localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    localLayoutParams.height = (int) (25 * getResources()
                            .getDisplayMetrics().scaledDensity);
                    localLayoutParams.format = PixelFormat.TRANSPARENT;

                    if (view != null) {
                        try {
                            manager.addView(view, localLayoutParams);
                            isClosed = true;

                        } catch (Exception e) {
                            //FIXME : Bug cuando ya tiene la vista el window maanager, pasa cuando se recrea el main
                            e.printStackTrace();
                        }
                    }

                    return true;
                }
                case UICallback.UNLOCK_SYSTEM_BAR: {
                    if (view != null) {
                        if (view.isAttachedToWindow()) {
                            manager.removeView(view);
                            isClosed = false;
                        }
                    }

                    return true;
                }
                default:
                    return false;
            }
        }
    }

    protected void pushNewBrigthness(int level) {
        Settings.System.putInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, level);

        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        Settings.System.putInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                level);
    }

    protected void onResume() {
        super.onResume();
        hideNavigationBar(LoadSDCardActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                ConfManager = new ConfigDevice();
                ConfManager.setContext(this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //se guarda en shared la opcion de mostrar/ocultar la barra superior que se declara en configMaster
                        initializeContextoGeneric2018();
                        initializeControllerBarSystem();
                    }
                }, 2000);
                sdcard = new CheckExternalStorage();

                pushNewBrigthness(255);
                init();


                if (ConstantsApp.iSeatEnable) {
                    ConfigDevice ConfManager = new ConfigDevice();
                    ConfManager.setContext(LoadSDCardActivity.this);
                    ConfManager.getStatusConfigAsynk(null);

                    Intent intentUDP = new Intent(getApplicationContext(), ReadUDPDataService.class);
                    startService(intentUDP);
                }


            } else {
                Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);
            }

            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                if (!Environment.isExternalStorageManager()) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                        startActivityForResult(intent, 2296);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }

        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getStatusConfig() {


        ConfManager.getStatusConfigAsynk(new ConfigDevice.OnLoadConfigDevice() {

            @Override
            public void OnFinishLoadConfigDeviceListener(Boolean isConfig,
                                                         String idDevice, String idBus) {
                //if (isConfig) {

                Log.e("LoadSDCardActivity", "Bluetooth name: " + mBlueAdapter.getName());

                mBlueAdapter.setName(LoadSDCardActivity.this.getString(R.string.name_tactil_config_device) + idDevice + LoadSDCardActivity.this.getString(R.string.type_tactil_config_device) + idBus);
                Log.e("LoadSDCardActivity", "Bluetooth new name: " + mBlueAdapter.getName());
                if (devices.size() > 0) {
                    for (BluetoothDevice device : devices) {
                        unpairDevice(device);
                        Log.e("LoadSDCardActivity", "deleting pared device: " + device);
                    }
                }
                Log.e("LoadSDCardActivity", "Turning Off Bluetooth...");
                mBlueAdapter.disable();
            }
            //}
        });
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("LoadSDCardActivity", e.getMessage());
        }
    }


}
