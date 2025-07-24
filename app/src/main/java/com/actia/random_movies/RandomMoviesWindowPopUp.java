package com.actia.random_movies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.utilities.UtilitiesComunicationServiceActia;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 23/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class RandomMoviesWindowPopUp {


    private RandomMoviewsAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;
    private final Context context;

    public RandomMoviesWindowPopUp(Context context) {
        this.context = context;
    }

    public void showPopPup(final ArrayList<Movie> item, int position) {

        AlertDialog.Builder builder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View popUpView = inflater.inflate(R.layout.popup_random_movie_base, null);
        builder.setView(popUpView);
        AlertDialog alertDialog = builder.create();

        ImageButton btnNextBasePopRandom = popUpView.findViewById(R.id.btnNextBasePopRandom);
        ImageButton btnBackBasePopRandom = popUpView.findViewById(R.id.btnBackBasePopRandom);

        btnNextBasePopRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item = getItem(+1);

                if (item < mCustomPagerAdapter.getCount())
                    mViewPager.setCurrentItem(item, true);
                UtilitiesComunicationServiceActia.sendLogBroadcast(context);
            }
        });

        btnBackBasePopRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item = getItem(-1);
                if (item >= 0)
                    mViewPager.setCurrentItem(item, true);
                UtilitiesComunicationServiceActia.sendLogBroadcast(context);
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                UtilitiesComunicationServiceActia.sendLogBroadcast(context);
            }
        });

        mCustomPagerAdapter = new RandomMoviewsAdapter(context, item);

        mViewPager = popUpView.findViewById(R.id.vpagerCoverPopUpRandom);
        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.setCurrentItem(position);

        if (item != null) {

            alertDialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

            //noinspection ConstantConditions
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = 980;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            alertDialog.getWindow().setAttributes(lp);
        }
    }


    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

}
