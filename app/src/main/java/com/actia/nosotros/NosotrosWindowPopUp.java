package com.actia.nosotros;

import android.app.AlertDialog;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 20/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class NosotrosWindowPopUp {

    private final ArrayList<File> alistImages;
    private ViewPager viewPager;
    private final Context context;
    private NosotrosPagerAdapter pagerAdapter;
    private ImageButton btnBackBasePopUpNosotros;
    private ImageButton btnNextBasePopUpNosotros;
    private ImageButton ibtnCloseDialogWeSlide;


    public NosotrosWindowPopUp(Context context, ArrayList<File> alistImages) {
        this.alistImages = alistImages;
        this.context = context;
    }

    public void showWindowPopup() {
        int positionInitial = 0;
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builderDialog.setCancelable(true);
        View popupView = inflater.inflate(R.layout.popup_nosotros_slider, null);
        builderDialog.setView(popupView);
        final AlertDialog alertDialog = builderDialog.create();

        btnBackBasePopUpNosotros = popupView.findViewById(R.id.btnBackBasePopUpNosotros);
        btnNextBasePopUpNosotros = popupView.findViewById(R.id.btnNextBasePopUpNosotros);
        ibtnCloseDialogWeSlide = popupView.findViewById(R.id.ibtnCloseDialogWeSlide);


        if (alistImages.size() == 1) {
            btnBackBasePopUpNosotros.setVisibility(View.INVISIBLE);
            btnNextBasePopUpNosotros.setVisibility(View.INVISIBLE);
        } else if (positionInitial == 0) {
            btnBackBasePopUpNosotros.setVisibility(View.INVISIBLE);
        } else if (positionInitial == alistImages.size() - 1) {
            btnNextBasePopUpNosotros.setVisibility(View.INVISIBLE);
        }

        btnBackBasePopUpNosotros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item = getItem(-1);
                if (item >= 0) {
                    viewPager.setCurrentItem(item, true);
                }
                hideAndShowNavigation(item);
            }
        });


        btnNextBasePopUpNosotros.setOnClickListener(new View.OnClickListener() {
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

        pagerAdapter = new NosotrosPagerAdapter(context, alistImages);

        viewPager = popupView.findViewById(R.id.vpagerCoverPopUpNosotros);
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
            btnBackBasePopUpNosotros.setVisibility(View.INVISIBLE);
            btnNextBasePopUpNosotros.setVisibility(View.VISIBLE);
        } else if (item == countadapter - 1) {
            btnBackBasePopUpNosotros.setVisibility(View.VISIBLE);
            btnNextBasePopUpNosotros.setVisibility(View.INVISIBLE);
        } else {
            btnBackBasePopUpNosotros.setVisibility(View.VISIBLE);
            btnNextBasePopUpNosotros.setVisibility(View.VISIBLE);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}


