package com.actia.mapas;


import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.mapas.Utils.Utils;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_internet.AsynckCheckInternetConn;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Map_Activity extends BaseActivity {
    Map_Fragment frgMap;

    protected ImageView mIvBack;

    protected RelativeLayout mSpinnerRelativeLayout;

    public static Animation animation;
    public static ImageView mIvCentrar;
    protected static TextView mSpeedTextView;
    protected static TextView mCourseTextView;
    protected static final String TAG = "Map_Activity";
    public static boolean isAnimated = false;

    private SharedPreferences settings;

    @SuppressLint("StaticFieldLeak")
    public static HashMap<String, String> map_messages = null;
    private View decorView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.map);

        AsynckCheckInternetConn stp100connection = new AsynckCheckInternetConn(this);

        try {
            if(stp100connection.execute().get()) {

                settings = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE);

                new Picasso.Builder(this).downloader(new MyUrlConnectionDownloader(this)).build();

                /* Statistics INIT APP */
                //sendStatistics("1");

                frgMap = (Map_Fragment) getSupportFragmentManager().findFragmentById(R.id.frgMap);
                mIvBack = (ImageView) findViewById(R.id.back_image_view);
                mIvCentrar = (ImageView) findViewById(R.id.centrar_image_view);
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_centrar_gps_off);
                mSpeedTextView = (TextView) findViewById(R.id.speed_text_view);
                mCourseTextView = (TextView) findViewById(R.id.course_text_view);
                mSpinnerRelativeLayout = (RelativeLayout) findViewById(R.id.spinner_relative_layour);

                mIvBack.setOnClickListener(onBack);
                mIvCentrar.setOnClickListener(onCentrarClick);
            }else {
                Toast.makeText(getApplicationContext(),"Sin conexión", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    hideNavigationBar(Map_Activity.this);
                }
            }
        });

        try{
            if(getActionBar() != null)
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMessage(String key){
        //noinspection SimplifiableIfStatement
        if(map_messages != null){
            return map_messages.containsKey(key);
        }
        else{
            return false;
        }
    }

    public static String getMessage(String key){
        if(map_messages != null){
            return map_messages.get(key);
        }
        else{
            return "";
        }
    }

    private View.OnClickListener onBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "onClick: Saliendo");
            mSpinnerRelativeLayout.setVisibility(View.VISIBLE);
            finish();
        }
    };

    private View.OnClickListener onCentrarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            frgMap.setCenterBusPosition(true);

            mIvCentrar.clearAnimation();
            mIvCentrar.setImageResource(R.drawable.centrar_gps);
            isAnimated = false;
        }
    };
    
    @Override
    protected void onResume() {
    	super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(Map_Activity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        hideNavigationBar(Map_Activity.this);
    }

    @Override
    protected void onPause() {
    	super.onPause();
    }

    @Override
    public void onBackPressed() {
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        /* Statistics FINISH APP */
        //sendStatistics("2");
    }

}
