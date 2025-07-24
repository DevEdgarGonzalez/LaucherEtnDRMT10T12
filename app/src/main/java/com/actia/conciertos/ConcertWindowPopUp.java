package com.actia.conciertos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actia.drm.ExoDRMActivity;
import com.actia.drm.PlayerDRMActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.peliculas.PlayMovieActivity;
import com.actia.utilities.UtilitiesComunicationServiceActia;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * Created by Edgar Gonzalez on 10/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class ConcertWindowPopUp {
    Context context;
    Activity activity;
    PopupWindow popupWindow;
    View view;

    ImageView img_pop_view;
    JustifyTextView sinopsis;
    LinearLayout bt;
    ImageView img_close;

    public ConcertWindowPopUp(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    /**
     *Show a popup with the image and synopsis of the movie.
     * @param movie An movie object
     * @param currentImage A movie's imageview.
     * @param linLayParent
     */
    @SuppressLint("NewApi")
    public void showPopPup(final Movie movie, ImageView currentImage, final LinearLayout linLayParent) {
        linLayParent.setAlpha(0.3f);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_movie_view,null,false);
        popupWindow = new PopupWindow(view, 980, 540,true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAtLocation(activity.findViewById(R.id.flytContainerConcerts), Gravity.CENTER_VERTICAL, 0, -40);

        img_pop_view= view.findViewById(R.id.image_expande_poppup);
        sinopsis= view.findViewById(R.id.popup_text_movie);
        bt = view.findViewById(R.id.btn_play_movie);
        bt.requestFocus();
        img_close = view.findViewById(R.id.imageViewClose);

        if(movie.image!=null){
            Bitmap bmSrc1 = ((BitmapDrawable)currentImage.getDrawable()).getBitmap();
            Bitmap bmSrc2 = bmSrc1.copy(bmSrc1.getConfig(), true);
            @SuppressWarnings("deprecation")
            Drawable dr=new BitmapDrawable(bmSrc2);
            img_pop_view.setImageDrawable(dr);
        }else img_pop_view.setImageResource(R.drawable.bkg_movie);


        sinopsis.setText(movie.textFile+"\n\n");
//        sinopsis.setMovementMethod(new ScrollingMovementMethod());

        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//
//                if(isMyMediaPlayerServiceRunning()){//Verifica si el servicio de música esta corriendo
//                    Intent intent = new Intent(activity, MyMediaPlayerService.class);
//                    intent.addCategory(MyMediaPlayerService.TAG);
//                    activity.stopService(intent);
//                }

                popupWindow.dismiss();

                UtilitiesComunicationServiceActia.updateMultimediaBroadcast(context);

//				Intent intent = new Intent(getActivity(), PlayMovieActivity.class);
//				intent.putExtra("path", movie.path);
//				startActivityForResult(intent, 1);

                if(movie.isDRM){
                    Intent intent = new Intent(activity, ExoDRMActivity.class);
                    intent.putExtra(PlayerDRMActivity.keyPathDRM, movie.path);
                    activity.startActivityForResult(intent, 1);

                }else {
                    Intent intent = new Intent(activity, PlayMovieActivity.class);
                    intent.putExtra("path", movie.path);
                    activity.startActivityForResult(intent, 1);
                }

            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
                linLayParent.setAlpha(1.0f);
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
