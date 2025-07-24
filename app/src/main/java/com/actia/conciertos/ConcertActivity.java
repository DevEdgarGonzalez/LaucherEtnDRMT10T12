package com.actia.conciertos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.utilities.utilities_animations.WaveCategoryAdapter;
import com.actia.mexico.launcher_t12_generico_2018.R;

import org.lucasr.twowayview.TwoWayView;

public class ConcertActivity extends BaseActivity implements BaseActivity.CategoryNavigationListener,BaseActivity.FuctionRandomListener {
    private final String TAG = this.getClass().getSimpleName();
    private TwoWayView twvAnimatedImageConcert;

    private int positionCategory= ConstantsApp.CATEGORY_NO_DETECTED;
    private String pathImgCategory="";
    private String titleCategory="";
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conciertos);

        getInfoCategory();
        startComponentsUI();
        addAdapters();
        addFragments();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(ConcertActivity.this);
                }
            }
        });

    }

    private void getInfoCategory() {
        for (int i = 0; i< ContextApp.categoriesApp.size(); i++){
            if (ContextApp.categoriesApp.get(i).getClassName().contains(this.getClass().getName())){
                positionCategory = i;
                pathImgCategory = ContextApp.categoriesApp.get(positionCategory).getPathImg();
                titleCategory = ContextApp.categoriesApp.get(positionCategory).getTitle();
                return;
            }
        }
    }

    private void startComponentsUI() {
        twvAnimatedImageConcert = findViewById(R.id.twvAnimatedImageConcert);
    }



    private void addAdapters() {
        WaveCategoryAdapter waveAdapter = new WaveCategoryAdapter(this, pathImgCategory);
        twvAnimatedImageConcert.setAdapter(waveAdapter);
    }
    private void addFragments() {
        Bundle bundle = new Bundle();
//        bundle.putString(ConcertFragment.ARG_GNRO, ConstantesGeneric_t10_t12.EXT_VALUE_GNRO_CONCERT);    //Add Genero Movies "conciertos"

        ConcertFragment concertFragment = new ConcertFragment();
        concertFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerConcerts, concertFragment).commit();
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
    public void onBackPressed() {
        home();
    }


    @Override
    public void randomInteraction() {

    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(ConcertActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(ConcertActivity.this);
    }
}
