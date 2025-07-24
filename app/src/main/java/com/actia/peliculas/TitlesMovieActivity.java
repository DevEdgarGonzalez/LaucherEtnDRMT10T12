package com.actia.peliculas;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.File;

public class TitlesMovieActivity extends BaseActivity implements BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader,BaseActivity.FuctionRandomListener {
    public static final String ARG_GNRE_MOVIE = "gnre";
    public static final String ARG_PAHT_IMG = "pathImage";
    public static final String ARG_PATH_ROOT = "rootPath";


    String genreMovies;
    String pathImgCat;
    private String path;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titles_movie);

        Bundle extras = getIntent().getExtras();


        getInfoExtras();
        //startBottomBar(this, this,BAR_ALL,this);
        startTopBar(this, UtilsLanguage.getName(genreMovies), mutableDrawableFromPath(pathImgCat),this);

        if (!TextUtils.isEmpty(extras.getString(ConstantsApp.ARG_MODULO))) {
            backButtonVisibility(false);
        }
        startComponentsUI();
        addFragments();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(TitlesMovieActivity.this);
                }
            }
        });

    }

    private void getInfoExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            genreMovies = extras.getString(ARG_GNRE_MOVIE);
            pathImgCat = extras.getString(ARG_PAHT_IMG);
            path = extras.getString(ARG_PATH_ROOT);
            positionCategory = extras.getInt(ConstantsApp.ARG_POSITION_CATEGORY);
        }
    }

    private void startComponentsUI() {
    }


    private void addFragments() {
        TitlesMovieFragment movieTitles = TitlesMovieFragment.newInstance(genreMovies,path);
        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerTitlesMov, movieTitles).commit();

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
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void randomInteraction() {
        loadMoviesRandomByPath(path);

    }

    protected void onResume() {
        super.onResume();
        hideNavigationBar(TitlesMovieActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(TitlesMovieActivity.this);
    }

}
