package com.actia.multimedia;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.viewpager.widget.ViewPager;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 20/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class MultimediaWindowPopUp {

    private ArrayList<File> alistImages;
    private ViewPager viewPager;
    private Context context;
    private MultimediaPagerAdapter pagerAdapter;
    private ImageButton btnBackBasePopUpMultimedia;
    private ImageButton btnNextBasePopUpMultimedia;
    private ImageButton ibtnCloseDialogWeSlide;


    public MultimediaWindowPopUp(Context context, ArrayList<File> alistImages) {
        this.alistImages = alistImages;
        this.context = context;
    }

    public void showWindowPopup() {
        int positionInitial = 0;
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builderDialog.setCancelable(true);
        View popupView = inflater.inflate(R.layout.popup_multimedia_slider, null);
        builderDialog.setView(popupView);
        final AlertDialog alertDialog = builderDialog.create();

        btnBackBasePopUpMultimedia = (ImageButton) popupView.findViewById(R.id.btnBackBasePopUpMultimedia);
        btnNextBasePopUpMultimedia = (ImageButton) popupView.findViewById(R.id.btnNextBasePopUpMultimedia);
        ibtnCloseDialogWeSlide = (ImageButton) popupView.findViewById(R.id.ibtnCloseDialogWeSlide);


        if (alistImages.size() == 1) {
            btnBackBasePopUpMultimedia.setVisibility(View.INVISIBLE);
            btnNextBasePopUpMultimedia.setVisibility(View.INVISIBLE);
        } else if (positionInitial == 0) {
            btnBackBasePopUpMultimedia.setVisibility(View.INVISIBLE);
        } else if (positionInitial == alistImages.size() - 1) {
            btnNextBasePopUpMultimedia.setVisibility(View.INVISIBLE);
        }

        btnBackBasePopUpMultimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item = getItem(-1);
                if (item >= 0) {
                    viewPager.setCurrentItem(item, true);
                }
                hideAndShowNavigation(item);
            }
        });


        btnNextBasePopUpMultimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int item = getItem(+1);
                if (item < pagerAdapter.getCount()) {
                    viewPager.setCurrentItem(item, true);
                }
                hideAndShowNavigation(item);
            }
        });
        ibtnCloseDialogWeSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
            }
        });

        pagerAdapter = new MultimediaPagerAdapter(context, alistImages);

        viewPager = (ViewPager) popupView.findViewById(R.id.vpagerCoverPopUpMultimedia);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(positionInitial);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        File abc = alistImages.get(positionInitial);
        if (abc != null && abc.exists()) {
            alertDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

            //noinspection ConstantConditions
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = 980;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            alertDialog.getWindow().setAttributes(lp);
        }
        viewPager.setFocusable(false);
    }


    private void hideAndShowNavigation(int item) {
        int countadapter = pagerAdapter.getCount();
        if (item == 0) {
            btnBackBasePopUpMultimedia.setVisibility(View.INVISIBLE);
            btnNextBasePopUpMultimedia.setVisibility(View.VISIBLE);
        } else if (item == countadapter - 1) {
            btnBackBasePopUpMultimedia.setVisibility(View.VISIBLE);
            btnNextBasePopUpMultimedia.setVisibility(View.INVISIBLE);
        } else {
            btnBackBasePopUpMultimedia.setVisibility(View.VISIBLE);
            btnNextBasePopUpMultimedia.setVisibility(View.VISIBLE);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}


