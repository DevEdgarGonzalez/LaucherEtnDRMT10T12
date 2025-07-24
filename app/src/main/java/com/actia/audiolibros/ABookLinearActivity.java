package com.actia.audiolibros;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.MyMediaPlayerService;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Display the general audio books from FragmentActivity
 * The genres are contents in the directory abooks (root: /mnt/extsd)
 * The genres are represented by directories and each directory
 * has audio files,the album covers and the audio files' synopsis
 * in a text file.
 *
 */

public class ABookLinearActivity extends BaseActivity implements  BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader, BaseActivity.FuctionRandomListener, ABookFragment.OnAbookFragmentListener{

    private String pathRoot = "genre";
    private final String TAG = this.getClass().getSimpleName();

    private final int positionCategory= ConstantsApp.CATEGORY_NO_DETECTED;
    private final boolean isSubMenu= false;

    private String pathImgCategory="";
    private String titleCategory="";
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abook_general);


        if (isConnectedSdCard()){

            //validation EmptyData
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                pathImgCategory =  bundle.getString(ConstantsApp.ARG_IMG_SUBGENRE);
                titleCategory = bundle.getString(ConstantsApp.ARG_TITLE_SUBGENRE);
            }

            ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();

            pathRoot = configMasterMGC.AUDIOBOOKS_PATH + "/" + configMasterMGC.getNameGenreDefaultAbooks();

            startTopBar(this, titleCategory, mutableDrawableFromPath(pathImgCategory),this);

            loadGenreSelected();

        }

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(ABookLinearActivity.this);
                }
            }
        });

    }





    private void loadGenreSelected(){

        Bundle genero_arguments = new Bundle();
        genero_arguments.putInt(ConstantsApp.ARG_POSITION_CATEGORY,positionCategory);
        genero_arguments.putString(ABookFragment.ARG_GENRE_ABOOK, pathRoot);

        ABookFragment grid_movies = new ABookFragment();
        grid_movies.setArguments(genero_arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelytListAbookGnral, grid_movies).commit();
    }


    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBar(this,positionCategory-1);
        this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBar(this, positionCategory+1);
        this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void randomInteraction() {
        ReadFileExternalStorage extStorage = new ReadFileExternalStorage();
        ArrayList<AudioBook> listABooks = extStorage.getAudioBooks(titleCategory, false);
        Random r = new Random();
        int i1 = r.nextInt(listABooks.size()) ;


        Intent intent = new Intent(ABookLinearActivity.this, PlayAbookActivity.class);
        intent.putExtra(PlayAbookActivity.ARG_CURRENT_ABOOK, (Serializable) listABooks.get(i1));
        intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
        intent.putExtra(ConstantsApp.ARG_IS_SUBMENU, isSubMenu);

        intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, pathImgCategory);
        intent.putExtra(ConstantsApp.ARG_TITLE_SUBGENRE, titleCategory);

        startActivity(intent);
    }

    @Override
    public void onAbookSelected(AudioBook aBook) {
        if (aBook.isAudioBook()) {
            if (isMyMediaPlayerServiceRunning()) { //Verifica si el servicio de música esta corriendo
                Intent intent = new Intent(this, MyMediaPlayerService.class);
                intent.addCategory(MyMediaPlayerService.TAG);
                stopService(intent);
            }
            Intent intent = new Intent(this, PlayAbookActivity.class);
            intent.putExtra(PlayAbookActivity.ARG_CURRENT_ABOOK, (Serializable) aBook);
            intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
            intent.putExtra(ConstantsApp.ARG_IS_SUBMENU, isSubMenu);

            intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, pathImgCategory);
            intent.putExtra(ConstantsApp.ARG_TITLE_SUBGENRE, titleCategory);
            startActivity(intent);
        }
    }


    private boolean isMyMediaPlayerServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyMediaPlayerService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(ABookLinearActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(ABookLinearActivity.this);
    }
}
