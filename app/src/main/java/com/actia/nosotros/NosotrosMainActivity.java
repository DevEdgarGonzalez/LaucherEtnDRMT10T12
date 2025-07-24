package com.actia.nosotros;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actia.help_aboutus.WebViewFragment;
import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.help_aboutus.UtilsHelp_AboutUs;
import com.actia.utilities.utils_language.UtilsLanguage;

import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_DIR_IMAGES;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_IMAGES;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_TEXT;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_VIDEO;
import static com.actia.help_aboutus.UtilsHelp_AboutUs.TYPE_WEB;
import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

public class NosotrosMainActivity extends BaseActivity implements BaseActivity.CategoryNavigationListener, BaseActivity.PressBackHeader,  NosotrosShowTextFragment.OnShowTextFgmntListener {
    private final String TAG = this.getClass().getSimpleName();
    private int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nosotros_main);

        category = getCategoryFromExtras(getIntent().getExtras());
        if (category == null) {
            onBackPressed();
            return;
        }
        positionCategory = getPositionCategory(category, this.getClass().getName());
        showContent();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(NosotrosMainActivity.this);
                }
            }
        });


    }



    private void showContent(){
        context = this;
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        UtilsHelp_AboutUs utilsHelp_aboutUs = new UtilsHelp_AboutUs(this, configMasterMGC.getNOSOTROS_PATH(), configMasterMGC);
        int typeContent = utilsHelp_aboutUs.getTypeContent();

        switch (typeContent) {
            case TYPE_IMAGES:
                getSupportFragmentManager().beginTransaction().replace(R.id.llytContainerParentNosotros, utilsHelp_aboutUs.getFragmentTypeImage()).commit();
                findViewById(R.id.llytContainerParentNosotros).setVisibility(View.VISIBLE);
                break;
            case TYPE_DIR_IMAGES:
                startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(category), mutableDrawableFromPath(category.getPathImg()),this);
                getSupportFragmentManager().beginTransaction().add(R.id.flytContainerNosotrosMain,utilsHelp_aboutUs.getFragmentTypeImgDir()).commit();
                findViewById(R.id.llytContentDirectoriesWe).setVisibility(View.VISIBLE);
                break;

            case TYPE_VIDEO:
                changeActivity(utilsHelp_aboutUs.getIntentVideo(), false);
                break;
            case TYPE_TEXT:

                getSupportFragmentManager().beginTransaction().replace(R.id.llytContainerParentNosotros, utilsHelp_aboutUs.getFragmentTypeText()).commit();
                findViewById(R.id.llytContainerParentNosotros).setVisibility(View.VISIBLE);
                break;

            case TYPE_WEB:
                WebViewFragment webViewFragment = utilsHelp_aboutUs.getWebViewFragment();
                if (webViewFragment == null) {
                    Toast.makeText(context, getString(R.string.we_invalid_content), Toast.LENGTH_SHORT).show();
                    home();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.llytContainerParentNosotros, webViewFragment).commit();
                findViewById(R.id.llytContainerParentNosotros).setVisibility(View.VISIBLE);
                break;

            default:
                Toast.makeText(context, getString(R.string.we_invalid_content), Toast.LENGTH_SHORT).show();
                home();
                break;
        }
    }

    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBar(this, positionCategory - 1);
        this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBar(this, positionCategory + 1);
        this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        home();
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
        hideNavigationBar(NosotrosMainActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(NosotrosMainActivity.this);
    }

}
