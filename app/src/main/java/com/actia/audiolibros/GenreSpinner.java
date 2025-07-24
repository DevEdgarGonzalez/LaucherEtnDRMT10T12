package com.actia.audiolibros;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.actia.mexico.launcher_t12_generico_2018.R;

/**
 * Genre Movies
 */
public class GenreSpinner {
	
	Context context=null;
	private Object filter=null;
	private OnCompleteSearchGenre interf=null;
	private ArrayList<String> colores=null;

	public interface OnCompleteSearchGenre{
		void OnCompleteSearchGenreListener(SpinAdapterGenre adapter);
	}
	
	public GenreSpinner(Context context,OnCompleteSearchGenre interf){
		this.context=context;
		this.interf=interf;
	}
	
	/**
	 * Get genres from external sdcard
	 * @param SDCardPathDir genre path
	 * @param filter filter file
	 * @param colores set color
	 */
	public void getGenres(String SDCardPathDir,Object filter,ArrayList<String> colores){
		this.filter=filter;
		this.colores=colores;
		new Thread(new RunnableAdapter(SDCardPathDir)).start();
	}
	
	/**
	 * Create an arraylist with genres
	 */
	private class RunnableAdapter implements Runnable {
		  
		  String SDCardPath;

	      RunnableAdapter(String SDCardPath) {
	    	  this.SDCardPath=SDCardPath;
	      }
	      
	      @Override
	      public void run() {
	    	String[] datas=null;  
	  		File home = new File(SDCardPath);
			ArrayList<String> Genre = new ArrayList<>();
	  		if(home.exists() && home.isDirectory() && home.canRead()){
				File[] files = home.listFiles();
				if(files.length>0){
				    for (File file : files) {
				        if (file.isDirectory() && (file.listFiles((FilenameFilter) filter).length > 0))
				        	Genre.add(file.getName());
				    } 
				}
	  		}

			  //noinspection ConstantConditions
			  if(Genre!=null && Genre.size()>0){
				Collections.sort(Genre);
				datas=Genre.toArray(new String[Genre.size()]);
			}

			
	    	android.os.Message msg =  new android.os.Message();
           Bundle bundle = new Bundle();
           bundle.putStringArray("msg",datas);
           msg.setData(bundle);
           handlerAdapter.sendMessage(msg);
	      }
	   }
	 
	 /**
	  * Handler the Adapter spinner to genres
	  */
		@SuppressLint("HandlerLeak")
		private final Handler handlerAdapter = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				Bundle bundle = msg.getData();
				String[] arriveDatas = bundle.getStringArray("msg");
				if(arriveDatas!=null && arriveDatas.length>0){
					SpinAdapterGenre out = new SpinAdapterGenre(context, 0, R.layout.layout_spinner_genre, arriveDatas,colores);
					interf.OnCompleteSearchGenreListener(out);
				}else{
					interf.OnCompleteSearchGenreListener(null);
				}
			}
		};
}
