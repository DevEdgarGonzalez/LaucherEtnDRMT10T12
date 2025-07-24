package com.actia.menu_maintance;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.mexico.tactilv4.JNICommand;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

/**
 * Created by Edgar Gonzalez on 07/03/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class AsynckInstallAPPFromDirAPP extends AsyncTask<Void, Void, String> {

    private final String TAG= "AsynckInstallAPPFromDirAPP";
    private OnInstallListener interf= null;
    private final ConfigMasterMGC configSingleton;
    private final Context context;

    public interface OnInstallListener {
        void onInstallFinished(String resultInstall, String resultUninstall);
    }

    public AsynckInstallAPPFromDirAPP(Context context, OnInstallListener interf){
        this.interf=interf;
        this.context=context;
        configSingleton = ConfigMasterMGC.getConfigSingleton();
    }

    @Override
    protected String doInBackground(Void... params) {
        // TODO Auto-generated method stub
        String apkInstalled="";
        File appDir = new File(configSingleton.getAppDirPath());
        if(appDir.exists() && appDir.isDirectory()){

            File[] apks = appDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    // TODO Auto-generated method stub
                    String lowercaseName = filename.toLowerCase(Locale.getDefault());
                    return lowercaseName.endsWith(".apk");
                }
            });

            for(File apk:apks){
                install(apk);
                apkInstalled=apkInstalled+ context.getString(R.string.installed)+apk.getName()+"\n";
            }
        }
        return apkInstalled;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        interf.onInstallFinished(result,context.getString(R.string.empty));
    }

	/*private boolean install(File apk){
		boolean isInstalled=true;
		Process process=null;
		if(!apk.exists() || apk.length()==0){
			Log.e(TAG, "ERROR: "+apk.getPath()+" not exist");
			isInstalled=false;
		}else{
			try {
				String apkPath=apk.getPath();
	 			process = Runtime.getRuntime().exec(new String[]{ "su", "-c","pm install -r "+"\""+apkPath+"\""});
				process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isInstalled=false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isInstalled=false;
		}finally{
			if(process!=null)
				process.destroy();
		}

		}
		return isInstalled;
	}*/


    private boolean install(File apk){
        boolean isInstalled=true;
        if(!apk.exists() || apk.length()==0){
            Log.e(TAG, "ERROR: "+apk.getPath()+" not exist");
            isInstalled=false;
        }else{
            try {
                String apkPath=apk.getPath();
                JNICommand.runCommand("pm install -r "+"\""+apkPath+"\"");
            }catch(Exception e){
                Log.e(TAG, "Error InstallAPK.java: "+e.toString());
            }
        }
        return isInstalled;
    }
}