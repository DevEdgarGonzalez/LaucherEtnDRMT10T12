package com.actia.mexico.generic_2018_t10_t12;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.menu_maintance.MenuMaintanceDialog;
import com.actia.mexico.launcher_t12_generico_2018.R;

/**
 * Activity that display the menu maintenance
 * 
 * @see MenuMaintanceDialog
 */


public class ActiaMaintenanceActivity extends BaseActivity {

	private View decorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actia_maintenance);

		hideNavigationBar(this);

		decorView = getWindow().getDecorView();

		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if (visibility == 0){
					hideNavigationBar(ActiaMaintenanceActivity.this);
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
	    super.onStart(); 
		new MenuMaintanceDialog(ActiaMaintenanceActivity.this, getSupportFragmentManager()).showMenu(true);
	}
	
	@Override
	public void onBackPressed() {
		 Intent intentone = new Intent(getApplicationContext(), MainActivity.class);
		  intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		  startActivity(intentone);
	}

	protected void onResume() {
		super.onResume();
		hideNavigationBar(ActiaMaintenanceActivity.this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		hideNavigationBar(ActiaMaintenanceActivity.this);
	}
}
