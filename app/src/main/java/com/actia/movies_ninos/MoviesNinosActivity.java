package com.actia.movies_ninos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.utilities.utilities_order_arrays.MovieComparator;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.util.ArrayList;
import java.util.Collections;

public class MoviesNinosActivity extends BaseActivity implements MovieNinosAdapter.ClickListener, BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader, BaseActivity.FuctionRandomListener {
    private final String TAG = this.getClass().getSimpleName();
    RecyclerView recycGridMoviesChild;
    ArrayList<Movie> alistMovies;
    private ItemsHome itemCategory;
    private int positionCategory;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_ninos);
        getInfoCategory();
        startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(itemCategory), ContextCompat.getDrawable(this, R.drawable.ico_movies_child), this);


        recycGridMoviesChild = findViewById(R.id.recycGridMoviesChild);
        ReadFileExternalStorage external = new ReadFileExternalStorage();
        alistMovies = external.getMovieChildrenByGenre();

        if (alistMovies==null || alistMovies.isEmpty()){
            onBackPressed();
            Toast.makeText(context, "No hay peliculas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (alistMovies != null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES) {
            Collections.sort(alistMovies, new MovieComparator());
        }


        MovieNinosAdapter movieAdapter = new MovieNinosAdapter(alistMovies, this);

//get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        } else {
            gridLayoutManager = new OrderElementsFourThree(this, alistMovies.size()).getGridManagerVtwo();
        }


        movieAdapter.setOnclickListenerMovNinos(this);
        recycGridMoviesChild.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0, 0, 0, 20));
        recycGridMoviesChild.setAdapter(movieAdapter);
        recycGridMoviesChild.setLayoutManager(gridLayoutManager);

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(MoviesNinosActivity.this);
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


    @Override
    public void OnItemClickMovNinos(View vie, int position) {
        ImageView imageView = vie.findViewById(R.id.imgvItemTitleMovie);
        MovieNinosWindowPopUp movChildPopUp = new MovieNinosWindowPopUp(alistMovies.get(position), MoviesNinosActivity.this);
        movChildPopUp.showPopUp(imageView);
    }

    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBarChildren(MoviesNinosActivity.this, positionCategory - 1);
        MoviesNinosActivity.this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBarChildren(MoviesNinosActivity.this, positionCategory + 1);
        MoviesNinosActivity.this.finish();
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
        loadMoviesRandomChild();
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(MoviesNinosActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(MoviesNinosActivity.this);
    }
}
