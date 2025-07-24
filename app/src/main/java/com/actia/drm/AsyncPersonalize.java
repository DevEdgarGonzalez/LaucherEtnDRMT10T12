package com.actia.drm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;

class AsyncPersonalize extends AsyncTask<Void, Void, Boolean> {
	
	private Context context=null;
	private ProgressDialog dialog=null;
	private onPersonalizeListener interf=null;

	AsyncPersonalize(Context context, onPersonalizeListener interf){
		this.context=context;
		this.interf=interf;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getString(R.string.working));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
//	    dialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String TAG = "AsyncPersonalize";
		try {
			Runtime.initialize(this.context.getDir("wasabi", Context.MODE_PRIVATE)
			        .getAbsolutePath());
			if (!Runtime.isPersonalized())
				Runtime.personalize();
		return true;
		} catch (NullPointerException | ErrorCodeException e) {
			Log.e(TAG, "runtime initialization or personalization error: "
			        + e.getLocalizedMessage());
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		interf.onPersonalized(result);
	}

}
