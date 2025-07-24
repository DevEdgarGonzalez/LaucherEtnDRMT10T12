package com.actia.movies_ninos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actia.drm.ExoDRMActivity;
import com.actia.drm.PlayerDRMActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.peliculas.PlayMovieActivity;
import com.actia.utilities.utilities_ui.ImageWorker;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * Created by Edgar Gonzalez on 27/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class MovieNinosWindowPopUp {
    private final Context context;
    private final Movie movieChildSelected;
    private final Activity activity;

    private View viewPopUp;
    private PopupWindow popupWindow;

    ImageView img_pop_view;
    JustifyTextView sinopsis;
    LinearLayout bt;
    ImageView img_close;



    public MovieNinosWindowPopUp(Movie movieChildSelected, Activity activity) {
        this.movieChildSelected = movieChildSelected;
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    void showPopUp(ImageView currentImageView) {

        activity.findViewById(R.id.llytParentMoviesChild).setAlpha(0.1f);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopUp = inflater.inflate(R.layout.popup_movie_view, null, false);

        startElementsUI();
        addlisteners();
        drawElementsUI(currentImageView);
    }

    private void startElementsUI() {

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if(dpHeight > 800 && dpWidth > 1400) {
            popupWindow = new PopupWindow(viewPopUp, (int) dpWidth, (int) dpHeight, true);
        }
        else if (dpHeight < 800 && dpWidth < 1400){
            popupWindow = new PopupWindow(viewPopUp, 980, 540, true);
        }
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(activity.findViewById(R.id.llytParentMoviesChild), Gravity.CENTER_VERTICAL, 0, -40);

        img_pop_view = viewPopUp.findViewById(R.id.image_expande_poppup);
        sinopsis = viewPopUp.findViewById(R.id.popup_text_movie);
        bt = viewPopUp.findViewById(R.id.btn_play_movie);
        bt.requestFocus();
        img_close = viewPopUp.findViewById(R.id.imageViewClose);

    }

    private void drawElementsUI(ImageView currentImage) {
     /*   if (movieChildSelected.image != null) {
            ImageWorker imageWorker = new ImageWorker();
            imageWorker.loadBitmap(movieChildSelected.image, img_pop_view, context, 500, 500);

        } else {
            img_pop_view.setImageResource(R.drawable.no_movie);
        }

        sinopsis.setText(movieChildSelected.textFile+"\n\n");*/

        if (movieChildSelected.image != null) {
            Bitmap bmSrc1 = ((BitmapDrawable) currentImage.getDrawable()).getBitmap();
            Bitmap bmSrc2 = bmSrc1.copy(bmSrc1.getConfig(), true);
            @SuppressWarnings("deprecation")
            Drawable dr = new BitmapDrawable(bmSrc2);
            img_pop_view.setImageDrawable(dr);
        } else img_pop_view.setImageResource(R.drawable.bkg_movie);


        sinopsis.setText(movieChildSelected.textFile+"\n\n");

    }

    private void addlisteners() {
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
//                girdvContentWindow.setAlpha(1.0f);
                activity.findViewById(R.id.llytParentMoviesChild).setAlpha(1.0f);
            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (movieChildSelected.isDRM) {
                    Intent intent = new Intent(activity, ExoDRMActivity.class);
                    intent.putExtra(PlayerDRMActivity.keyPathDRM, movieChildSelected.path);
                    activity.startActivityForResult(intent, 1);

                } else {
                    Intent intent = new Intent(activity, PlayMovieActivity.class);
                    intent.putExtra("path", movieChildSelected.path);
                    activity.startActivityForResult(intent, 1);
                }
                popupWindow.dismiss();
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
    }

}
