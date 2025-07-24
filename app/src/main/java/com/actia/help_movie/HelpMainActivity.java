package com.actia.help_movie;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actia.help_aboutus.WebViewFragment;
import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.nosotros.NosotrosShowTextFragment;
import com.actia.help_aboutus.UtilsHelp_AboutUs;

import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_DIR_IMAGES;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_IMAGES;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_TEXT;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_VIDEO;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_WEB;
import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import androidx.annotation.RequiresApi;

public class HelpMainActivity extends BaseActivity implements NosotrosShowTextFragment.OnShowTextFgmntListener {

    private final String TAG = this.getClass().getSimpleName();
    private View decorView;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_main);

        showContent();
        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(HelpMainActivity.this);
                }
            }
        });
    }


    private void showContent() {
        context = this;
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        UtilsHelp_AboutUs utilsHelp_aboutUs = new UtilsHelp_AboutUs(this, configMasterMGC.getPathHelpVideo(), configMasterMGC);
        int typeContent = utilsHelp_aboutUs.getTypeContent();

        switch (typeContent) {
            case TYPE_IMAGES:
                getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerMainHelp, utilsHelp_aboutUs.getFragmentTypeImage()).commit();
                break;
            case TYPE_DIR_IMAGES:
                getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerMainHelp, utilsHelp_aboutUs.getFragmentTypeImgDir()).commit();
                findViewById(R.id.flytContainerMainHelp).setVisibility(View.VISIBLE);
                break;

            case TYPE_VIDEO:
                changeActivity(utilsHelp_aboutUs.getIntentVideo(), false);
                break;
            case TYPE_TEXT:

                getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerMainHelp, utilsHelp_aboutUs.getFragmentTypeText()).commit();

                break;
            case TYPE_WEB:
                WebViewFragment webViewFragment = utilsHelp_aboutUs.getWebViewFragment();

                if (webViewFragment == null) {
                    showErrorAndFinishActivity();
                    return;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.flytContainerMainHelp, webViewFragment).commit();
                break;
            default:
                showErrorAndFinishActivity();
                break;
        }
    }

    private void showErrorAndFinishActivity() {
        Toast.makeText(context, getString(R.string.we_invalid_content), Toast.LENGTH_SHORT).show();
        home();
    }


    @Override
    public void onCloseShowTextFgmntInteraction() {
        home();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(HelpMainActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(HelpMainActivity.this);
    }
}
