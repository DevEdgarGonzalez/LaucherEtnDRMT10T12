package com.actia.menu_maintance;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_external_storage.HandleXML;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Set device.
 */
public class ConfigDevice{
	Context context=null;


	/**
	 * Interface definition for a callback to be invoked when the device was configured.
	 * IDBus, IDDevice etc..
	 */
	public interface OnFinishConfigListener {
		void onFinish();
	}

	/**
	 * Interface definition for a callback to be invoked when
	 * the tactil_info file is analyzed and the information will be display.
	 */
	public interface OnLoadConfigDevice {
		void OnFinishLoadConfigDeviceListener(Boolean isConfig, String idDevice, String idBus);
	}
	/**
	 * Set context
	 * @param context A context
	 */
	public void setContext(Context context){
		this.context=context;
	}
	
	/**
	 * Get status config device asynchronously
	 */
	public void getStatusConfigAsynk(OnLoadConfigDevice interf){
		new ConfigDeviceAsynk(interf).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	/**
	 * Set device
	 * @param idDevice device id
	 * @param idBus bus number
	 * @param interf callback when set is finished.
	 */
	public void setConfiDevice(String idDevice, String idBus, OnFinishConfigListener interf){
		new setConfigDeviceAsynk(interf).execute(context.getString(R.string.name_tactil_config_device) + idBus + context.getString(R.string.type_tactil_config_device)+ idDevice);
	}
	
	/**
	 * Set device asynchronously
	 */
	private class setConfigDeviceAsynk extends AsyncTask<String, Void,Void>{

		 private ProgressDialog dialog;
		 OnFinishConfigListener interf;
		 
		setConfigDeviceAsynk(OnFinishConfigListener interf){
			this.interf=interf;
		}

		@Override
		 protected void onPreExecute() {
		     super.onPreExecute();
		 	dialog = new ProgressDialog(context);
			dialog.setMessage(context.getString(R.string.config));
	        dialog.show();
		     }
		 
		@Override
		protected Void doInBackground(String... params) {
			ConfigMasterMGC ConfigMaster = ConfigMasterMGC.getConfigSingleton();
			File tactil_info=new File(ConfigMaster.getXML_TACTIL_INFO());
			
			if(tactil_info.exists() && tactil_info.length()>0){
				HandleXML xml=new HandleXML(context);
				Document doc = xml.getDomElement(tactil_info.getPath());
				
				if(doc!=null){
					NodeList nl = doc.getElementsByTagName("tactil");
					if(nl.getLength()>0){
						     Element e = (Element) nl.item(0);
						     if(e.getElementsByTagName("device_id").getLength()>0){
						    	 NodeList nodo = e.getElementsByTagName("device_id");
					    		 Element i = (Element)nodo.item(0);
					    		 i.setTextContent(params[0]);
					    		
								 Transformer tFormer;
						            try {
						            	tFormer = TransformerFactory.newInstance().newTransformer();
						    			tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
						    			Source source = new DOMSource(doc);
						    		    Result result = new StreamResult(tactil_info.getPath());
										tFormer.transform(source, result);
									} catch (TransformerException | TransformerFactoryConfigurationError e1) {
										e1.printStackTrace();
										Log.e("ConfigDevice",e1.toString());
									}
							 }else Log.e("ConfigDevice","error device id");
					}else Log.e("ConfigDevice","error tactil");
				}else Log.e("ConfigDevice","document null");
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		 super.onPostExecute(result);
		 
		 if(dialog!=null && dialog.isShowing())
			 dialog.dismiss();
		 interf.onFinish();
		}
		
	}
	
	/**
	 * get config device asynchronously
	 */
	private class ConfigDeviceAsynk extends AsyncTask<Void, Void,String[]>{

		OnLoadConfigDevice interf;
		
		ConfigDeviceAsynk(OnLoadConfigDevice interf){
			this.interf=interf;
		}
		 
		@Override
		protected String[] doInBackground(Void... params) {
			Log.i("ConfigDevice","ConfigDevice asynk");
			ConfigMasterMGC ConfigMaster = ConfigMasterMGC.getConfigSingleton();
			File tactil_info=new File(ConfigMaster.getXML_TACTIL_INFO());
			
			if(tactil_info.exists() && tactil_info.length()>0){
				HandleXML xml=new HandleXML(context);
				Document doc = xml.getDomElement(tactil_info.getPath());
				
				if(doc!=null){
					ReadFileExternalStorage extSD=new ReadFileExternalStorage();
					String[] config=extSD.getConfig(doc);
					return config;
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
			if (result != null) {
				if (result.length == 2) {
					ContextApp.idBuss = result[0];
					ContextApp.idSeat = result[1];
				}
				if (interf!=null){
					interf.OnFinishLoadConfigDeviceListener(true, result[0], result[1]);
				}
			} else {
				ContextApp.idBuss = "000000";
				ContextApp.idSeat = "00";
				if (interf!=null){
					interf.OnFinishLoadConfigDeviceListener(false, "000000", "00");
				}
			}
		}
	}
}
