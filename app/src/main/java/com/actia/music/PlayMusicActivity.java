package com.actia.music;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.selection_genre.GenreMusicFragment;
import com.actia.utilities.utilities_external_storage.CheckExternalStorage;
import com.actia.mexico.generic_2018_t10_t12.ErrorActivity;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_media_player.UtilitiesGeneralMediaPlayer;
import com.actia.utilities.utilities_order_arrays.SongComparator;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayMusicActivity extends BaseActivity implements ControlsMusicFragment.CallChangeItemsListMsuic, SongsListFragment.Callbacks, GenreMusicFragment.CallbacksGeneros, BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader, BaseActivity.FuctionRandomListener {
    private final String TAG = this.getClass().getSimpleName();
    public static String ARG_GENRE_MUSIC = "genrem";
    public static String ARG_PATHIMG = "path";

    static public MyMediaPlayerService mServ;
    public static ArrayList<Song> GlobalSongs;
    static boolean mIsBound = false;

    SongsListFragment lista_canciones;

    public static String[] Genre = null;


    private int positionCategory;
    private String pathImage;
    private String genreMusic;

    ReadFileExternalStorage FilesExternal = new ReadFileExternalStorage();
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        MyMediaPlayerService.randomPlayer = false;
        getInfoExtras();
        startTopBar(this, UtilsLanguage.getName(genreMusic), mutableDrawableFromPath(pathImage), this);
        addListenersSensors_iSeat(mListenersPlayers);

        validateSDCard();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(PlayMusicActivity.this);
                }
            }
        });

    }

    private void getInfoExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            positionCategory = extras.getInt(ConstantsApp.ARG_POSITION_CATEGORY, ConstantsApp.CATEGORY_NO_DETECTED);
            genreMusic = extras.getString(ARG_GENRE_MUSIC);
            pathImage = extras.getString(ARG_PATHIMG);
        }

    }

    private void validateSDCard() {
        CheckExternalStorage sdcard = new CheckExternalStorage();
        String stateSDCard = sdcard.getExternalStorageState();
        ReadFileExternalStorage fileStorage = new ReadFileExternalStorage();

        if (stateSDCard.equals(ConstantsApp.STATE_SD_CARD_MOUNTED)) {
            ConfigMasterMGC singletonConfig = ConfigMasterMGC.getConfigSingleton();

            if (fileStorage.existDirectory(singletonConfig.getMUSIC_PATH())) {

                if (!UtilitiesGeneralMediaPlayer.isMyMediaPlayerServiceRunning(this)) {//Check if a service media player is running
                    Intent intentMediaPlayer = new Intent(this, MyMediaPlayerService.class);
                    intentMediaPlayer.putExtra(MyMediaPlayerService.START_PLAY, true);
                    startService(intentMediaPlayer);
                }
            } else
                Toast.makeText(getApplicationContext(), getString(R.string.error_no_music_path), Toast.LENGTH_SHORT).show();
        } else {
            Intent errorIntent = new Intent(this, ErrorActivity.class);
            errorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            errorIntent.putExtra("state", getString(R.string.error_sd_not_mounted) + stateSDCard);
            PlayMusicActivity.this.finish();
            deleteAllElements();
            startActivity(errorIntent);
        }
    }


    public void mainPanels() {
        //Fixme: este bloque de codigo deberia mostrar en un grid los generos para que al seleccionar un genero sustituya este GenreMusicFragment por la lista de canciones (SongsListFragment)
        //Fixme: las siguientes 5 lineas se pueden sustituir solo por lo que esta en el metodo sobreescrito: "onGeneroSelecionada" pasandole el genero "genreMusic"
        //Init Fragment genre
        GenreMusicFragment lista_generos = new GenreMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GEN", genreMusic);
        lista_generos.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerSongsPlayMusic, lista_generos).commit();

        //Init Fragment controles
        //En caso de tener un genero seleccionado anteriormente lo sustituye por el nuevo genero
        if (mServ.getNameGenre() != null) {
            Bundle arguments = new Bundle();
            arguments.putStringArray(
                    ControlsMusicFragment.ARG_ID_ENTRADA_SELECIONADA,
                    new String[]{
                            mServ.getNameGenre(), Integer.toString(mServ.getcurrentIdSong()), "0"});
            ControlsMusicFragment fragment = new ControlsMusicFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerControlsPlayMusic, fragment).commit();
        }
    }

    @Override
    public void onEntradaSelecionada(String[] id) {
        Bundle arguments = new Bundle();
        arguments.putStringArray(ControlsMusicFragment.ARG_ID_ENTRADA_SELECIONADA, id);
        ControlsMusicFragment fragment = new ControlsMusicFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerControlsPlayMusic, fragment).commit();

    }

    @Override
    public void onGeneroSelecionada(String id) {
        Bundle genero_arguments = new Bundle();
        genero_arguments.putString(SongsListFragment.ARG_ID_GENERO_SELECIONADO, genreMusic);
        lista_canciones = new SongsListFragment();
        lista_canciones.setArguments(genero_arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerSongsPlayMusic, lista_canciones).commit();

    }

    @Override
    public void onChangeItem(String genre, int index) {

        try {
            SongsListFragment Obj = (SongsListFragment) getSupportFragmentManager().findFragmentById(R.id.flytContainerSongsPlayMusic);
            Obj.setItemListCurrentSong(genre, index);
        } catch (Exception e) {
            Log.v("MusicActivity", e.toString());
        }
    }

    private final ServiceConnection Scon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mServ = ((MyMediaPlayerService.ServiceBinder) service).getService();
            if (mServ.getNameGenre() == null) {
                new loadGlobalSongs(PlayMusicActivity.this).execute();
            } else mainPanels();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
            Toast.makeText(getApplicationContext(), getString(R.string.disconnected_service), Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        bindService(new Intent(this, MyMediaPlayerService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    //I’ll jump up, I’ll take flight, fall through the cloudline

    @Override
    protected void onStart() {
        doBindService();
        super.onStart();
        Log.i("OnStart MusicActivity", "OnStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            MyMediaPlayerService.randomPlayer = false;
            UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(this);
        }
    }


    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
        android.os.Debug.stopMethodTracing();
        MyMediaPlayerService.randomPlayer = false;
    }

    @Override
    protected void onStop() {
        doUnbindService();
        super.onStop();
        Log.i("OnStop MusicActivity", "OnStop");
    }


    private class loadGlobalSongs extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        Context context;

        loadGlobalSongs(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(getString(R.string.load_wait_please));
//            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
//					Genre=FilesExternal.getAllGenre();
            try {
                GlobalSongs = FilesExternal.getSongsByGenre(genreMusic);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (GlobalSongs != null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES) {
                Collections.sort(GlobalSongs, new SongComparator());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            mainPanels();
            if (dialog.isShowing())
                dialog.dismiss();
        }

    }

    @Override
    public void previousCategory() {
        if (lista_canciones==null || lista_canciones.listSongs==null ){
            return;
        }
        goCategorySelectedInBottomBar(this, positionCategory - 1);
        this.finish();
    }

    @Override
    public void nextCategory() {
        if (lista_canciones==null || lista_canciones.listSongs==null ){
            return;
        }
        goCategorySelectedInBottomBar(this, positionCategory + 1);
        this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(this);
        if (lista_canciones==null || lista_canciones.listSongs==null ){
            return;
        }
        super.onBackPressed();
        this.finish();
    }
    @Override
    protected void goChildrensActivity() {
        if (lista_canciones==null || lista_canciones.listSongs==null ){
            return;
        }
        super.goChildrensActivity();
    }

    @Override
    public void helpFuction() {
        if (lista_canciones==null || lista_canciones.listSongs==null ){
            return;
        }
        super.helpFuction();
    }

    @Override
    protected void home() {
        if (lista_canciones==null || lista_canciones.listSongs==null ){
            return;
        }
        super.home();
    }

    @Override
    public void randomInteraction() {
        if (lista_canciones == null || lista_canciones.listSongs == null) {
            Log.d("cargaLista", "se puchurra pero lista canciones es nulla o lista canciones");
            return;
        } else if (lista_canciones.listSongs.isEmpty()) {
            Log.d("cargaLista", "se puchurra pero aun No hay nada de canciones");
            return;
        }
        if (!mServ.isMediaPlayerPlaying()) {
            //Fixme: para reproducir cualquier cancion de la lista actual
            if (lista_canciones != null && lista_canciones.listSongs != null) {
                Random random = new Random();
                int indexRandom = random.nextInt(lista_canciones.listSongs.size());
                lista_canciones.setSelection(indexRandom);
                lista_canciones.onListItemClick(lista_canciones.getListView(), lista_canciones.getView(), indexRandom, lista_canciones.getSelectedItemId());

            }
        }

        mServ.enableDisableRandom();

        if (MyMediaPlayerService.randomPlayer) {
            imgBtnRandomBotBar.setImageDrawable(getResources().getDrawable(R.drawable.ico_random_white));
//            imgBtnRandomBotBar.setTextColor(getResources().getColor(R.color.blue_bot_bar));
        } else {
            imgBtnRandomBotBar.setImageDrawable(getResources().getDrawable(R.drawable.ico_random_black));
//            tvRandomBotBar.setTextColor(getResources().getColor(R.color.white));
        }

        /*//Fixme: para crear de nuevo el fragmento con una lista nueva con las canciones en aleatorio, el problema es que no funciona cuando el tactil se suspende

            Bundle genero_arguments = new Bundle();
            genero_arguments.putString(SongsListFragment.ARG_ID_GENERO_SELECIONADO, genreMusic);
            genero_arguments.putBoolean(SongsListFragment.ARG_SHUFLE, true);
            lista_canciones = new SongsListFragment();
            lista_canciones.setArguments(genero_arguments);

        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerSongsPlayMusic, lista_canciones,ARG_TAG_FGMNT).commit();
        UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    lista_canciones.onListItemClick(lista_canciones.getListView(), lista_canciones.getView(), 0, lista_canciones.getSelectedItemId());

                }catch (IllegalStateException e){
                    e.printStackTrace();
                }

            }
        },2000);
*/
    }
    BaseActivity.ListenersPlayers mListenersPlayers = new ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            if (mServ==null){
                return;
            }
            if (isPassengerSitting && isSafetyBeltPlaced){
                if (!mServ.isMediaPlayerPlaying()){
                    mServ.resumeMusic(mServ.getCurrentTime());
                }

            }else {
                if (mServ.isMediaPlayerPlaying()){
                    mServ.pauseMusic();
                }
            }





        }
    };

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(PlayMusicActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(PlayMusicActivity.this);
    }
}



