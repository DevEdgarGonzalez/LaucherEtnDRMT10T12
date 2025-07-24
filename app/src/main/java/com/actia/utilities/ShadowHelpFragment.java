package com.actia.utilities;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mapas.SingletonConfig;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShadowHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShadowHelpFragment extends DialogFragment {

    private Animation animation;
    private ImageView mSwipUpImageView;
    private ConstraintLayout mShadowConstraintLayout;
    private ShimmerTextView mCommentTextView;
    private ShimmerTextView mTouchScreenTextView;
    private Shimmer shimmer;

    public static ShadowHelpFragment newInstance() {
        ShadowHelpFragment fragment = new ShadowHelpFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shadow_help, container, false);

        mSwipUpImageView = rootView.findViewById(R.id.swipeup_image_view);
        mCommentTextView = rootView.findViewById(R.id.comment_text_view);
        mTouchScreenTextView = rootView.findViewById(R.id.touch_screen_text_view);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        mSwipUpImageView.startAnimation(animation);
        mShadowConstraintLayout = rootView.findViewById(R.id.shadow_relative_layout);
        shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.start(mCommentTextView);
        shimmer.start(mTouchScreenTextView);

        mCommentTextView.setText(Html.fromHtml(getBienvenidaTexto()));

        //updateLanguage();
        mShadowConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfigurationChanged(LocaleHelper.setLocale(getContext(), "es"));
                handlerChangeLanguage.removeCallbacks(runnableChangeLanguage);
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    private void updateLanguage(){
        handlerChangeLanguage.postDelayed(runnableChangeLanguage, 5000);
    }

    private final Handler handlerChangeLanguage = new Handler();

    private final Runnable runnableChangeLanguage = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            if(Locale.getDefault().getLanguage().equalsIgnoreCase("es"))
                onConfigurationChanged(LocaleHelper.setLocale(getContext(), "en"));
            else
                onConfigurationChanged(LocaleHelper.setLocale(getContext(), "es"));

            updateLanguage();
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        mCommentTextView.setText(R.string.navbar_comment);
        mTouchScreenTextView.setText(R.string.touch_screen);
        super.onConfigurationChanged(newConfig);
        Log.i("AdvertisingActivity", "onConfigurationChanged: si cambio " + newConfig.locale);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerChangeLanguage.removeCallbacks(runnableChangeLanguage);
    }

    private String getBienvenidaTexto(){
        ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
        String bienvenidaTxt = "";
        File jsonExist = new File(Config.getBIENVENIDA_TEXT_FILE());
        try {
            if(jsonExist.exists()){
                InputStream ins = new FileInputStream(Config.getBIENVENIDA_TEXT_FILE());
                Scanner obj = new Scanner(ins);
                while (obj.hasNextLine()) {
                    bienvenidaTxt += obj.nextLine();
                }
            } else {
                return bienvenidaTxt;
            }
        } catch (Exception ex){
            Log.e("ShadowHelpFragment", "getBienvenidaTexto: ", ex);
            return "";
        }

        return bienvenidaTxt;
    }
}