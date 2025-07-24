package com.actia.games_ninos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import com.actia.games.GamesRecyclerAdapter;
import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ContextApp;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.infraestructure.ItemsHome;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.util.ArrayList;

public class GamesNinosActivity extends BaseActivity implements GamesRecyclerAdapter.ClickListenerGames, BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader {
    private final String TAG = this.getClass().getSimpleName();
    RecyclerView recyclerGamesNinos;
    ConfigMasterMGC config;
    private ArrayList<ItemsHome> alistGames = null;
    ItemsHome category;
    int positionCategoryChildren;
    int positionCategory;
    String pathImgcategory, pathBarCateory;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_ninos);

        getInfoCategory();
        startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(category), ContextCompat.getDrawable(this, R.drawable.ico_games_child),this);
        startElementsUI();
        startAdapters();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(GamesNinosActivity.this);
                }
            }
        });

    }

    private void getInfoCategory(){

        for (int i = 0; i< ContextApp.categoriesChildren.size(); i++){
            if (ContextApp.categoriesChildren.get(i).getClassName().contains(this.getClass().getName())){
                category= ContextApp.categoriesChildren.get(i);
                positionCategory = i;
                return;
            }
        }

//        positionCategory = getIntent().getExtras().getInt(ConstantesTitanium_t10_t12.ARG_POSITION_CATEGORY,0);
//        pathImgCategory = ContextoTitanium_t10_t12.categoriesChildren.get(positionCategory).getPathImg();
//        titleCategory = ContextoTitanium_t10_t12.categoriesChildren.get(positionCategory).getTitle();
    }
    private void startElementsUI() {
        recyclerGamesNinos = findViewById(R.id.recyclerGamesNinos);
    }

    @Override
    public void onItemClickGames(View view, int position) {
        launchIntent(alistGames.get(position), this);
    }

    public static void launchIntent(ItemsHome g1, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(g1.getPackageName(), g1.getClassName());
        changeActivity(activity, intent, false);
    }

    public static void changeActivity(Activity activity, Intent intent, boolean destroyActivity) {
        new AsyncIntents(activity, destroyActivity).execute(intent);
    }

    private void startAdapters() {
        config = ConfigMasterMGC.getConfigSingleton();
        alistGames = config.getKidsGames();

        GamesRecyclerAdapter gameAdapter = new GamesRecyclerAdapter(alistGames, this);

        recyclerGamesNinos.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0, 0, 10, 10));
        gameAdapter.setOnClickListenerGame(this);


//get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        } else {
            gridLayoutManager = new OrderElementsFourThree(this, alistGames.size()).getGridManagerVtwo();
        }


        recyclerGamesNinos.setLayoutManager(gridLayoutManager);

        recyclerGamesNinos.setAdapter(gameAdapter);
    }

    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBarChildren(GamesNinosActivity.this, positionCategory-1);
        GamesNinosActivity.this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBarChildren(GamesNinosActivity.this, positionCategory+1);
        GamesNinosActivity.this.finish();
    }
    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(GamesNinosActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(GamesNinosActivity.this);
    }
}
