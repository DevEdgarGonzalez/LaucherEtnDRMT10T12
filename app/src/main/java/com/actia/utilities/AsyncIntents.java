package com.actia.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.actia.mexico.launcher_t12_generico_2018.R;

/**
 * Start activity asynchronously
 */
public class AsyncIntents extends AsyncTask<Intent, Void, Boolean>{

	
	private final Activity activity;
	private ProgressDialog dialog;
	private final boolean destroyActivity;

	public AsyncIntents(Activity activity,boolean destroyActivity){
		this.activity=activity;
		this.destroyActivity=destroyActivity;
	}
	
	 @Override
	 protected void onPreExecute() {
		    dialog = new ProgressDialog(activity);
		    dialog.setMessage(activity.getString(R.string.load_wait_please));
		    dialog.setTitle(activity.getString(R.string.starting));
		    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    dialog.setCancelable(false);
		    dialog.setIndeterminate(true);
		    dialog.show();
	 }

	@Override
	protected Boolean doInBackground(Intent... arg0) {

	    try {
		 activity.startActivity(arg0[0]);
		 if(destroyActivity)
			 activity.finish();
		 dialog.dismiss();
		 dialog=null;
		 } catch (ActivityNotFoundException e) {
             Log.i("Actia AsynckTask", "Activity Not Found Exception: "+e);
             this.cancel(true);
             dialog.dismiss();
    		 dialog=null;
         }
		return null;
	}
	
	 @Override
	 protected void onCancelled(Boolean flag){
		 
	 }
	 
	 @Override
	 protected void onPostExecute(Boolean flag) {
		 if(dialog!=null)
			 dialog.dismiss();
	 }

}
