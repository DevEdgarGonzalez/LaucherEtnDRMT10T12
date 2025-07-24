package com.actia.conciertos;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;

import java.util.ArrayList;


public class ConcertFragment extends Fragment implements ConcertRecyclerAdapter.ClickListenerConcert{
    public static final String ARG_GNRO = "GNRO";

    private LinearLayout llytParentConcert;

    private String currentGenre=null;
    ArrayList<Movie> movies;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_GNRO)) {
            currentGenre = getArguments().getString(ARG_GNRO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        llytParentConcert = getActivity().findViewById(R.id.llytParentConcert);

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_concert, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.recycGridConcerts);


//Fixme: Leee pelicualas de la SD
        ReadFileExternalStorage external = new ReadFileExternalStorage();
        movies = external.getMovieByGenre(currentGenre);

//Fixme: Add Adapters
        ConcertRecyclerAdapter adapterConcert = new ConcertRecyclerAdapter(getActivity(), movies);
        adapterConcert.setOnClickListenerConcert(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapterConcert);





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
    public void OnItemClickConcert(View view, int position) {

        ImageView imageView = view.findViewById(R.id.imgvCoverConcert);
        Movie movie = movies.get(position);
        LinearLayout llytParentConcert = getActivity().findViewById(R.id.llytParentConcert);
        ConcertWindowPopUp titlesMoviePopUp= new ConcertWindowPopUp(getActivity());
        titlesMoviePopUp.showPopPup(movie, imageView, llytParentConcert);

    }
}
