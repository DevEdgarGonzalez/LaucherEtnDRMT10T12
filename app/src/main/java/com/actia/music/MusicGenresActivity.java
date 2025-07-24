package com.actia.music;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utils_language.UtilsLanguage;

public class MusicGenresActivity extends BaseActivity implements BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader{
    private final String TAG = this.getClass().getSimpleName();

    private int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_music);


        category = getCategoryFromExtras(getIntent().getExtras());

        if (category==null) {
            onBackPressed();
            return;
        }
        positionCategory = getPositionCategory(category, this.getClass().getName());

        startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(category), mutableDrawableFromPath(category.getPathImg()), this);
        addFragments();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(MusicGenresActivity.this);
                }
            }
        });
    }



    private void addFragments() {
        MusicGenresFragment musicGenresFragment = MusicGenresFragment.newInstance(positionCategory);
        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerGenreMusic, musicGenresFragment).commit();
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
        home();
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(MusicGenresActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(MusicGenresActivity.this);
    }
}
