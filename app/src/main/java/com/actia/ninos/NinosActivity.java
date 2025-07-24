package com.actia.ninos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.utilities_recycler.SpacesItemDecoration;
import com.actia.utilities.utilities_ui.UtilsFonts;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.util.ArrayList;
import java.util.HashMap;

public class NinosActivity extends BaseActivity implements BaseActivity.CategoryNavigationListener,BaseActivity.PressBackHeader {
    private final String TAG = this.getClass().getSimpleName();
    ArrayList<ImageView> alistImagesView = new ArrayList<>();

    private Handler handlerAnimation;
    private boolean flagAnimation = false;
    private int positionCategory= ConstantsApp.CATEGORY_NO_DETECTED;

    ConfigMasterMGC configSingleton;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninos);


        context = this;
        getCategoriesChildren();

        category = getCategoryFromExtras(getIntent().getExtras());
        if (category==null) {
            onBackPressed();
            return;
        }

        ContextApp.childrenCategory = category;
        positionCategory = getPositionCategory(category, this.getClass().getName());

        startTopBar(this, "", mutableDrawableFromPath(category.getPathImg()), this);
        startElementsUI();

        configAndDrawElements();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(NinosActivity.this);
                }
            }
        });

    }

    private void configAndDrawElements() {

        for (int i = 0; i < ContextApp.categoriesChildren.size(); i++) {
            if (alistImagesView.size() <= i) break;

            drawOne(alistImagesView.get(i), ContextApp.categoriesChildren.get(i));
            Log.d("TAG", "configAndDrawElements (" + i + "): alistImagesView: " + alistImagesView.get(i).getId() + " categoriesChildren: " + ContextApp.categoriesChildren.get(i).getTitle());
        }

    }

    private void drawOne(ImageView aImageView, final ItemsHome itemsHomeChildren) {

        aImageView.setImageDrawable(Drawable.createFromPath(itemsHomeChildren.getPathImg()));
        aImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClassName(NinosActivity.this, itemsHomeChildren.getClassName());
                new AsyncIntents(NinosActivity.this, false).execute(intent);
            }
        });

    }

    private void startElementsUI() {
        int size = ContextApp.categoriesChildren.size();

        LinearLayout contentCategories = findViewById(R.id.contentCategories);

        LinearLayout llItem01Children = findViewById(R.id.llItem01Children);
        ImageView imgvItem01Children = findViewById(R.id.imgvItem01Children);
        if (size<1) llItem01Children.setVisibility(View.GONE);
        alistImagesView.add(imgvItem01Children);

        LinearLayout llItem02Children = findViewById(R.id.llItem02Children);
        ImageView imgvItem02Children = findViewById(R.id.imgvItem02Children);
        if (size<2) llItem02Children.setVisibility(View.GONE);
        alistImagesView.add(imgvItem02Children);

        LinearLayout llItem00Children = findViewById(R.id.llItem00Children);
        ImageView imgvItem00Children = findViewById(R.id.imgvItem00Children);
        if (size<3) llItem00Children.setVisibility(View.GONE);
        alistImagesView.add(imgvItem00Children);

        LinearLayout llItem03Children = findViewById(R.id.llItem03Children);
        ImageView imgvItem03Children = findViewById(R.id.imgvItem03Children);
        if (size<4) llItem03Children.setVisibility(View.GONE);
        alistImagesView.add(imgvItem03Children);

    }


    private void getCategoriesChildren() {
        configSingleton = ConfigMasterMGC.getConfigSingleton();
        if (ContextApp.categoriesChildren == null) {
            HashMap<Integer, ItemsHome> catgoriesLoad = new HashMap<>();
            int count = 0;
            ArrayList<ItemsHome> alistChildrens = configSingleton.getAppHomeKids("childrens");
            for (ItemsHome category : alistChildrens) {

//                category.setImgBar(getPathImgBar(category.getPathImg()));  //fun: setBarCategory
                String className = category.getClassName();
                if (category.getClassName() != null && className.length() > 5) {
                    catgoriesLoad.put(count++, category);
                }
            }
            ContextApp.categoriesChildren = catgoriesLoad;

        }
    }


    private void addAnimations() {
        final Animation animABC = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        final int size = ContextApp.categoriesChildren.size();

        handlerAnimation = new Handler();
        handlerAnimation.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flagAnimation) {
                    if (size>0) alistImagesView.get(0).startAnimation(animABC);
                    if (size>2) alistImagesView.get(2).startAnimation(animABC);
                } else {
                    if (size>1) alistImagesView.get(1).startAnimation(animABC);
                    if (size>3) alistImagesView.get(3).startAnimation(animABC);
                }

                flagAnimation = !flagAnimation;
                handlerAnimation.postDelayed(this, 2000);
            }
        }, 2000);


    }


    @Override
    public void previousCategory() {
        /*home();*/
        goCategorySelectedInBottomBar(this,positionCategory-1);
        this.finish();

    }

    @Override
    public void nextCategory() {
       /* Intent intent = new Intent(NinosActivity.this, MoviesNinosActivity.class);
        changeActivity(intent, true);*/
        goCategorySelectedInBottomBar(this, positionCategory+1);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        home();
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
        hideNavigationBar(NinosActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(NinosActivity.this);
    }
}
