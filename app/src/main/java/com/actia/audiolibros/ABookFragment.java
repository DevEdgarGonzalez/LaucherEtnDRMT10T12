package com.actia.audiolibros;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_order_arrays.AbookComparator;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_file.FileExtensionFilterMusic;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Display all audio books by genre selected into a ListView
 *
 * @see FileExtensionFilterMusic
 */


public class ABookFragment extends Fragment implements ABookRecyclerAdapter.ABookClickListener {

//    private static int[] positionsNeedCustom = {1, 2};
    public static String ARG_GENRE_ABOOK = "genre";
    private String currentGenre = null;
    private int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    //    private String titleGenre ;
//    private String pathImgGenre ;
    private LinearLayout llytParentAbook;
    private RecyclerView recycGridAllABook;

    ArrayList<AudioBook> listABooks;

    private OnAbookFragmentListener mListener= null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_GENRE_ABOOK)) {
            currentGenre = getArguments().getString(ARG_GENRE_ABOOK);
            positionCategory = getArguments().getInt(ConstantsApp.ARG_POSITION_CATEGORY);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_abook_list, container, false);

        llytParentAbook = getActivity().findViewById(R.id.llytParentAbook);
        recycGridAllABook = root.findViewById(R.id.recycGridAllABook);
//        recycGridAllABook.setHasFixedSize(true);

        ReadFileExternalStorage extStorage = new ReadFileExternalStorage();
        listABooks = extStorage.getAudioBooksByPath(currentGenre);

        if (listABooks != null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES) {
            Collections.sort(listABooks, new AbookComparator());
        }

        if (listABooks != null) {
            //Abre directo el reproductor de abooks
            if (listABooks.size() == 1) {
                OnItemClick(null,0);
                getActivity().finish();
                return null;
            }
        }

        loadRecycler();




//		}


        return root;
    }

    private void loadRecycler() {
        //get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new OrderElementsFourThree(getActivity(), listABooks.size()).getGridManagerVtwo();
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if(dpHeight > 800 && dpWidth > 1400) {
            recycGridAllABook.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(75, 75, 35, 35));
        }
        else if (dpHeight < 800 && dpWidth < 1400){
            recycGridAllABook.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(50, 50, 17, 17));
        }
        recycGridAllABook.setLayoutManager(gridLayoutManager);
        ABookRecyclerAdapter adapterRecyclerAbook = new ABookRecyclerAdapter(listABooks, getActivity());
        adapterRecyclerAbook.setClickListener(this);
        adapterRecyclerAbook.notifyDataSetChanged();

        recycGridAllABook.setAdapter(adapterRecyclerAbook);
    }





    @Override
    public void OnItemClick(View view, int position) {
        AudioBook aBook = listABooks.get(position);
        if (mListener!=null){
            mListener.onAbookSelected(aBook);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAbookFragmentListener){
            mListener= (OnAbookFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener= null;
    }


    public interface OnAbookFragmentListener{
        void onAbookSelected(AudioBook aBook);
    }
}

