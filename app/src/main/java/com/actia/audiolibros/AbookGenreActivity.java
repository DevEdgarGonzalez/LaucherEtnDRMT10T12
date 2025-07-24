package com.actia.audiolibros;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.home_categories.MainActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_file.FileExtensionFilterForFile;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class AbookGenreActivity extends BaseActivity implements OnAbookGenreListener, BaseActivity.CategoryNavigationListener,BaseActivity.FuctionRandomListener,BaseActivity.PressBackHeader{
    private final String TAG = this.getClass().getSimpleName();
    private String modulo = null;
    private final ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();
    private RecyclerView rvGenres_actag;

    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abook_genre);

        category = getCategoryFromExtras(getIntent().getExtras());
        if (category==null) {
            onBackPressed();
            return;
        }

        loadInitialData();

        File rootPathAbook = new File(config.getAUDIOBOOKS_PATH(), modulo);

        //Filtro si existe contenido
        File[] allFilesInDir = rootPathAbook.listFiles();
        if (allFilesInDir == null || allFilesInDir.length == 0) {
            Toast.makeText(this, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
            goHome();
            return;
        }


        File[] dirs = rootPathAbook.listFiles(new FileExtensionFilterForFile());
        if (dirs != null && dirs.length > 0) {
            //llena el recycler con generos
            loadRecycler(dirs);

        } else {
            //si no existen Dirs
            goNextScreen( rootPathAbook.getPath(),rootPathAbook.getName(), category.getPathImg());
            this.finish();
        }

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(AbookGenreActivity.this);
                }
            }
        });
    }

    private void loadInitialData() {
        positionCategory = getPositionCategory(category, this.getClass().getName());



        if (category.getPackageName().isEmpty()){
            modulo = config.getNameGenreDefaultAbooks();
        }else{
            modulo = category.getPackageName();
        }

        startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(category),mutableDrawableFromPath(category.pathImg),this);
    }

    private void loadRecycler(File[] dirs) {
        rvGenres_actag = findViewById(R.id.rvContainer_actag);
        rvGenres_actag.setAdapter(new AbookGenreAdapter(this, dirs,this));



        //get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (!ContextApp.enableCustomization) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        } else {
            gridLayoutManager = new OrderElementsFourThree(this, dirs.length).getGridManagerVtwo();
        }

        rvGenres_actag.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0, 0, 0, 40));
        rvGenres_actag.setLayoutManager(gridLayoutManager);
    }


    private void goNextScreen(String rootCategoryPath, String name, String pathImg){
        Intent intent = new Intent(this, ABookLinearActivity.class);

        intent.putExtra(ConstantsApp.ARG_ROOT_PATH_SUB_GENRE,rootCategoryPath);

        intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
        intent.putExtra(ConstantsApp.ARG_IS_SUBMENU, category.isSubMenu());

        intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, pathImg);
        intent.putExtra(ConstantsApp.ARG_TITLE_SUBGENRE, name);

        startActivity(intent);
    }

    @Override
    public void onClickCategory(File categorySelected, String pathImg) {
        goNextScreen(categorySelected.getPath(),categorySelected.getName(),pathImg);
    }

    private void goHome() {

//        BooksActivity.this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        changeActivity(intent, true);
    }




    @Override
    public void previousCategory() {
        onBackPressed();
    }

    @Override
    public void nextCategory() {

    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    @Override
    public void randomInteraction() {
        ReadFileExternalStorage extStorage = new ReadFileExternalStorage();
        ArrayList<AudioBook> listABooks = extStorage.getAudioBooks(SpinAdapterGenre.Genres[0], false);
        Random r = new Random();
        int i1 = r.nextInt(listABooks.size()) ;


        Intent intent = new Intent(this, PlayAbookActivity.class);
        intent.putExtra(PlayAbookActivity.ARG_CURRENT_ABOOK, (Serializable) listABooks.get(i1));
        intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);

        startActivity(intent);
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(AbookGenreActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(AbookGenreActivity.this);
    }
}

