package com.actia.menu_maintance;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.nio.file.FileAlreadyExistsException;

public class DialogInstallAPKActivity extends Activity implements AsynckInstallAPPFromDirAPP.OnInstallListener {

    private final Handler validationHandler = new Handler();
    private TextView txtinstall;
    private Button btn;
    private ProgressBar progresBar;
    private View decorView;
//    private TextView txtuninstall;
//    private String install=null;
//    private String uninstall=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_install_apk);
        txtinstall= findViewById(R.id.install);
//        txtuninstall=(TextView)findViewById(R.id.uninstall);
        progresBar= findViewById(R.id.pb);
        btn= findViewById(R.id.btn_ok_dialoginstall);


        progresBar.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), getString(R.string.start), Toast.LENGTH_SHORT).show();
        new AsynckInstallAPPFromDirAPP(DialogInstallAPKActivity.this,this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(DialogInstallAPKActivity.this);
                }
            }
        });

    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            DialogInstallAPKActivity.this.finish();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        validationHandler.removeCallbacks(mUpdateTimeTask);
        android.os.Debug.stopMethodTracing();
    }

    @Override
    public void onInstallFinished(String resultInstall, String resultUninstall) {
        Toast.makeText(getApplicationContext(), getString(R.string.finish), Toast.LENGTH_SHORT).show();
        txtinstall.setText(resultInstall);
        progresBar.setVisibility(View.INVISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validationHandler.removeCallbacks(mUpdateTimeTask);
                DialogInstallAPKActivity.this.finish();
            }
        });
        validationHandler.postDelayed(mUpdateTimeTask,18000);
    }

    protected void onResume() {
        super.onResume();
        hideNavigationBar(DialogInstallAPKActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(DialogInstallAPKActivity.this);
    }
}
