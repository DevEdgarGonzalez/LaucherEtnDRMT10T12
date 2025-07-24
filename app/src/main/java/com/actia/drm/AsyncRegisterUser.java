package com.actia.drm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncRegisterUser extends AsyncTask<Void, Void, Void> {
	
	private Context context=null;
	private OnUserRegisterListener interf=null;
	private boolean error=false;
	private String errorString;
	private ProgressDialog dialog=null;
	//private String userLicense="//mnt/extsdcard/key.xml";
	private ConfigMasterMGC Config;
	private final String TAG="AsyncRegisterUser";
	private String pathUser=null;
	private final int idUser;
	
	public AsyncRegisterUser(Context context, String pathUser, int idUser, OnUserRegisterListener interf){
		this.context=context;
		this.interf=interf;
		this.pathUser=pathUser;
		this.idUser=idUser;
		this.errorString = context.getString(R.string.ok);
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		dialog = new ProgressDialog(this.context);
		dialog.setMessage(context.getString(R.string.working));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
	    dialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
        Config = ConfigMasterMGC.getConfigSingleton();
		File userToken=new File(pathUser);
		if(userToken.exists() && userToken.length()>0){
			try {
				//Runtime.setProperty(Property.ROOTED_OK,true);
				Runtime.initialize(this.context.getDir("wasabi", Context.MODE_PRIVATE)
				        .getAbsolutePath());
	
				if (!Runtime.isPersonalized())
					Runtime.personalize();
			
			} catch (NullPointerException e) {
				error=true;
				errorString=e.toString();
				return null;
			} catch (ErrorCodeException e) {
				Log.e(TAG, "runtime initialization or personalization error: "
				        + e.getLocalizedMessage());
				error=true;
				errorString= context.getString(R.string.error_personalize_wasabi)
				        + e.getLocalizedMessage();
				return null;
			}
			
			String licenseAcquisitionToken =getTokenFromSDCard();
			if (licenseAcquisitionToken == null) {
				error=true;
				errorString=context.getString(R.string.error_token_not_found);
				Log.e(TAG,"Could not find action token in the assets directory - exiting");
				return null;
			}
			
			long start = System.currentTimeMillis();
			try {
				Runtime.processServiceToken(licenseAcquisitionToken);
				Log.i(TAG,"License successfully acquired in (ms): "+ (System.currentTimeMillis() - start));
			} catch (ErrorCodeException e1) {
				error=true;
				errorString=context.getString(R.string.license_acquisition_token)+e1.toString();
				Log.e(TAG,"Could not acquire the license from the license acquisition token - exiting: "+e1.toString());
				return null;
			}
		}else{
			error=true;
			errorString=userToken.getPath()+context.getString(R.string.does_not_exist);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
	// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		interf.userRegistered(this.idUser,error, errorString);
	}
	
	protected String getTokenFromSDCard(){
		BufferedReader myReader=null;
		try {
			File myFile = new File(Config.getPathUserDRM());
			FileInputStream fIn = new FileInputStream(myFile);
			myReader = new BufferedReader(
					new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow + "\n";
			}
			Log.i(TAG, aBuffer);
			return aBuffer;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}finally{
			if(myReader!=null){
				try {
					myReader.close();
					myReader=null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
