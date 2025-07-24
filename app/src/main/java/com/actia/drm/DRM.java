package com.actia.drm;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;

public class DRM {
	
	private Context context=null;
	private OnTokenRegisteredListener tokenInterf=null;
	private AsyncTask<Void, String, Void> regLicenses=null;
	
	DRM(Context context){
		this.context=context;
	}
	
	void setOnUserRegisterListener(String pathUserDRM, int idUser, OnUserRegisterListener interf){
		new AsyncRegisterUser(context,pathUserDRM,idUser,interf).execute();
	}
	
	void setLicensesRegister(Context context, File[] tokens, LinearLayout textView, ScrollView scroll){
		regLicenses=new AsyncRegisterLicenses(context,tokens, textView,scroll,tokenInterf).execute();
	}
	
	void setOnLicensesRegisterListener(OnTokenRegisteredListener interf){
		this.tokenInterf=interf;
	}
	
	void personalizeDevice(onPersonalizeListener interf){
		new AsyncPersonalize(context, interf).execute();
	}
	
	void stopRegisterLicenses(){
		if(regLicenses!=null)
			regLicenses.cancel(true);
	}
}
