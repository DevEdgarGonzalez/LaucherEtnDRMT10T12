package com.actia.infraestructure;

import static com.actia.utilities.utilities_external_storage.UtilitiesFile.setExtSdCard;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.IBluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.actia.drm.ExoDRMActivity;
import com.actia.drm.PlayerDRMActivity;
import com.actia.help_movie.HelpMainActivity;
import com.actia.home_categories.MainActivity;
import com.actia.iSeat.SensorOfSittingPerson;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.MyMediaPlayerService;
import com.actia.ninos.NinosActivity;
import com.actia.peliculas.Movie;
import com.actia.peliculas.PlayMovieActivity;
import com.actia.random_movies.RandomMovies;
import com.actia.random_movies.RandomMoviesWindowPopUp;
import com.actia.random_movies.RandomMoviewsAdapter;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.ReceiverServiceActia;
import com.actia.utilities.UtilitiesComunicationServiceActia;
import com.actia.utilities.list_devices_bt.ManagerBluetoothLauncher;
import com.actia.utilities.utilities_dialog_brightness.DialogBrightnessFragment;
import com.actia.utilities.utilities_external_storage.CheckExternalStorage;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utilities_media_player.UtilitiesGeneralMediaPlayer;
import com.actia.utilities.utilities_ui.AdminReceiver;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Edgar Gonzalez on 23/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class BaseActivity extends FragmentActivity implements RandomMoviewsAdapter.OnSelectedListener {
    private static final String TAG = "BaseActivityLog";
    private final Gson gson= new Gson();

    private ManagerBluetoothLauncher managerBluetoothLauncher;
    public static IBluetoothHeadset iBtHeadset;
    private final Handler handlerStartBtAdapter = new Handler();
    int countBtGetName = 0;
    protected Context context;

    public static Context mycontext;
    private String nameDevice;
    protected ImageView imgBtnHomeBotBar;
    protected ImageView imgBtnBrightnessBotBar;
    protected ImageView imgBtnHelpBotBar;
    protected ImageView imgBtnBackBotBar;
    protected ImageView imgBtnRandomBotBar;
    protected ImageView ivTurnOff;
    protected ImageView imgBtnNextBotBar;
    protected ImageView imgvBluetoothMainAct;

    ImageView imgvCategory;
    ImageView ibtnBackHeader;
    ImageView imgvBrightHeader;
    ImageView btnBluetooth;
    ImageView ibtnHomeHeader;

    protected int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    protected ItemsHome category;

    private CategoryNavigationListener categoryNavigationListener;
    private FuctionRandomListener randomListener = null;

    protected static final int BAR_ALL = 1;
    protected static final int BAR_HOME_CHILDREN_HELP = 2;
    protected static final int BAR_HOME_HELP = 3;
    protected static final int BAR_CHILDREN_HOME = 4;
    protected static final int BAR_HOME_BACK = 5;
    protected static final int BAR_BACK = 6;
    public static String prefExtSd = "";
    ReceiverServiceActia serviceActia = new ReceiverServiceActia();

    public interface CategoryNavigationListener {
        void previousCategory();

        void nextCategory();

    }

    public interface PressBackHeader {
        void clickBackHeader();
    }

    public interface FuctionRandomListener {
        void randomInteraction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setUIFullScreen();
        UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(this);

        mycontext = this;
        if (ConstantsApp.iSeatEnable) {
            sensorOfSittingPerson = new SensorOfSittingPerson(this);
            createInstanceDialog();
            registerReceiver(receiverIseatBrigth, allDataSensorReceiver);
        }

        //Toast.makeText(this, "Registrando receiver", Toast.LENGTH_SHORT).show();
        IntentFilter filter = new IntentFilter("com.actia.mgc");
        registerReceiver(serviceActia, filter);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setUIFullScreen();
    }


    protected void startTopBar(final Context context, String titleCategory, Drawable imgDrawable, final PressBackHeader pressBackHeader) {
        if (this.context == null) this.context = context;

        imgvCategory = findViewById(R.id.imgvCategory);
        ibtnBackHeader = findViewById(R.id.ibtnBackHeader);
        imgvBrightHeader = findViewById(R.id.ibtnBrillo);
        btnBluetooth = findViewById(R.id.btnBluetooth);
        ibtnHomeHeader = findViewById(R.id.ibtnHomeHeader);

        imgvCategory.setImageDrawable(imgDrawable);

        ibtnBackHeader.setImageResource(R.drawable.state_button_back_header);

        Animation animationTraslate = AnimationUtils.loadAnimation(this, R.anim.anim_hideshow_back_header);
        ibtnBackHeader.setAnimation(animationTraslate);
        ibtnBackHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pressBackHeader != null) {
                    pressBackHeader.clickBackHeader();
                }
            }
        });

        ibtnBackHeader.requestFocus();

        imgvBrightHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrightnessDialog();
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBluetooth();
            }
        });

        ibtnHomeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    protected void backButtonVisibility(boolean showBackButton){
        if(showBackButton){
            ibtnBackHeader.setVisibility(View.VISIBLE);
        } else {
            ibtnBackHeader.setVisibility(View.GONE);
        }
    }

    protected Drawable mutableDrawableFromPath(String path) {
        if (path == null || path.length() == 0) return null;
        return Drawable.createFromPath(path);

    }


    /**
     * @param context
     * @param categoryNavigationListener
     * @param typeBottomoBar
     * @param randomListener             Si es nulo se oculta el icono de random
     */
    protected void startBottomBar(Context context, CategoryNavigationListener categoryNavigationListener, int typeBottomoBar, FuctionRandomListener randomListener) {
        if (this.context == null) this.context = context;
        if (mycontext == null) mycontext = context;
        this.categoryNavigationListener = categoryNavigationListener;
        this.randomListener = randomListener;

        addListenersBottomBar(false);
        showElementsBottomBar(typeBottomoBar);

        if (this.randomListener == null) {
            imgBtnRandomBotBar.setVisibility(View.INVISIBLE);
        }
        if (this.categoryNavigationListener == null) {
            imgBtnBackBotBar.setVisibility(View.INVISIBLE);
            imgBtnNextBotBar.setVisibility(View.INVISIBLE);
        }

    }


    private void showElementsBottomBar(int typeBottomoBar) {
        switch (typeBottomoBar) {
            case (BAR_ALL):
                ivTurnOff.setVisibility(View.GONE);
                break;
            case (BAR_HOME_CHILDREN_HELP):

            case (BAR_HOME_HELP):
                imgBtnBackBotBar.setVisibility(View.INVISIBLE);
                imgBtnRandomBotBar.setVisibility(View.INVISIBLE);
                imgBtnNextBotBar.setVisibility(View.INVISIBLE);
                ivTurnOff.setVisibility(View.GONE);
                break;
            case (BAR_CHILDREN_HOME):
                imgBtnHomeBotBar.setVisibility(View.GONE);
                imgBtnRandomBotBar.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private void addListenersBottomBar(final boolean isChildScreen) {
        imgBtnHomeBotBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });

        imgBtnBrightnessBotBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrightnessDialog();
            }
        });

        imgBtnHelpBotBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpFuction();
//                    showImageHelp();

            }
        });

        imgBtnBackBotBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryNavigationListener.previousCategory();
            }
        });

        imgBtnRandomBotBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomListener.randomInteraction();
            }
        });

        imgBtnNextBotBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryNavigationListener.nextCategory();
            }
        });

        startBluetoothSearch();

    }

    protected void home() {
        Intent intent = new Intent(context, MainActivity.class);
        changeActivity(intent, true);
    }

    protected void goChildrensActivity() {
        Intent intent = new Intent(context, NinosActivity.class);
        changeActivity(intent, true);
    }


    /**
     * Change of activity asynchronously.
     *
     * @param intent          Intent of the app.
     * @param destroyActivity true if the activity must be destroy.
     */
    protected void changeActivity(Intent intent, boolean destroyActivity) {
        new AsyncIntents(this, destroyActivity).execute(intent);
    }


    protected boolean isConnectedSdCard() {
        CheckExternalStorage sdcard = new CheckExternalStorage();

        String stateSDCard = sdcard.getExternalStorageState();
        return stateSDCard.equals(ConstantsApp.STATE_SD_CARD_MOUNTED);
    }

    private void setUIFullScreen() {

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    public void onSelected(Movie movie) {
        if (UtilitiesGeneralMediaPlayer.isMyMediaPlayerServiceRunning(this)) {//Verifica si el servicio de música esta corriendo
            Intent intent = new Intent(context, MyMediaPlayerService.class);
            intent.addCategory(MyMediaPlayerService.TAG);
            stopService(intent);
        }

        UtilitiesComunicationServiceActia.updateMultimediaBroadcast(this);

        if (movie.isDRM) {
            Intent intent = new Intent(context, ExoDRMActivity.class);
            intent.putExtra(PlayerDRMActivity.keyPathDRM, movie.path);
            startActivityForResult(intent, 1);

        } else {
            Intent intent = new Intent(context, PlayMovieActivity.class);
            intent.putExtra("path", movie.path);
            startActivityForResult(intent, 1);
        }
    }

    /*protected void loadAllMoviesRandom() {
        ArrayList<Movie> alistRandomMovie;

        RandomMovies randomMovies = new RandomMovies();
        alistRandomMovie = randomMovies.getRandomAllGenres(this);
        alistRandomMovie.size();

        RandomMoviesWindowPopUp randomMoviesWindowPopUp = new RandomMoviesWindowPopUp(this);
        randomMoviesWindowPopUp.showPopPup(alistRandomMovie, 0);
    }*/
    protected void loadMoviesRandomByPath(String rootPath) {

        RandomMovies randomMovies = new RandomMovies();
        boolean includeChuildren =false;

        if (rootPath.equals(ConfigMasterMGC.getConfigSingleton().getVIDEO_PATH())){
            includeChuildren =true;
        }

        ArrayList<Movie> alistRandomMovie = randomMovies.getRandomMoviesByRootPath(this,rootPath, includeChuildren);


        RandomMoviesWindowPopUp randomMoviesWindowPopUp = new RandomMoviesWindowPopUp(this);
        randomMoviesWindowPopUp.showPopPup(alistRandomMovie, 0);
    }

    protected void loadMoviesRandomChild() {
        if (UtilitiesGeneralMediaPlayer.isMyMediaPlayerServiceRunning(this)) {
            Intent intent = new Intent(context, MyMediaPlayerService.class);
            intent.addCategory(MyMediaPlayerService.TAG);
            stopService(intent);
        }
        ReadFileExternalStorage readFileExternalStorage = new ReadFileExternalStorage();
        ArrayList<Movie> alistRandomMovie;

        alistRandomMovie = readFileExternalStorage.getMovieChildrenByGenre();
        Collections.shuffle(alistRandomMovie);

        RandomMoviesWindowPopUp randomMoviesWindowPopUp = new RandomMoviesWindowPopUp(this);
        randomMoviesWindowPopUp.showPopPup(alistRandomMovie, 0);
    }


    public void helpFuction() {
        File pathHelp = new File(new ConfigMasterMGC().getPathHelpVideo());
        if (!pathHelp.exists()) {
            Toast.makeText(context, getString(R.string.we_invalid_content) + "", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] filesVideo = pathHelp.listFiles(new FileExtensionFilterVideo());
        if (filesVideo != null && filesVideo.length > 0) {
            playMovieHelp();
        } else {
//                    goHelpAction(context);
            Intent intent = new Intent(context, HelpMainActivity.class);
            changeActivity(intent, false);

        }
    }

    public void playMovieHelp() {
        if (UtilitiesGeneralMediaPlayer.isMyMediaPlayerServiceRunning(this)) {//Verifica si el servicio de música esta corriendo
            Intent intent = new Intent(context, MyMediaPlayerService.class);
            intent.addCategory(MyMediaPlayerService.TAG);
            stopService(intent);
        }

        ConfigMasterMGC mgc = ConfigMasterMGC.getConfigSingleton();

        File mFile = new File(mgc.getPathHelpVideo());
        if (mFile.exists() && mFile.listFiles() != null) {


            UtilitiesComunicationServiceActia.updateMultimediaBroadcast(this);


            Intent intent = new Intent(this, HelpMainActivity.class);
//            startActivityForResult(intent, 1);
            changeActivity(intent, false);
        } else {
            Toast.makeText(this, getString(R.string.video_help_not_avaliable), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Metodo que te manda a la categoria a traves de su posicion en el HashMAp CategoriesApp
     *
     * @param context  necesario para realizar el cambio de actividad
     * @param category posicion de la categoria a la que se quiere mandar
     */
    protected void goCategorySelectedInBottomBar(Context context, int category) {
        Intent intent;
        if (category < 0 || category >= ContextApp.categoriesApp.size()) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent();
            intent.setClassName(context, ContextApp.categoriesApp.get(category).getClassName());
            intent.putExtra(ConstantsApp.ARG_CATEGORY,parseItemHomeToJson(ContextApp.categoriesApp.get(category)));
        }
        try {
            startActivity(intent);
            this.finish();

        } catch (Exception e) {
            home();
        }
    }

    protected void goCategorySelectedInBottomBarChildren(Context context, int category) {
        Intent intent;
        if (category < 0 || category >= ContextApp.categoriesChildren.size()) {
            intent = new Intent(context, NinosActivity.class);
            if (ContextApp.childrenCategory!=null){
                String jsonCategoryChildren = new Gson().toJson(ContextApp.childrenCategory);
                intent.putExtra(ConstantsApp.ARG_CATEGORY, jsonCategoryChildren);

            }
        } else {
            intent = new Intent();
            intent.setClassName(context, ContextApp.categoriesChildren.get(category).getClassName());
        }
        try {
            startActivity(intent);

        } catch (Exception e) {
            goChildrensActivity();
        }
    }

    protected void deleteAllElements() {
        imgBtnHomeBotBar = null;
        imgBtnBrightnessBotBar = null;
        imgBtnHelpBotBar = null;
        imgBtnBackBotBar = null;
        imgBtnRandomBotBar = null;
        imgBtnNextBotBar = null;
        context = null;
    }

    /**
     * este metodo manda un mensaje broadcast a ServicioActia para generar el logMovies con los datos que se guardan en el Bundle que genera el reproductor de videos (Exoplayer o media player)
     *
     * @param requestCode
     * @param resultCode
     * @param data        Contiene los banners y la pelicula reproducida
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle bundleInfoMovieBanners = data.getBundleExtra("data");
                    if (bundleInfoMovieBanners != null &&
                            !bundleInfoMovieBanners.isEmpty() &&
                            bundleInfoMovieBanners.containsKey("movie") &&
                            bundleInfoMovieBanners.containsKey("banners")) {

                        UtilitiesComunicationServiceActia.createLogMovieBannersBroadcast(this, bundleInfoMovieBanners);


                    }
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UtilitiesComunicationServiceActia.sendLogBroadcast(getApplicationContext());
                }
            }, 1000);

        }

        if (managerBluetoothLauncher != null) {
            managerBluetoothLauncher.onActivityResult(requestCode, resultCode, data);
        }

    }

    Runnable runnableSerachDeviceConected = new Runnable() {
        @Override
        public void run() {
            if (managerBluetoothLauncher==null){
                //tvNameDeviceBluetoothMainAct.setText(getString(R.string.main_bluetooth_msg_connect));
                handlerStartBtAdapter.postDelayed(this, 2000);
                return;
            }
            nameDevice = managerBluetoothLauncher.getNameDeviceConnected();
            if (nameDevice == null || nameDevice.isEmpty()) {
                countBtGetName++;
                //tvNameDeviceBluetoothMainAct.setText(getString(R.string.main_bluetooth_msg_connect));
                handlerStartBtAdapter.postDelayed(this, 2000);
            } else {
                countBtGetName = 0;
                //tvNameDeviceBluetoothMainAct.setText(nameDevice);
                handlerStartBtAdapter.removeCallbacks(this);
            }
        }
    };


    protected String getPathImgBar(String pathImgCat) {
        if (pathImgCat == null || pathImgCat.isEmpty()) {
            return null;
        }
        File pathBar = new File(pathImgCat);
        String name = pathBar.getName();
        String[] parts = name.split("\\.");
        if (parts != null && parts.length == 2) {
            String nameBar = parts[0] + "_bar." + parts[1];  // name + "_bar" + extension
            File pathImageBar = new File(pathBar.getParent(), nameBar);
            return pathImageBar.getPath();
        }

        return null;
    }


    //-------------------------- iSeat--------------------------------
    private Dialog dialogSeatBelt;
    private ListenersPlayers mListenerPlayers;
    private SensorOfSittingPerson sensorOfSittingPerson;

    protected void addListenersSensors_iSeat(ListenersPlayers listeners) {
        this.mListenerPlayers = listeners;

    }

    private void createInstanceDialog() {

        if (dialogSeatBelt == null) {
            dialogSeatBelt = new Dialog(this);
            dialogSeatBelt.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSeatBelt.setContentView(R.layout.fragment_seat_belt_dialog);
            dialogSeatBelt.setCancelable(false);
            dialogSeatBelt.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialogSeatBelt.getWindow().getAttributes());
            lp.width = 1000;
            lp.height = 600;
            dialogSeatBelt.getWindow().setAttributes(lp);
        }
    }

    IntentFilter allDataSensorReceiver = new IntentFilter("allDataSensor");


    BroadcastReceiver receiverIseatBrigth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: llega broadcast de sensores");
            if (intent != null && intent.getExtras() != null) {

                Bundle extras = intent.getExtras();


                if (extras.containsKey("passenger") && extras.containsKey("seatbelt")) {

                    boolean isTheSittingPassenger = extras.getBoolean("passenger");
                    sensorOfSittingPerson.applyActionSafetyBelt(isTheSittingPassenger); // turnOff/turnOn screen

                    boolean isSafetyBeltPlaced = extras.getBoolean("seatbelt");
                    manageDialogSeatBelt(isSafetyBeltPlaced);

                    Log.d(TAG, "passenger: " + isTheSittingPassenger);
                    Log.d(TAG, "coloquedSafetyBelt: " + isSafetyBeltPlaced);
                    if (mListenerPlayers != null) {
                        mListenerPlayers.statusSensors(isTheSittingPassenger, isSafetyBeltPlaced);
                    }


                }


            }
        }
    };

    protected void manageDialogSeatBelt(boolean isSafetyBeltPlaced) {

        if (isSafetyBeltPlaced) {
            if (dialogSeatBelt != null && dialogSeatBelt.isShowing()) {
                dialogSeatBelt.dismiss();
                Log.d(TAG, "Se cierra dialogo");

            }
        } else {
            if (dialogSeatBelt != null && !dialogSeatBelt.isShowing()) {
                dialogSeatBelt.show();
                Log.d(TAG, "se muestra dialogo");
            }
        }

    }

    public interface ListenersPlayers {
        void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced);

    }

    protected void manageVideoPlayerWithSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced, MediaPlayer mMediaPlayer) {
        if (mMediaPlayer == null) {
            return;
        }

        if (isPassengerSitting && isSafetyBeltPlaced) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    protected void showBrightnessDialog() {
//        DialogBrightness dialog = new DialogBrightness();
//        dialog.show(getSupportFragmentManager(), null);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogBrightnessFragment dialog = DialogBrightnessFragment.newInstance();
        dialog.show(ft, "dialog");
//      DialogFragment_Login dialog = DialogFragment_Login.newInstance(configObject.numEco);
//      dialog.show(ft, "dialog");


    }


    /**
     * Obtiene la categoria del Intent
     *
     * @param extras
     * @return null si no se logra parsear, o object si se logra parsear correctamente
     */
    protected ItemsHome getCategoryFromExtras(Bundle extras) {

        if (extras == null || !extras.containsKey(ConstantsApp.ARG_CATEGORY)) {
            return null;
        }
        ItemsHome category = null;
        try {
            String categoryInJson = extras.getString(ConstantsApp.ARG_CATEGORY);
            category = gson.fromJson(categoryInJson, ItemsHome.class);
        } catch (JsonSyntaxException e) {

        }

        return category;
    }

    /**
     * Obtiene la posicion en el arreglo de categorias o subcategorias en ConstansApp
     *
     * @param categoryFromExtras itemHome proveniente del intent
     * @param nameClass          nombre de la clase hija
     * @return posicion en la cual se localiza
     */
    protected int getPositionCategory(ItemsHome categoryFromExtras, String nameClass) {
        if (categoryFromExtras.isSubMenu()) {
            for (int i = 0; i < ContextApp.subCategoriesApp.size(); i++) {
                if (compareCategories(ContextApp.subCategoriesApp.get(i), categoryFromExtras, nameClass)) {
                    return i;
                }
            }

        } else {
            for (int i = 0; i < ContextApp.categoriesApp.size(); i++) {

                if (compareCategories(ContextApp.categoriesApp.get(i), categoryFromExtras, nameClass)) {
                    return i;
                }
            }
        }


        return ConstantsApp.CATEGORY_NO_DETECTED;
    }

    //bluetooth
    private void startBluetoothSearch() {
        context = this;
        imgvBluetoothMainAct = findViewById(R.id.imgvBluetoothMainAct);
        imgvBluetoothMainAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBluetooth();
            }
        });

        handlerStartBtAdapter.postDelayed(new Runnable() {
            @Override
            public void run() {
                managerBluetoothLauncher = new ManagerBluetoothLauncher(BaseActivity.this, getFragmentManager(), imgvBluetoothMainAct);
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter!=null ){
                    imgvBluetoothMainAct.setVisibility(View.VISIBLE);
                }

            }
        }, 500);

        Intent i = new Intent(IBluetoothHeadset.class.getName());
        i.setPackage("com.android.bluetooth");


        if (bindService(i, HSPConnection, Context.BIND_AUTO_CREATE)) {

        } else {
            Log.e("HSP FAILED", "Could not bind to Bluetooth HFP Service");
        }


    }


    /**
     * Valida si los datos corresponden a ItemHome en constantsApp
     *
     * @param categoryInConstants Item home especifico en Constants
     * @param categoryFromExtras  la categoria que se paso en intent
     * @param nameClass           clase hija
     * @return si esa
     */
    private boolean compareCategories(ItemsHome categoryInConstants, ItemsHome categoryFromExtras, String nameClass) {

        return categoryInConstants.getClassName().contains(nameClass) &&                                //validate className
                categoryInConstants.getPathImg().equals(categoryFromExtras.pathImg) &&                  //validate Img
                categoryInConstants.getPackageName().equals(categoryFromExtras.getPackageName());       //validate PackageName
    }


    public String parseItemHomeToJson(ItemsHome itemsHome){
        return gson.toJson(itemsHome);
    }


    public void openBluetooth() {
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    //Method for bind
    public static ServiceConnection HSPConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBtHeadset = IBluetoothHeadset.Stub.asInterface(service);
            //iBtHeadset instance used to connect and disconnect headset afterwards

            Intent intent = new Intent();
            intent.setAction("HEADSET_INTERFACE_CONNECTED");
            //same as the one we register earlier for broadcastreciever
            mycontext.sendBroadcast(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBtHeadset = null;
        }

    };

    public void lock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            DevicePolicyManager policy = (DevicePolicyManager)
                    getSystemService(Context.DEVICE_POLICY_SERVICE);
            try {
                policy.lockNow();
            } catch (SecurityException ex) {
                Toast.makeText(
                        this,
                        "must enable device administrator",
                        Toast.LENGTH_LONG).show();
                ComponentName admin = new ComponentName(this, AdminReceiver.class);
                Intent intent = new Intent(
                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                this.startActivity(intent);
            }
        }
    }

    public String getPrefExtSd(){
        SharedPreferences preferences = mycontext.getSharedPreferences(mycontext.getString(R.string.preference_extsd_path), Context.MODE_PRIVATE);
        return preferences.getString(mycontext.getString(R.string.preference_extsd_path), "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handlerStartBtAdapter!=null && runnableSerachDeviceConected!=null){
            handlerStartBtAdapter.post(runnableSerachDeviceConected);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handlerStartBtAdapter!=null ){
            handlerStartBtAdapter.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "quitando receiver", Toast.LENGTH_SHORT).show();
        unregisterReceiver(serviceActia);
    }
}

