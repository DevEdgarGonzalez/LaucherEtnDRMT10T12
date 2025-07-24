package com.actia.drm;


import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_file.TokenFilter;
import com.intertrust.wasabi.media.PlaylistProxyListener;

import java.io.File;

@SuppressWarnings("unused")
enum ContentTypes {
	DASH("application/dash+xml"), HLS("application/vnd.apple.mpegurl"), PDCF(
	        "video/mp4"), M4F("video/mp4"), DCF("application/vnd.oma.drm.dcf"), BBTS(
	        "video/mp2t");
	String mediaSourceParamsContentType = null;

	ContentTypes(String mediaSourceParamsContentType) {
		this.mediaSourceParamsContentType = mediaSourceParamsContentType;
	}

	public String getMediaSourceParamsContentType() {
		return mediaSourceParamsContentType;
	}
}

public class SettingsActivity extends BaseActivity {

//	private ActionMode mActionMode;
	public static boolean exitFlag=true;
	private View decorView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			        .add(R.id.containerLicense, new MBB_Playback_Fragment2()).commit();
		}

		hideNavigationBar(this);

		decorView = getWindow().getDecorView();

		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if (visibility == 0){
					hideNavigationBar(SettingsActivity.this);
				}
			}
		});
	}
	
	@SuppressLint("ValidFragment")
	public class MBB_Playback_Fragment2 extends Fragment implements PlaylistProxyListener,OnUserRegisterListener,OnTokenRegisteredListener {

		private DRM drm=null;
		private File[] tokens=null;
		private LinearLayout txtToken;
		private File dirToken=null;
		private ConfigMasterMGC Config;
		private ListView listLicenses;
		private AdapterTokens adapter;
		private Button btnUser;
		private CheckBox chkBox;
		private Button btnStop;
		private Button btnUser2;
		private final int userDRM=1;
		private final int userSwank=2;
		private ScrollView scrollView;
		private TextView txtTotalTokens;
		private int totalTokens=0;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			
	        Config = ConfigMasterMGC.getConfigSingleton();
			View rootView = inflater.inflate(R.layout.fragmentlicense, container,false);
			
			btnUser= rootView.findViewById(R.id.btn_user);
			btnUser2= rootView.findViewById(R.id.btn_userSw);
			Button btnLicenses= rootView.findViewById(R.id.btn_lic);
			btnStop= rootView.findViewById(R.id.stop_valid_lic);
			txtToken= rootView.findViewById(R.id.tokenView);
			chkBox = rootView.findViewById(R.id.check_all_licences);
			listLicenses= rootView.findViewById(R.id.list_licenses);
			scrollView= rootView.findViewById(R.id.scrollToken);
			txtTotalTokens = rootView.findViewById(R.id.num_tokens_val);
			
			dirToken=new File(Config.getPathTokens());
			
			if(!dirToken.exists())
				return null;
			
			tokens = dirToken.listFiles(new TokenFilter());
			if(tokens.length==0){
				Toast.makeText(getActivity(), getString(R.string.no_tokens_in)+dirToken.getPath(), Toast.LENGTH_SHORT).show();
				return null;
			}
			totalTokens=tokens.length;
			
			adapter=new AdapterTokens(getActivity(),R.layout.cell_list_token, tokens);
			listLicenses.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listLicenses.setItemsCanFocus(false);
			listLicenses.setAdapter(adapter);
			
			listLicenses.setOnItemClickListener(new OnItemClickListener() {

			    @Override
			    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
			    	adapter.toggleSelection(position);
			    }
			});
			
			drm = new DRM(getActivity());		
			drm.setOnLicensesRegisterListener(MBB_Playback_Fragment2.this);
			
			btnUser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SettingsActivity.exitFlag=false;
					drm.setOnUserRegisterListener(Config.getPathUserDRM(),userDRM,MBB_Playback_Fragment2.this);
				}
			});
			
			btnUser2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SettingsActivity.exitFlag=false;
					drm.setOnUserRegisterListener(Config.getPathSwankDRM(),userSwank,MBB_Playback_Fragment2.this);
				}
			});
			
			btnLicenses.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					txtToken.removeAllViews();
					txtTotalTokens.setText(R.string.valid_tokens);
					if(chkBox.isChecked()){
						listLicenses.clearChoices();
						listLicenses.requestLayout();
						adapter.removeSelection();
						if(tokens.length>0){
							totalTokens=tokens.length;
							btnStop.setVisibility(View.VISIBLE);
							SettingsActivity.exitFlag=false;
							drm.setLicensesRegister(getActivity(), tokens, MBB_Playback_Fragment2.this.txtToken,MBB_Playback_Fragment2.this.scrollView);
						}else Toast.makeText(getActivity(), getString(R.string.There_are_no_token_in)+dirToken.getPath(), Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(adapter!=null){
						SparseBooleanArray selected;
						selected = adapter.getSelectedIds();
						if(selected.size()>0){
							File[] fTokens;
							fTokens = new File[selected.size()];
							for(int i=0;i<selected.size();i++){
								if (selected.valueAt(i)) {
									File selecteditem = adapter.getItem(selected.keyAt(i));
									fTokens[i]=selecteditem;
								}
							}
							SettingsActivity.exitFlag=false;
							btnStop.setVisibility(View.VISIBLE);
							totalTokens=fTokens.length;
							drm.setLicensesRegister(getActivity(), fTokens, MBB_Playback_Fragment2.this.txtToken,MBB_Playback_Fragment2.this.scrollView);	
						}else Toast.makeText(getActivity(),getString(R.string.token_no_selected), Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			btnStop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					drm.stopRegisterLicenses();
					btnStop.setVisibility(View.INVISIBLE);
					
					TextView valueTV = new TextView(SettingsActivity.this);
				    valueTV.setText(R.string.canceled_token);
				    valueTV.setTypeface(Typeface.DEFAULT_BOLD);
				    valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
				    valueTV.setLayoutParams(new LayoutParams(
				               LayoutParams.MATCH_PARENT,
				               LayoutParams.WRAP_CONTENT));
				    valueTV.setTextColor(Color.RED);
				      
				    txtToken.addView(valueTV);
				}
			});
	
			return rootView;
		}

		/**************************************
		 * Helper methods to avoid cluttering *
		 **************************************/

		@Override
		public void onErrorNotification(int errorCode, String errorString) {
			String TAG = "DRMVALIDATE";
			Log.e(TAG, "PlaylistProxy Event: Error Notification, error code = " +
					errorCode + ", error string = " +
				errorString); 
		}

		@Override
		public void userRegistered(int idUser,boolean error, String errorString) {
			if(!error){
				if(userDRM==idUser)
					btnUser.setBackgroundColor(Color.GREEN);
				else btnUser2.setBackgroundColor(Color.GREEN);
			}else{
				Toast.makeText(getActivity(), getString(R.string.error_login_user)+errorString, Toast.LENGTH_LONG).show();
				if(userDRM==idUser)
					btnUser.setBackgroundColor(Color.RED);
				else btnUser2.setBackgroundColor(Color.RED);
			}
			SettingsActivity.exitFlag=true;
		}

		@Override
		public void onTokenRegistered(boolean error,int tokensOk) {
			Toast.makeText(getActivity(), getString(R.string.tokens_ok), Toast.LENGTH_SHORT).show();
			btnStop.setVisibility(View.INVISIBLE);
			SettingsActivity.exitFlag=true;
			TextView valueTV = new TextView(SettingsActivity.this);
		    valueTV.setText(R.string.finished);
		    valueTV.setTypeface(Typeface.DEFAULT_BOLD);
		    valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
		    valueTV.setLayoutParams(new LayoutParams(
		               LayoutParams.MATCH_PARENT,
		               LayoutParams.WRAP_CONTENT));
		    valueTV.setTextColor(Color.GREEN);
		    txtToken.addView(valueTV);
		    scrollView.fullScroll(View.FOCUS_DOWN);
		    txtTotalTokens.setText(getString(R.string.tokens_validated)+tokensOk+getString(R.string.preposition_of)+totalTokens);
		} 
		
	}
	
	@Override
	public void onBackPressed() {
		if(exitFlag)
			this.finish();
		else Toast.makeText(getApplicationContext(), getString(R.string.working), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideNavigationBar(SettingsActivity.this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		hideNavigationBar(SettingsActivity.this);
	}
}
