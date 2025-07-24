package com.actia.utilities.utilities_internet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.actia.mexico.launcher_t12_generico_2018.R;

public class AsynckCheckInternetConn extends AsyncTask<Void, Void, Boolean>{

	Context context=null;
	private ProgressDialog dialog;
	
	public AsynckCheckInternetConn(Context context){
		this.context=context;
	}
	
	 @Override
	 protected void onPreExecute() {
		    dialog = new ProgressDialog(context);
		    dialog.setMessage(context.getString(R.string.load_wait_please));
		    dialog.setTitle(context.getString(R.string.loading));
		    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    dialog.setCancelable(false);
		    dialog.setIndeterminate(true);
		    dialog.show();
	 }
	
	@Override
	protected Boolean doInBackground(Void... params) {
		 if (isNetworkAvailable()) {
			 try {
				 NetworkInterface redInterface = NetworkInterface
						 .getByInetAddress(InetAddress.getByName("192.168.1.2"));

				 for (int i = 0; i <= 5; i++) {

					 // build the next IP address
					 String addr = "192.168.1.2";
					 InetAddress pingAddr = InetAddress.getByName(addr);

					 Log.e("PING", pingAddr.getHostAddress());
					 // 50ms Timeout for the "ping"
					 if (pingAddr.isReachable(redInterface, 0, 50)) {
						 Log.e("PING reachable", pingAddr.getHostAddress());
						 return true;
					 }
				 }
			 } catch (IOException ex) {
				 Log.e("PING", "Error PING: ", ex);
				 return false;
			 }
		 }
		    return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if(dialog!=null && dialog.isShowing())
			dialog.dismiss();
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	         = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
}
