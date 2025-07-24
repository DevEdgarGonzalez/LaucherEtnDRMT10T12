package com.actia.peliculas;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ContextApp;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_file.FileExtensionFilterForFile;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.File;
import java.util.Arrays;

public class PeliculasActivity extends BaseActivity implements BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader, BaseActivity.FuctionRandomListener {
    private final String TAG = this.getClass().getSimpleName();
    private final String backStateName = this.getClass().getName();
    private ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();
    private String modulo;

    private final String title ="";
    private String pathImg ="";
    private String rootPath="";
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas);


        Bundle extras = getIntent().getExtras();
        this.pathImg = extras.getString(ConstantsApp.ARG_IMG_SUBGENRE);

        if (!TextUtils.isEmpty(extras.getString(ConstantsApp.ARG_MODULO))) {
            modulo = extras.getString(ConstantsApp.ARG_MODULO);
        }
        startTopBar(this, "", mutableDrawableFromPath(this.pathImg), this);
        selectCategoryMovieSelected();


        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(PeliculasActivity.this);
                }
            }
        });
    }


    private void selectCategoryMovieSelected() {
        ReadFileExternalStorage read = new ReadFileExternalStorage();
        String[] genre;
        Bundle genero_arguments = new Bundle();

        if (modulo == null) {
            genre = read.getGenresMovie(true);
            if (ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES) {
                Arrays.sort(genre);
            }

            genero_arguments.putStringArray(GenreMovieFragment.ARG_GNRE_MOVIE, genre);

            rootPath= config.getVIDEO_PATH();
            genero_arguments.putString("rootPath", rootPath);

            genero_arguments.putInt(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
            GenreMovieFragment genreMovieFragment = new GenreMovieFragment();
            genreMovieFragment.setArguments(genero_arguments);

            getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerGenreMovies, genreMovieFragment).addToBackStack(backStateName).commit();
        } else {
            if (!modulo.isEmpty()) {
                config = ConfigMasterMGC.getConfigSingleton();

                rootPath= config.getVIDEO_PATH();
                File dirPathArg = new File(rootPath, modulo);

                Intent intent;
                File[] listFiles = dirPathArg.listFiles(new FileExtensionFilterVideo());
                String pathIcon = config.getVIDEO_PATH() + "/" + modulo + "/" + modulo + ".png";
                //Carpetas
                if (listFiles != null && listFiles.length > 0) {
                    if (listFiles.length>0){
                        intent = new Intent(context, TitlesMovieActivity.class);
                        intent.putExtra(TitlesMovieActivity.ARG_PATH_ROOT, dirPathArg.getParent());
                        intent.putExtra(TitlesMovieActivity.ARG_GNRE_MOVIE, modulo);
                        intent.putExtra(TitlesMovieActivity.ARG_PAHT_IMG, pathIcon);
                        intent.putExtra(ConstantsApp.ARG_MODULO, modulo);
                        intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
                        startActivity(intent);
                    }

                } else {
                    //Cuando no ahy carpetas en ese dir
                    String[] categories = read.getGenresByName(modulo);

                    if (categories != null && categories.length > 0) {

                        genre = read.getGenresMovieByPath(dirPathArg);

                        genero_arguments.putString("rootPath", rootPath);
                        genero_arguments.putStringArray("genre", categories);
                        genero_arguments.putInt(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);

                        GenreMovieFragment grid_movies = new GenreMovieFragment();
                        grid_movies.setArguments(genero_arguments);
                        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerGenreMovies, grid_movies).addToBackStack(backStateName).commit();
                    } else {
                        this.finish();
                        Toast.makeText(this, R.string.no_content, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
//                MovieGenres.this.finish();
            }

        }
    }

    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBar(this, positionCategory - 1);
        this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBar(this, positionCategory + 1);
        this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void randomInteraction() {
        loadMoviesRandomByPath(rootPath);
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(PeliculasActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(PeliculasActivity.this);
    }
}
