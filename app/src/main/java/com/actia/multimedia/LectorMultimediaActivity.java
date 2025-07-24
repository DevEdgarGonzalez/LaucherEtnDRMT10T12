package com.actia.multimedia;

import static com.actia.utilities.ContentType.UtilsContentType.TYPE_DIR_IMAGES;
import static com.actia.utilities.ContentType.UtilsContentType.TYPE_IMAGES;
import static com.actia.utilities.ContentType.UtilsContentType.TYPE_TEXT;
import static com.actia.utilities.ContentType.UtilsContentType.TYPE_VIDEO;
import static com.actia.utilities.ContentType.UtilsContentType.TYPE_WEB;
import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.ContentType.UtilsContentType;

public class LectorMultimediaActivity extends BaseActivity implements MultimediaShowTextFragment.OnShowTextFgmntListener {

    private View decorView;
    private final String TAG = this.getClass().getSimpleName();
    private String ruta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lector_multimedia_activity);

        Bundle extras = getIntent().getExtras();
        if(!TextUtils.isEmpty(extras.getString(ConstantsApp.ARG_SUBCAT)))
            this.ruta = extras.getString(ConstantsApp.ARG_SUBCAT);

        showContent();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(LectorMultimediaActivity.this);
                }
            }
        });


    }



    private void showContent(){
        context = this;
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        UtilsContentType utilsHelp = new UtilsContentType(this, configMasterMGC.getPLUS_MEDIA_PATH() + ruta, configMasterMGC);
        int typeContent = utilsHelp.getTypeContent();

        switch (typeContent) {
            case TYPE_IMAGES:
                getSupportFragmentManager().beginTransaction().replace(R.id.llytContainerParentMultimedia, utilsHelp.getFragmentTypeImage()).commit();
                findViewById(R.id.llytContainerParentMultimedia).setVisibility(View.VISIBLE);
                break;
            case TYPE_DIR_IMAGES:
                getSupportFragmentManager().beginTransaction().add(R.id.flytContainerMultimediaMain,utilsHelp.getFragmentTypeImgDir()).commit();
                findViewById(R.id.llytContentDirectoriesWe).setVisibility(View.VISIBLE);
                break;

            case TYPE_VIDEO:
                changeActivity(utilsHelp.getIntentVideo(), false);
                break;
            case TYPE_TEXT:

                getSupportFragmentManager().beginTransaction().replace(R.id.llytContainerParentMultimedia, utilsHelp.getFragmentTypeText()).commit();
                findViewById(R.id.llytContainerParentMultimedia).setVisibility(View.VISIBLE);
                break;

            case TYPE_WEB:
                WebViewFragment webViewFragment = utilsHelp.getWebViewFragment();
                if (webViewFragment == null) {
                    Toast.makeText(context, getString(R.string.we_invalid_content), Toast.LENGTH_SHORT).show();
                    home();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.llytContainerParentMultimedia, webViewFragment).commit();
                findViewById(R.id.llytContainerParentMultimedia).setVisibility(View.VISIBLE);
                break;

            default:
                Toast.makeText(context, getString(R.string.we_invalid_content), Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }
    }



    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onCloseShowTextFgmntInteraction() {
        home();
    }

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(LectorMultimediaActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(LectorMultimediaActivity.this);
    }
}