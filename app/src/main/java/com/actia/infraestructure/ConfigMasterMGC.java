package com.actia.infraestructure;

import static com.actia.infraestructure.BaseActivity.mycontext;
import static com.actia.infraestructure.BaseActivity.prefExtSd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.actia.audiolibros.AbookGenreActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.PeliculasActivity;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utils_language.UtilsLanguage;

/**
 * Singleton Class that containing all the variables to set the launcher.
 * The JSONConfig file has all apps, games, class, package, image of the Launcher.
 */

public class ConfigMasterMGC {

	protected boolean isFirtTime=false;

	private static ConfigMasterMGC singleton=null;

	//Maintance Menu File Manager class and packet
	public String keyMaintance= "250689";
	public String keyTestApp= "007007";
	protected String apkFileManagerClass= "com.android.documentsui.LauncherActivity";
	protected String apkFileManagerPackage= "com.android.documentsui";

	protected String apkFileManagerClassIPS= "com.estrongs.android.pop.view.FileExplorerActivity";
	protected String apkFileManagerPackageIPS= "com.estrongs.android.pop";

	//quiz
	protected String apkQuizClass= "com.android.Sa4.Encuesta";
	protected String apkQuizPackage= "com.android.Sa4";

	public static boolean enableCustomization = true;

	protected String PATH_SDCARD = new String(prefExtSd);
	//Splash
	final String SPLASH_IMG= getPATH_SDCARD() + "/splash/splash.jpeg";
	protected final int TIME_SHOW_IMG = 5 * 1000;

	protected String ABSOLUTE_INTERNAL_PATH = new String("/mnt/sdcard");
	public String MUSIC_PATH = getPATH_SDCARD() + "/music";
	public String VIDEO_PATH = getPATH_SDCARD() + "/video";
	public String IMAGE_HOME_PATH = getPATH_SDCARD() + "/home/client";
	public String IMAGE_HOME_PATH_APPS = getPATH_SDCARD() + "/home/applications/";
	public String CHILDREN_PATH= getPATH_SDCARD() + "/childrens";
	public String MUSIC_CHILDREN_PATH= getPATH_SDCARD() + "/childrens/music";
	public String VIDEO_CHILDREN_PATH= getPATH_SDCARD() + "/childrens/movie";
	public String AUDIOBOOKSCHILDREN_PATH= getPATH_SDCARD() + "/childrens/audiobooks";
	public String NOSOTROS_PATH = getPATH_SDCARD() + "/nosotros";
	public String BIENVENIDA_TEXT_FILE = getPATH_SDCARD() + "/config/Bienvenida.txt";


	public String AUDIOBOOKS_PATH= getPATH_SDCARD() + "/abooks";
	private final String nameGenreDefaultAbooks = "General";

	@SuppressLint("SdCardPath")
	private String XML_ENCUESTA_DIR = new String(getPATH_SDCARD() + "/config/encuesta/");
	private String ENCUESTAS_DIR = new String(getAbsoluteInternalPath() + "/encuestas/");
	private String XML_ENCUESTA_FILE = new String(getPATH_SDCARD() + "/config/encuesta/survey.xml");



	public String CONFIG_DIRECTORY= getPATH_SDCARD() + "/config";
	public String CONFIG_FILE= getPATH_SDCARD() + "/config/config.json";
	@SuppressLint("SdCardPath")
	private final String XML_TACTIL_INFO= "/mnt/sdcard/tactil_info.xml";
	private final String XML_TACTIL_INFO_EXTSD= getPATH_SDCARD() + "/config/tactil_info.xml";
	private final String PathAdvertising= getPATH_SDCARD() + "/pubvideo";
	private final String PathClientVideos = getPATH_SDCARD() + "/clientvideo";
	private final String PathHelpVideo = getPATH_SDCARD() + "/ayuda";
	private final String PathClientImg = getPATH_SDCARD() + "/clientimg";
	private String PathImgAdvertising= getPATH_SDCARD() + "/pubimg";
	private String appDirPath= getPATH_SDCARD() + "/aplicaciones";

	private String PLUS_MEDIA_PATH = new String(getPATH_SDCARD() + "/multimedia/");


	private final String pathBanners= getPATH_SDCARD() + "/banner";

    public boolean isBarDisplay=false;

    ///*******************DRM*****************

	private final String pathUserDRM= getPATH_SDCARD() + "/config/key.xml";
	private final String pathSwankDRM= getPATH_SDCARD() + "/config/keysw.xml";

	private final String pathTokens= getPATH_SDCARD() + "/drm";

	//***************************************


	private final String nameJsonWebView =  "web.json";
	private final String nameMessageTxt =  "long_content.txt";
	private final String nameTitleTxt =  "short_content.txt";


	/*
	STEVIE
	 */
	public String pathWebQuien= getPATH_SDCARD() + "/web/quienes_somos/index.html";
	public String pathWebEnvios= getPATH_SDCARD() + "/web/tap_envios/index.html";
	public String pathAbout = getPATH_SDCARD() + "/web/tap_royal/index.html";

	private String pageServer=null;

	private ArrayList<ItemsHome> apps=null;
	private ArrayList<ItemsHome> homeSubmenus = null;
	private ArrayList<ItemsHome> appsKids=null;
	private ArrayList<ItemsHome> appsGames=null;
	private ArrayList<ItemsHome> appsKidsGames=null;
	private ArrayList<ItemsHome> photosHome=null;

	private boolean checkMultimediaupdate=true;

	public ConfigMasterMGC(){
	}

	static String JSONConfig=null;

	public String getAppDirPath() {
		return appDirPath;
	}

	public void setAppDirPath(String appDirPath) {
		this.appDirPath = appDirPath;
	}
	public boolean isCheckMultimediaupdate() {
		return checkMultimediaupdate;
	}

	public void setCheckMultimediaupdate(boolean checkMultimediaupdate) {
		this.checkMultimediaupdate = checkMultimediaupdate;
	}

	public String getPathImgAdvertising() {
		return PathImgAdvertising;
	}

	public void setPathImgAdvertising(String pathImgAdvertising) {
		PathImgAdvertising = pathImgAdvertising;
	}

	public String getPathSwankDRM() {
		return pathSwankDRM;
	}

	public String getPathUserDRM() {
		return pathUserDRM;
	}

	public String getPathTokens() {
		return pathTokens;
	}


	/**
	 * Singleton Class that containing all the variables to set the launcher
	 * @return a Advertising's path in the external storage
	 */
	public String getPathAdvertising() {
		return PathAdvertising;
	}

	public String getPathClientVideos(){
		return PathClientVideos;
	}

	public String getPathClientImg(){
		return PathClientImg;
	}

	/**
	 * @return Banners' path in the external storage
	 */
	public String getPathBanners() {
		return pathBanners;
	}

	/**
	 * @return tactil_info's path in the internal storage
	 */
	public String getXML_TACTIL_INFO() {
		return XML_TACTIL_INFO;
	}

	/**
	 * @return tactil_info's path in the external storage
	 */
	public String getXML_TACTIL_INFO_EXTSD() {
		return XML_TACTIL_INFO_EXTSD;
	}

	/**
	 * @return xilon's ip
	 */
	public String getPageServer() {
		return this.pageServer;
	}

	/**
	 */
	public void setPageServer(String pageServer) {
		this.pageServer = pageServer;
	}

	/**
	 * @return the class to launch the Quiz apk
	 */
	public String getApkQuizClass() {
		return apkQuizClass;
	}

	/**
	 * @return the package name to launch the Quiz apk
	 */
	public String getApkQuizPackage() {
		return apkQuizPackage;
	}

	/**
	 * @return true if the initial advertising was already deployed then the home is display
	 */
	public boolean isFirtTime() {
		return isFirtTime;
	}

	/**
	 *Set flag to know if the advertising was already deployed
	 *@param isFirtTime set flag advertising
	 */
	public void setFirtTime(boolean isFirtTime) {
		this.isFirtTime = isFirtTime;
	}

	/**
	 * @return music directory's path of the general section.
	 */
	public String getMUSIC_PATH() {
		return MUSIC_PATH;
	}

	/**
	 * @return video directory's path of the general section.
	 */
	public String getVIDEO_PATH() {
		return VIDEO_PATH;
	}

	/**
	 * @return The path of the client's images display in the Launcher's home.
	 */
	public String getIMAGE_HOME_PATH() {
		return IMAGE_HOME_PATH;
	}

	/**
	 * @return The path of the app's images.
	 */
	public String getIMAGE_HOME_PATH_APPS() {
		return IMAGE_HOME_PATH_APPS;
	}

	/**
	 * @return The path of the section children.
	 */
	public String getCHILDREN_PATH() {
		return CHILDREN_PATH;
	}

	public String getNameGenreDefaultAbooks() {
		return nameGenreDefaultAbooks;
	}
	/**
	 * @return The path of the section children.
	 */
	public String getMUSIC_CHILDREN_PATH() {
		return MUSIC_CHILDREN_PATH;
	}

	/**
	 * @return video directory's path of the children section.
	 */
	public String getVIDEO_CHILDREN_PATH() {
		return VIDEO_CHILDREN_PATH;
	}
	/**
	 * @return audio books directory's path of the children section.
	 */
	public String getAUDIOBOOKSCHILDREN_PATH() {
		return AUDIOBOOKSCHILDREN_PATH;
	}
	/**
	 * @return audio books directory's path of the general section.
	 */
	public String getAUDIOBOOKS_PATH() {
		return AUDIOBOOKS_PATH;
	}

	public String getNOSOTROS_PATH() {
		return NOSOTROS_PATH;
	}

	/**
	 * @return path config directory.
	 */
	public String getCONFIG_DIRECTORY() {
		return CONFIG_DIRECTORY;
	}
	/**
	 * @return config file's path.
	 */
	public String getCONFIG_FILE() {
		return CONFIG_FILE;
	}
	/**
	 * @return time show image.
	 */
	public int getTimeShowImg() {
		return TIME_SHOW_IMG;
	}
	/**
	 * @deprecated
	 */
	public String getSPLASH_IMG() {
		return SPLASH_IMG;
	}
	/**
	 * @return key main.
	 */
	public String getKeyMaintance() {
		return keyMaintance;
	}

	public String getKeyTestApp() {
		return keyTestApp;
	}

	/**
	 * @return the class name to launch the FileManager
	 */
	public String getApkFileManagerClassIPS() {
		return apkFileManagerClassIPS;
	}
	/**
	 * @return the package name to launch the FileManager
	 */
	public String getApkFileManagerPackageIPS() {

		return apkFileManagerPackageIPS;
	}

	/**
	 * @return the class name to launch the FileManager
	 */
	public String getApkFileManagerClass() {
		return apkFileManagerClass;
	}
	/**
	 * @return the package name to launch the FileManager
	 */
	public String getApkFileManagerPackage() {
		return apkFileManagerPackage;
	}

	public String getNameJsonWebView() {
		return nameJsonWebView;
	}

	public String getNameMessageTxt() {
		return nameMessageTxt;
	}

	public String getNameTitleTxt() {
		return nameTitleTxt;
	}

	public String getAbsoluteInternalPath(){
		return ABSOLUTE_INTERNAL_PATH;
	}

	public String getXML_ENCUESTA_DIR() {
		return XML_ENCUESTA_DIR;
	}

	public String getENCUESTAS_DIR() {
		return ENCUESTAS_DIR;
	}

	public String getXML_ENCUESTA_FILE() {
		return XML_ENCUESTA_FILE;
	}

	public String getPLUS_MEDIA_PATH() {
		return PLUS_MEDIA_PATH;
	}

	public void setPLUS_MEDIA_PATH(String ruta) {
		PLUS_MEDIA_PATH += ruta;
	}

	/**
	 * @return external sdcard path.
	 */
	public String getPATH_SDCARD() {
		return PATH_SDCARD;
	}

	/**
	 * @return a new instance of the singleton if it´s null
	 */
	public static ConfigMasterMGC getConfigSingleton(){

		if(singleton==null)
			   singleton= new ConfigMasterMGC();

		return singleton;
	}

	/**
	 * @return true if json file exist and it´s can be parseable, also setting the JSONConfig file.
	 */
	public boolean getJSONConfig() {

		if(JSONConfig!=null)
			return true;

	        String json = null;
	        File jsonFile=new File(getCONFIG_DIRECTORY()+"/config.json");
	        FileInputStream stream=null;
	        File dir=new File(getCONFIG_DIRECTORY());
			if(dir.exists() && dir.isDirectory() && dir.list().length>0 && dir.canRead() && jsonFile.exists() && jsonFile.canRead() && jsonFile.length()>0){
					try {
						stream = new FileInputStream(jsonFile);
						FileChannel fc = stream.getChannel();
		                MappedByteBuffer bb;
					    bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		                json = Charset.defaultCharset().decode(bb).toString();
					    JSONConfig=json;
					}catch (FileNotFoundException e) {
					    e.printStackTrace();
					    JSONConfig=null;
					    return false;
				    }catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JSONConfig=null;
						return false;
					}finally {
		                try {
							stream.close();
						} catch (IOException e) {
							e.printStackTrace();
							return false;
						}
		              }
				return true;
			}else return false;
    }

	/**
	 * Get ArrayList with all apps display in the Home. Is necessary first call to getJSONConfig() function.
	 *
	 * @param room The name of the room.
	 * @return an ArrayList of itemshome
	 */
	public ArrayList<ItemsHome> getAppHome(String room) {

		if (apps != null)
			return apps;

		apps = new ArrayList<ItemsHome>();
		try {
			JSONObject configJSON = new JSONObject(JSONConfig);
			JSONArray home = configJSON.getJSONArray(room);
			JSONObject app = home.getJSONObject(0);
			JSONArray itemApps = app.getJSONArray("app");
			JSONObject item = null;

			for (int i = 0; i < itemApps.length(); i++) {
				item = itemApps.getJSONObject(i);

				if (item.getInt("sub_menu") == 0) {
					if (isValid(item.getString("className"), item.getString("packageName"))) {
						apps.add(new ItemsHome(i,
								item.getString("title"),
								"",//item.getString("title_en"), //descomentar en caso de que exista la llave en el config.json
								item.getString("packageName"),
								item.getString("className"),
								item.getString("intent"),
								item.getString("url"),
								getPathFromImage(item.getString("img")),
								true, true, item.getInt("browser"),
								item.getInt("sub_menu")));
					}
				}


			}

		} catch (JSONException e) {
			e.printStackTrace();
			apps = null;
			return null;
		}
		return apps;
	}



	public ArrayList<ItemsHome> geSubMenuHome(String room) {

		if (homeSubmenus != null)
			return homeSubmenus;

		homeSubmenus = new ArrayList<ItemsHome>();
		try {
			JSONObject configJSON = new JSONObject(JSONConfig);
			JSONArray home = configJSON.getJSONArray(room);
			JSONObject app = home.getJSONObject(0);
			JSONArray itemhomeSubmenus = app.getJSONArray("app");
			JSONObject item = null;

			for (int i = 0; i < itemhomeSubmenus.length(); i++) {
				item = itemhomeSubmenus.getJSONObject(i);

				if (item.getInt("sub_menu") == 1) {
					if (isValid(item.getString("className"), item.getString("packageName"))) {
						homeSubmenus.add(new ItemsHome(i,
								item.getString("title"),
								"",//item.getString("title_en"), //descomentar en caso de que exista la llave en el config.json
								item.getString("packageName"),
								item.getString("className"),
								item.getString("intent"),
								item.getString("url"),
								getPathFromImage(item.getString("img")),
								true, true, item.getInt("browser"),
								item.getInt("sub_menu")));
					}
				}


			}

		} catch (JSONException e) {
			e.printStackTrace();
			homeSubmenus = null;
			return null;
		}
		return homeSubmenus;
	}
	/**
	 * Get ArrayList with all apps to display in the Kids section. Is necessary first call to getJSONConfig() function.
	 * @param room The name of the room.
	 * @return an ArrayList of itemshome
	 */
	public ArrayList<ItemsHome> getAppHomeKids(String room){

		if(appsKids!=null)
			return appsKids;

		appsKids=new ArrayList<ItemsHome>();
		try {
				JSONObject configJSON = new JSONObject(JSONConfig);
				JSONArray home = configJSON.getJSONArray(room);
				JSONObject app = home.getJSONObject(0);
				JSONArray itemApps = app.getJSONArray("app");
				JSONObject item=null;

				for(int i=0;i<itemApps.length();i++){
					item=itemApps.getJSONObject(i);
					appsKids.add(new ItemsHome(i,
							item.getString("title"),
							"",//item.getString("title_en"), //descomentar en caso de que exista la llave en el config.json
							item.getString("packageName"),
							item.getString("className"),
							item.getString("intent"),
							item.getString("url"),
							getPathFromImage(item.getString("img")),
							true, true,item.getInt("browser"),
							item.getInt("sub_menu")));
			    }

			} catch (JSONException e) {
				e.printStackTrace();
				appsKids=null;
				return null;
			}
			return appsKids;
	}

	/**
	 * Get ArrayList with photos to display in the Home section. Is necessary first call to getJSONConfig() function.
	 * @return an ArrayList of itemshome
	 */
	public ArrayList<ItemsHome> getPhotosHome(){

		if(photosHome!=null)
			return photosHome;

		File home=null;

		photosHome=new ArrayList<ItemsHome>();
    	int count=0;
    	home = new File(getIMAGE_HOME_PATH());

        if (home.listFiles(new FileExtensionFilterImages()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilterImages())) {
            	photosHome.add(new ItemsHome(count++,null, null, null, null, null, null, file.getPath(), false, false, 0, 0));
            }
        }else photosHome.add(new ItemsHome(count++,null, null, null, null, null, null, null, false, false, 0, 0));
        return photosHome;
	}

	/**
	 * Get ArrayList with apps to display in the Games Home section. Is necessary first call to getJSONConfig() function.
	 * @return an ArrayList of itemshome
	 */
	public ArrayList<ItemsHome > getGeneralGames(){
		JSONObject configJSON=null;
		if(appsGames!=null)
			return appsGames;

		appsGames=new ArrayList<ItemsHome>();
		try {
			configJSON = new JSONObject(JSONConfig);
			JSONArray home = configJSON.getJSONArray("home");
			JSONObject app = home.getJSONObject(1);
			JSONArray itemApps = app.getJSONArray("games");
			JSONObject item=null;

			if(itemApps.length()>0){
				for(int i=0;i<itemApps.length();i++){
					item=itemApps.getJSONObject(i);
					appsGames.add(new ItemsHome(i,
										   item.getString("title"),
										   "",//item.getString("title_en"), //descomentar en caso de que exista la llave en el config.json
										   item.getString("packageName"),
										   item.getString("className"),
										   item.getString("intent"),
										   item.getString("url"),
										   getPathFromImage(item.getString("img")),
										   true, true,item.getInt("browser"),
										   item.getInt("sub_menu")));
			    }
				return appsGames;
			}else return null;
		} catch (JSONException e) {
			appsGames=null;
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get ArrayList with apps to display in the Games Kids section. Is necessary first call to getJSONConfig() function.
	 * @return an ArrayList of itemshome
	 */
	public ArrayList<ItemsHome > getKidsGames(){

		if(appsKidsGames!=null)
			return appsKidsGames;

		appsKidsGames=new ArrayList<ItemsHome>();
		try {
			JSONObject configJSON = new JSONObject(JSONConfig);
			JSONArray home = configJSON.getJSONArray("childrens");
			JSONObject app = home.getJSONObject(1);
			JSONArray itemApps = app.getJSONArray("games");
			JSONObject item=null;

			if(itemApps.length()>0){
				for(int i=0;i<itemApps.length();i++){
					item=itemApps.getJSONObject(i);
					appsKidsGames.add(new ItemsHome(i,
										   item.getString("title"),
							               "",//item.getString("title_en"), //descomentar en caso de que exista la llave en el config.json
										   item.getString("packageName"),
										   item.getString("className"),
										   item.getString("intent"),
										   item.getString("url"),
										   getPathFromImage(item.getString("img")),
										   true, true,item.getInt("browser"),
										   item.getInt("sub_menu")));
			    }
				return appsKidsGames;
			}else return null;
		} catch (JSONException e) {
			appsKidsGames=null;
			e.printStackTrace();
			return null;
		}
	}

	public File[] getItemsNosotros() {
		ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
		String pathGenre = configSingleton.getNOSOTROS_PATH();
		File home = new File(pathGenre);
		File[] itemsGnrals = home.listFiles(UtilsLanguage.getFileExtensionImageByLanguage(home));
		ArrayList<File> itemsNosotros =  new ArrayList<>();

		if (itemsGnrals.length > 0) {
			for (File file : itemsGnrals) {
				if (file.isDirectory() && file.listFiles(new FileExtensionFilterImages()).length > 0) {
					itemsNosotros.add(file);
				}
			}
		}


		return itemsNosotros.toArray(new File[itemsNosotros.size()]);
	}



	public File[] getItemsSd(String pathMain){
		File home = new File(pathMain);
		File[] itemsGnrals = home.listFiles();
		ArrayList<File> itemsNosotros =  new ArrayList<>();

		if (itemsGnrals.length > 0) {
			for (File file : itemsGnrals) {
				if (file.isDirectory() && file.listFiles(new FileExtensionFilterImages()).length > 0) {
					itemsNosotros.add(file);
				}
			}
		}


		return itemsNosotros.toArray(new File[itemsNosotros.size()]);
	}


	/**
	 * Get the path of each image set in the jsonconfig file.
	 * @param name The name of the image.
	 * @return path of the image.
	 */
	public String getPathFromImage(String name){
		   ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
		   File file=new File(configSingleton.getIMAGE_HOME_PATH_APPS()+name);
		   if(file.exists())
			   return file.getPath();
	return "";
	}

	public String getPathHelpVideo(){
		return PathHelpVideo;
	}


	public boolean isValid(String className, String modulo) {

		if (className.contains(AbookGenreActivity.class.getName())) {
			String pathAbook = AUDIOBOOKS_PATH;
			File pathSource;

			if (modulo != null && !modulo.isEmpty()) {
				pathSource = new File(pathAbook, modulo);
			} else {
				pathSource = new File(pathAbook);
			}

			if (!pathSource.exists()) return false;


//            File[] files = pathSource.listFiles(new FileExtensionFilterMusic());
			File[] files = pathSource.listFiles();
            return files != null && files.length != 0;

		} else if (className.contains(PeliculasActivity.class.getName())) {

			String pathMovie = VIDEO_PATH;
			File pathSourceMovie;
			if (modulo != null && !modulo.isEmpty()) {
				pathSourceMovie = new File(pathMovie, modulo);

			} else {
				pathSourceMovie = new File(pathMovie);
			}

			if (!pathSourceMovie.exists()) return false;
//            File[] files = pathSourceMovie.listFiles(new FileExtensionFilterMusic());
			File[] files = pathSourceMovie.listFiles();
            return files != null && files.length != 0;
		}

		return true;
	}

	/**
	 * @return jsonSeasonRefresh file's path.
	 */
	public String getBIENVENIDA_TEXT_FILE() {
		return BIENVENIDA_TEXT_FILE;
	}
}
