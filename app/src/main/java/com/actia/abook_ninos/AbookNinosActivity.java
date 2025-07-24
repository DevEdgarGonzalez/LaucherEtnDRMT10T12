package com.actia.abook_ninos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.actia.audiolibros.AudioBook;
import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_order_arrays.AbookComparator;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_external_storage.CheckExternalStorage;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AbookNinosActivity extends BaseActivity implements AbookNinosRecyclerAdapter.ClickListenerAbookChild, BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader, BaseActivity.FuctionRandomListener {
    private final String TAG = this.getClass().getSimpleName();
    RecyclerView recGridvAbookChild;


    ReadFileExternalStorage fileStorage;
    private ConfigMasterMGC configSingleton;
    private ArrayList<AudioBook> audioBooks;
    private LinearLayout llytParentAbookChild;

    ItemsHome itemCategory;
    int positionCategory;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abook_child);

        getInfoCategory();
        startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(itemCategory), ContextCompat.getDrawable(this, R.drawable.ico_books_child), this);
        startElementsUI();
        loadAbooks();
        if (audioBooks==null){
            this.finish();
            return;
        }
        addAdapters();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(AbookNinosActivity.this);
                }
            }
        });

    }


    private void getInfoCategory() {

        for (int i = 0; i < ContextApp.categoriesChildren.size(); i++) {
            if (ContextApp.categoriesChildren.get(i).getClassName().contains(this.getClass().getName())) {
                itemCategory = ContextApp.categoriesChildren.get(i);
                positionCategory = i;
                return;
            }
        }

    }

    private void loadAbooks() {
        CheckExternalStorage sdcard = new CheckExternalStorage();
        fileStorage = new ReadFileExternalStorage();
        String stateSDCard = sdcard.getExternalStorageState();
        configSingleton = ConfigMasterMGC.getConfigSingleton();
        this.audioBooks = fileStorage.getAudioBooks(configSingleton.getAUDIOBOOKSCHILDREN_PATH(), true);
        if (this.audioBooks != null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES) {
            Collections.sort(this.audioBooks, new AbookComparator());
        }

        /*if (audioBooks != null && audioBooks.size() > 0) {
            int tam = audioBooks.size();
            if (tam > 0) {

                int quotient = tam / 5;
                int remainder = tam % 5;//residuo

                int finalTam;

                if (remainder == 0)
                    finalTam = quotient * 5;
                else finalTam = (quotient + 1) * 5;

                for (int i = tam; i < finalTam; i++)
                    audioBooks.add(new AudioBook(0, null, null, null, null, null, false));
            }
        }*/
    }

    private void addAdapters() {
        AbookNinosRecyclerAdapter abookAdapter = new AbookNinosRecyclerAdapter(audioBooks, this);
        abookAdapter.setClickListenerAbookChild(this);

        //get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        } else {
            gridLayoutManager = new OrderElementsFourThree(this, audioBooks.size()).getGridManagerVtwo();
        }

        recGridvAbookChild.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0, 0, 0, 0));
        recGridvAbookChild.setLayoutManager(gridLayoutManager);
        recGridvAbookChild.setAdapter(abookAdapter);
    }

    private void startElementsUI() {
        recGridvAbookChild = findViewById(R.id.recvGridAbookChild);
        llytParentAbookChild = findViewById(R.id.llytParentAbookChild);

    }


    @Override
    public void setOnClickListener(View view, int position) {
        AbookNinosWindowPopUp popUpAbookChild = new AbookNinosWindowPopUp(this, audioBooks.get(position));
//        popUpAbookChild.showPopup(audioBooks,position,llytParentAbookChild,recGridvAbookChild);
        popUpAbookChild.showPopUp();

    }

    @Override
    public void randomInteraction() {
        ArrayList<AudioBook> listABooks = audioBooks;
        Random r = new Random();
        int i1 = r.nextInt(listABooks.size());

        AbookNinosWindowPopUp popUpAbookChild = new AbookNinosWindowPopUp(this, audioBooks.get(i1));
        popUpAbookChild.showPopUp();

    }

    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBarChildren(AbookNinosActivity.this, positionCategory - 1);
        AbookNinosActivity.this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBarChildren(AbookNinosActivity.this, positionCategory + 1);
        AbookNinosActivity.this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        this.finish();

    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(AbookNinosActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(AbookNinosActivity.this);
    }
    /*
     @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
     */
}
