package com.actia.drm;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncRegisterLicenses extends AsyncTask<Void, String, Void> {

	private Context context=null;
	private OnTokenRegisteredListener interf=null;
	private LinearLayout LinearView=null;
	private final ProgressDialog dialog=null;
	private File[] tokens=null;
	private final String TAG="AsyncRegisterLicenses";
	private boolean error=false;
	ScrollView scroll;
	private int count=0;

	public AsyncRegisterLicenses(Context context, File[] tokens, LinearLayout LinearView, ScrollView scroll, OnTokenRegisteredListener interf){
		this.context=context;
		this.LinearView=LinearView;
		this.interf=interf;
		this.tokens=tokens;
		this.scroll=scroll;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		sleep(3000);
		
		try {
			//Runtime.setProperty(Property.ROOTED_OK,true);
			Runtime.initialize(this.context.getDir("wasabi", Context.MODE_PRIVATE)
			        .getAbsolutePath());

			if (!Runtime.isPersonalized())
				Runtime.personalize();
		
		} catch (NullPointerException e) {
			error=true;
			publishProgress(e.toString(),"0");
			return null;
		} catch (ErrorCodeException e) {
			Log.e(TAG, "runtime initialization or personalization error: "
			        + e.getLocalizedMessage());
			error=true;
			publishProgress(context.getString(R.string.error_personalize_wasabi)
			        + e.getLocalizedMessage(),"0");
			return null;
		}
		
		for(File f:this.tokens){
			
			if(!isCancelled()){
				
				String licenseAcquisitionToken =getTokenFromSDCard(f.getPath());
				
				if (licenseAcquisitionToken == null) {
					error=true;
					Log.e(TAG,"Could not find action token in the assets directory - exiting");
					publishProgress(context.getString(R.string.error_token_not_found),"0");
					return null;
				}
				
				long start = System.currentTimeMillis();
				try {
					Runtime.processServiceToken(licenseAcquisitionToken);
					count++;
					Log.i(TAG,f.getName()+" - license successfully acquired in (ms): "+ (System.currentTimeMillis() - start));
					publishProgress("***"+f.getName()+context.getString(R.string.license_successfully)+ (System.currentTimeMillis() - start),"1");
				} catch (ErrorCodeException e1) {
					error=true;
					publishProgress("***"+f.getName()+context.getString(R.string.license_acquisition_token)+e1.toString(),"0");
					Log.e(TAG,"Could not acquire the license from the license acquisition token - exiting: "+e1.toString());
					return null;
				}  
				sleep(1000);
				
			}else{
				Log.i(TAG, "Stop for and validate licens");
				SettingsActivity.exitFlag=true;
				break;
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		interf.onTokenRegistered(error,count);
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	       TextView valueTV = new TextView(context);
	       valueTV.setText(values[0]);
	       valueTV.setTypeface(Typeface.DEFAULT_BOLD);
	       valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
	       LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
	               LayoutParams.MATCH_PARENT,
	               LayoutParams.WRAP_CONTENT);
	       ll.setMargins(0, 0, 0, 0);
	       valueTV.setLayoutParams(ll);
	       valueTV.setPadding(0, 0, 0, 0);
	      
	       if(values[1]!=null && !values[1].isEmpty() && values[1].equals("1"))
		       valueTV.setTextColor(Color.CYAN);
	       else valueTV.setTextColor(Color.RED);
	       
	       this.LinearView.addView(valueTV);
	       this.scroll.fullScroll(View.FOCUS_DOWN);
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		publishProgress(context.getString(R.string.canceling));
	}
	
	@Override
	protected void onCancelled(Void result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		publishProgress(context.getString(R.string.canceling));
	}

	protected String getTokenFromSDCard(String path){
		BufferedReader myReader=null;
		try {
			File myFile = new File(path);
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
	
	private void sleep(long miliseconds){
		long timeSleep= System.currentTimeMillis()+miliseconds;
		long now= System.currentTimeMillis();
		while(now<timeSleep){
			now= System.currentTimeMillis();
		}
	}
}
