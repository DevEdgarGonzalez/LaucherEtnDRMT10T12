package com.actia.utilities.utilities_external_storage;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;

import java.io.File;
//import android.os.ServiceManager;
//import android.os.storage.IMountService;

/**
 * Check if external sdcard is mounted
 */

public class CheckExternalStorage {
//	IMountService mountService;
	/**
	 * check external sdcard state
	 * @return mount if external sdcard is mounted
	 */
//	public String getExternalStorageState(){
//		ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
//		mountService = IMountService.Stub.asInterface(ServiceManager
//                .getService("mount"));
//		try {
//			return mountService.getVolumeState(Config.getPATH_SDCARD());
//		}catch (RemoteException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public String getExternalStorageState(){
		String mount = "";
		ConfigMasterMGC config = new ConfigMasterMGC();
		String path = config.getIMAGE_HOME_PATH_APPS();

		File file = new File(path);
			if(file.exists())
				mount = ConstantsApp.STATE_SD_CARD_MOUNTED;

		return mount;
	}

}

