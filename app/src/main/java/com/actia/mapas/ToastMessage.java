package com.actia.mapas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastMessage {

//	static final String TAG = "ToastMessage";

	@SuppressLint("StaticFieldLeak")
	private static ToastMessage mInstance;
	private Context mAppContext = null;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private final Object mMutex = new Object();
	private String mMsg = "";
	private Toast mToast = null;

	@SuppressLint("ShowToast")
	private ToastMessage(Context context) {
		mAppContext = context;
		mToast = Toast.makeText(mAppContext, "", Toast.LENGTH_LONG);		
	}

	public static ToastMessage init(Context context) {
		if (mInstance == null) {
			mInstance = new ToastMessage(context);
		}
		return mInstance;
	}

	public static ToastMessage get() {
		return mInstance;
	}
	
	public void showToastMessage(int msg_id) {
		synchronized (mMutex) {
			mMsg = mAppContext.getString(msg_id);
			if (mAppContext != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mToast.setText(mMsg);
						mToast.show();
					}
				});

			}
		}
	}

//	public void showAlertMessage(int title_id, int msg_id, Context context) {
//		synchronized (mMutex) {
//			mMsg = mAppContext.getString(msg_id);
//			@SuppressWarnings("UnusedAssignment") String mTitle = "";
//			if (title_id > 0) {
//				mTitle = mAppContext.getString(title_id);
//			}else{
//				mTitle = "";
//			}
//			if (context != null) {
//				Builder mAlertDialogBuilder = new Builder(context);
//				// set title
//				mAlertDialogBuilder.setTitle(mTitle);
//				// set dialog message
//				mAlertDialogBuilder
//							.setMessage(mMsg)
//							.setCancelable(false)
//							.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,int id) {
//
//								}
//							  });
//
//				// create alert dialog
//				AlertDialog alertDialog = mAlertDialogBuilder.create();
//
//				// show it
//				alertDialog.show();
//			}
//		}
//	}
}
