package com.actia.games;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.infraestructure.ItemsHome;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;


public class GamesFragment extends Fragment implements GamesRecyclerAdapter.ClickListenerGames {

    public static String ARG_GAMES_LIST = "getGames";

    private RecyclerView recycGridGames;

    private ArrayList<ItemsHome> alistGames = null;

    public GamesFragment() {
    }


    public static GamesFragment newInstance(ArrayList<ItemsHome> games) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_GAMES_LIST, games);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alistGames = getArguments().getParcelableArrayList(ARG_GAMES_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_games, container, false);
        recycGridGames = root.findViewById(R.id.recycGridGames);


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if(dpHeight > 800 && dpWidth > 1400) {
            recycGridGames.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(75, 75, 35, 35));
        }
        else if (dpHeight < 800 && dpWidth < 1400){
            recycGridGames.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(50, 50, 17, 17));
        }
        GamesRecyclerAdapter gamesAdapter = new GamesRecyclerAdapter(alistGames, getContext());
        gamesAdapter.setOnClickListenerGame(this);

        //get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new OrderElementsFourThree(getActivity(), alistGames.size()).getGridManagerVtwo();
        }

        recycGridGames.setLayoutManager(gridLayoutManager);
        recycGridGames.setAdapter(gamesAdapter);


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
    public void onItemClickGames(View view, int position) {
        launchIntent(alistGames.get(position), getActivity());
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
}
