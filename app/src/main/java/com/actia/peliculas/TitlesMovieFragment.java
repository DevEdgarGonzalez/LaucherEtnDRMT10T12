package com.actia.peliculas;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_order_arrays.MovieComparator;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;
import java.util.Collections;

public class TitlesMovieFragment extends Fragment implements TitlesMovieAdapter.ClickListenerTitleMovie{

    private static final String ARG_GNRE_MOVIE = "ARG_GNRE_MOVIE";
    private static final String ARG_PATH = "path";

    private RecyclerView recycGridTileMovies;


    private String mGenreMovie;
    private String path;
    ArrayList<Movie> movies;

//    private OnFragmentInteractionListener mListener;


    public TitlesMovieFragment() {
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param genreMovie all the movies will be searched in the folder in SD of the corresponding genre
     * @return A new instance of fragment TitlesMovieFragment.
     */
    public static TitlesMovieFragment newInstance(String genreMovie, String pathRoot) {
        TitlesMovieFragment fragment = new TitlesMovieFragment();
        Bundle args = new Bundle();
        args.putString(TitlesMovieFragment.ARG_GNRE_MOVIE, genreMovie);
        args.putString(TitlesMovieFragment.ARG_PATH, pathRoot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGenreMovie = getArguments().getString(ARG_GNRE_MOVIE);
            path = getArguments().getString(ARG_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_movie_titles, container, false);
        recycGridTileMovies = root.findViewById(R.id.recycGridTileMovies);
        ReadFileExternalStorage external=new ReadFileExternalStorage();
        if(mGenreMovie!=null) {
            movies = external.getMovieByGenreByPath(path,mGenreMovie, getActivity());

            if (movies!=null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES){
                Collections.sort(movies,new MovieComparator());
            }

            TitlesMovieAdapter titlesMovieAdapter =  new TitlesMovieAdapter(movies,getActivity());
            titlesMovieAdapter.setOnClickListenerTitleMov(this);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            if(dpHeight > 800 && dpWidth > 1400) {
                recycGridTileMovies.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(75, 75, 35, 35));
            }
            else if (dpHeight < 800 && dpWidth < 1400){
                recycGridTileMovies.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(50, 50, 17, 17));
            }

            GridLayoutManager gridLayoutManager;
            if (ContextApp.enableCustomization == false) {
                //FIXME: Acomodo normal
                gridLayoutManager = new GridLayoutManager(getActivity(),3);
            } else {
                //FIXME: acomodo Custom
                gridLayoutManager = new OrderElementsFourThree(getActivity(), movies.size()).getGridManagerVtwo();
            }



//            gridLayoutManager.setSpanSizeLookup(UtilitiesRecyclerView.getSpanSizeTresDos(titlesMovieAdapter.getItemCount()));
            recycGridTileMovies.setLayoutManager(gridLayoutManager);
            recycGridTileMovies.setAdapter(titlesMovieAdapter);


        }


        return root;
    }

    @Override
    public void OnItemClick(View view, int position) {
            ImageView imageView = view.findViewById(R.id.imgvItemTitleMovie);
            Movie movie = movies.get(position);
            LinearLayout linlay = getActivity().findViewById(R.id.llytParentTitlesMov);
            TitlesMoviePopUp titlesMoviePopUp= new TitlesMoviePopUp(getActivity());
            titlesMoviePopUp.showPopPup(movie, imageView, linlay);
    }
}
