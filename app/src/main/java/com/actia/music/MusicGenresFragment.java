package com.actia.music;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_file.FileExtensionFilterMusic;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class MusicGenresFragment extends Fragment implements MusicGenresRecyclerAdapter.ClickListenerGenreMusic {
    private static final String ARG_PATH_IMAGE = "argPIM";

    private int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    private String[] genreMusic;
    private final int noColumns = 4;
    ConfigMasterMGC configSingleton;


    public MusicGenresFragment() {
    }

    public static MusicGenresFragment newInstance(int positionCategory) {
        MusicGenresFragment fragment = new MusicGenresFragment();
        Bundle args = new Bundle();
        args.putInt(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            positionCategory = getArguments().getInt(ConstantsApp.ARG_POSITION_CATEGORY);
        }
        genreMusic = getAllGenre();
        if (genreMusic!=null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES){
            Arrays.sort(genreMusic);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_genre_music, container, false);

        RecyclerView recycGridMusic = root.findViewById(R.id.recycGridMusic);
        /*GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            //FIXME: Acomodo normal
            gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        } else {
            //FIXME: acomodo Custom
            gridLayoutManager = new OrderElementsFourThree(getActivity(), genreMusic.length).getGridManagerVtwo();
        }*/


        //Config Adapter
        MusicGenresRecyclerAdapter genreMusicAdapter = new MusicGenresRecyclerAdapter(getActivity(), genreMusic);
        genreMusicAdapter.setOnItemGenreMusicListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;


        recycGridMusic.setLayoutManager(layoutManager);
/*
        int space = 0;
        switch (genreMusicAdapter.arrayGenreMusic.length) {
            case 1:
                recycGridMusic.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.oneCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_1);
                break;
            case 2:
                recycGridMusic.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.twoCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            case 3:
                recycGridMusic.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.threeCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            case 4:
                recycGridMusic.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.fourCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            default:
                recycGridMusic.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.fiveCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;
            *//*default:
                rvContainerCategories_fgmntct.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.fiveCategories);
                space = getResources().getDimensionPixelSize(R.dimen.space_cover_5);
                break;*//*
        }


        recycGridMusic.addItemDecoration(new SpacesItemDecoration(space));
        //add all to RecyclerView
        //recycGridMusic.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0, 0, 0, 30));
        recycGridMusic.setAdapter(genreMusicAdapter);*/

        if(dpHeight > 800 && dpWidth > 1400) {
            recycGridMusic.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(75, 75, 35, 35));
        }
        else if (dpHeight < 800 && dpWidth < 1400){
            recycGridMusic.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(50, 50, 17, 17));
        }

        recycGridMusic.setAdapter(genreMusicAdapter);

        return root;
    }

    /**
     * Get music genres from the external sdcard.
     *
     * @return array string with genres.
     */
    public String[] getAllGenre() {
        configSingleton = ConfigMasterMGC.getConfigSingleton();
        File home = new File(configSingleton.getMUSIC_PATH());
        ArrayList<String> Genre = new ArrayList<>();
        File[] files = home.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isDirectory() && file.listFiles(new FileExtensionFilterMusic()).length > 0) {
                    Genre.add(file.getName());
                }
            }
        }
        return Genre.toArray(new String[Genre.size()]);
    }

    @Override
    public void OnItemClickGenreMusic(View view, int position) {
        Intent intent = new Intent(getActivity(), PlayMusicActivity.class);

        String pathIcon = configSingleton.getMUSIC_PATH() + "/" + genreMusic[position] + "/" + genreMusic[position] + ".png";
        intent.putExtra(PlayMusicActivity.ARG_GENRE_MUSIC, genreMusic[position]);
        intent.putExtra(PlayMusicActivity.ARG_PATHIMG, pathIcon);
        intent.putExtra(ConstantsApp.ARG_POSITION_CATEGORY, positionCategory);
        startActivity(intent);
    }
}
