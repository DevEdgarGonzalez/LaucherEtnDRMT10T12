package com.actia.peliculas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;

import java.io.File;


public class GenreMovieFragment extends Fragment implements GenreMovieRecyclerAdapter.MovieGenreClickListener {
    public static final String ARG_GNRE_MOVIE = "genre";
    public static final String ARG_ROOT_PATH = "rootPath";

    RecyclerView recycGridAllGeneros;
    String[] currentGenre = null;
    private String pathRoot ="";
    private String argumentRoot = "";
    private int positionCategory= ConstantsApp.CATEGORY_NO_DETECTED;

    public GenreMovieFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_GNRE_MOVIE)) {
            currentGenre = getArguments().getStringArray(ARG_GNRE_MOVIE);
            argumentRoot = getArguments().getString(ARG_ROOT_PATH);
            positionCategory = getArguments().getInt(ConstantsApp.ARG_POSITION_CATEGORY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_peliculas_genero, container, false);
        recycGridAllGeneros = root.findViewById(R.id.recycGridAllGeneros);
        //recycGridAllGeneros.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0,0,0,50));
        GenreMovieRecyclerAdapter genreMovieRecyclerAdapter = new GenreMovieRecyclerAdapter(currentGenre, argumentRoot, getActivity());
        genreMovieRecyclerAdapter.setClickListener(this);

        //get custom grid or grid normal
        /*GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        } else {
            gridLayoutManager = new OrderElementsFourThree(getActivity(), currentGenre.length).getGridManagerVtwo();
        }*/

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);

        recycGridAllGeneros.setLayoutManager(layoutManager);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if(dpHeight > 800 && dpWidth > 1400) {
            recycGridAllGeneros.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(75, 75, 35, 35));
        }
        else if (dpHeight < 800 && dpWidth < 1400){
            recycGridAllGeneros.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(50, 50, 17, 17));
        }


//        gridLayoutManager.setSpanSizeLookup(UtilitiesRecyclerView.getSpanSizeTresDos(genreMovieRecyclerAdapter.getItemCount()));
        /*int space = 0;
        switch (genreMovieRecyclerAdapter.alistGnreMovies.length) {
            case 1:
                recycGridAllGeneros.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.oneCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_1);
                break;
            case 2:
                recycGridAllGeneros.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.twoCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            case 3:
                recycGridAllGeneros.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.threeCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            case 4:
                recycGridAllGeneros.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.fourCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            default:
                recycGridAllGeneros.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.fiveCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            *//*default:
                rvContainerCategories_fgmntct.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.fiveCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;*//*
        }


        recycGridAllGeneros.addItemDecoration(new SpacesItemDecoration(space));*/
        recycGridAllGeneros.setAdapter(genreMovieRecyclerAdapter);

        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void OnItemClick(View view, int position) {

        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        String gen = currentGenre[position];
        File destiny;

        if(!gen.contains(ConstantsApp.nameCategoryChildren)){
            pathRoot = argumentRoot;
            destiny = new File(pathRoot,gen);
        }
        else{
            pathRoot = configMasterMGC.getVIDEO_CHILDREN_PATH();
            destiny = new File(configMasterMGC.getVIDEO_CHILDREN_PATH());
        }

        Intent intent;
        File[] listFiles = destiny.listFiles(new FileExtensionFilterVideo());
        String pathIcon = configMasterMGC.getVIDEO_PATH() + "/" + gen + "/" + gen + ".png";

        if(gen.contains(ConstantsApp.nameCategoryChildren)){
            pathIcon = configMasterMGC.getVIDEO_CHILDREN_PATH() + "/" + gen + ".png";
            gen = "";
        }

        try {
            if (listFiles.length>0){
                intent = new Intent(getActivity().getApplicationContext(), TitlesMovieActivity.class);
                intent.putExtra(TitlesMovieActivity.ARG_PATH_ROOT, pathRoot);
                intent.putExtra(TitlesMovieActivity.ARG_GNRE_MOVIE, gen);
                intent.putExtra(TitlesMovieActivity.ARG_PAHT_IMG, pathIcon);
                intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, pathIcon);
                intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
            }else{
                intent = new Intent(getActivity().getApplicationContext(), PeliculasActivity.class);
                intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
                intent.putExtra(ConstantsApp.ARG_TITLE_SUBGENRE,gen);
                intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, pathIcon);
            }

            startActivity(intent);
        } catch (Exception exception){
            Log.e("GenreMovieFragment", "OnItemClick: " + destiny.getAbsolutePath(), exception);
        }
    }

//    public interface  OnGenreMovieFragment
}
