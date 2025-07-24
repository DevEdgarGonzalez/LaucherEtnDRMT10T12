package com.actia.mexico.generic_2018_t10_t12;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import java.io.File;

import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.menu_maintance.MenuMaintanceDialog;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_file.MediaFileFunctions;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This activity is showing when exist any error in the Launcher
 */
public class ErrorActivity extends BaseActivity {
	public static final String ARG_STATE = "state";
	private View decorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);


		ImageButton btnMenuHome = findViewById(R.id.btnMenuHome);
		btnMenuHome.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new MenuMaintanceDialog(ErrorActivity.this, getSupportFragmentManager()).showMenu(false);
				return false;
			}
		});
		Bundle extras = getIntent().getExtras();
		 String message;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					ConfigMasterMGC ConfigMaster = ConfigMasterMGC.getConfigSingleton();
					File f=new File(ConfigMaster.getXML_TACTIL_INFO());
					
					if(!f.exists()){
						File renF=new File(ConfigMaster.getXML_TACTIL_INFO_EXTSD());
						if(renF.exists() && renF.length()>0){
							MediaFileFunctions fun = new MediaFileFunctions();
							fun.copyFile(renF, f);
							fun.deleteViaContentProvider(getApplicationContext(), renF.getPath());
						}
					 }
				}
			}).start();
			
		if (extras != null) {
		    message =extras.getString(ARG_STATE);
			TextView error= findViewById(R.id.error);
			error.setText(message);
		}

		hideNavigationBar(this);

		decorView = getWindow().getDecorView();

		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if (visibility == 0){
					hideNavigationBar(ErrorActivity.this);
				}
			}
		});

	}

	protected void onResume() {
		super.onResume();
		hideNavigationBar(ErrorActivity.this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		hideNavigationBar(ErrorActivity.this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	    super.onCreateOptionsMenu(menu);
//	       new MenuMaintanceDialog(ErrorActivity.this, getSupportFragmentManager()).showMenu(false);
	    return false;
	}
	
	@Override
	public void onBackPressed() {
		
	}

}
