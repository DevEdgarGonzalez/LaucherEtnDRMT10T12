package com.actia.drm;


import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.media.PlaylistProxy;
import com.intertrust.wasabi.media.PlaylistProxy.MediaSourceParams;
import com.intertrust.wasabi.media.PlaylistProxy.MediaSourceType;
import com.intertrust.wasabi.media.PlaylistProxyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerDRMActivity extends BaseActivity implements PlaylistProxyListener,
														   onPersonalizeListener,
        OnPreparedListener,
        OnErrorListener,
        OnCompletionListener {
	
	private final String TAG="PLAYER_DRM";
	VideoView videoView =null;
	private PlaylistProxy playerProxy;
	public static final String keyPathDRM="drm_path";
	private String moviePath=null;
	private ConfigMasterMGC configSingleton;
	Timer timerBanners=null;
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private boolean isGetBanners=false;
	private ArrayList<String> BannersShowed=null;
	//private Handler validationHandler = new Handler();
	private ImageView BannerView;
	protected Handler handlerBanner=null;
	private File[] bannersImg=null;
	int count=0;
	private ImageWorker w=null;
	private final int tiempo_visualizando_banner=8000;
	private int tiempo_entre_banners=900000; //15 min
	private File dirBanners;
	private View decorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_play);
		
		videoView = findViewById(R.id.videoView1);
		BannerView= findViewById(R.id.BannerDRM);
		MediaController mediaController = new MediaController(PlayerDRMActivity.this, false);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		configSingleton=ConfigMasterMGC.getConfigSingleton();
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null){ 
			moviePath = extras.getString(keyPathDRM);
			BannersShowed= new ArrayList<>();
			dirBanners=new File(configSingleton.getPathBanners());
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.null_path), Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
		DRM drm = new DRM(PlayerDRMActivity.this);
		drm.personalizeDevice(this);

		hideNavigationBar(this);

		decorView = getWindow().getDecorView();

		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if (visibility == 0){
					hideNavigationBar(PlayerDRMActivity.this);
				}
			}
		});
	}

	@Override
	public void onErrorNotification(int errorCode, String errorString) {
		Log.e(TAG, "PlaylistProxy Event: Error Notification, error code = " +
				errorCode + ", error string = " +
				errorString); 
		Toast.makeText(getApplicationContext(), getString(R.string.PlaylistProxy_Event) +
				errorCode + ", "+getString(R.string.error_string) +
				errorString, Toast.LENGTH_LONG).show();
		this.finish();
	}

	@Override
	public void onPersonalized(boolean error) {
		try {
			EnumSet<PlaylistProxy.Flags> flags = EnumSet.noneOf(PlaylistProxy.Flags.class);
			playerProxy = new PlaylistProxy(flags, this, new Handler());
			playerProxy.start();
		} catch (ErrorCodeException e) {
			Log.e(TAG, "playlist proxy error: " + e.getLocalizedMessage());
			Toast.makeText(getApplicationContext(), getString(R.string.error_playlist_proxy) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			this.finish();
		}
		
		ContentTypes contentType = ContentTypes.PDCF;
		MediaSourceParams params = new MediaSourceParams();
		params.sourceContentType = contentType.getMediaSourceParamsContentType();

		String proxy_url;
		try {
			
			proxy_url = playerProxy.makeUrl(moviePath, MediaSourceType.SINGLE_FILE,params);
			videoView.setOnErrorListener(this);
			videoView.setOnCompletionListener(this);
			videoView.setVideoURI(Uri.parse(proxy_url));
			videoView.requestFocus();
			videoView.setOnPreparedListener(this);


		} catch (Exception e) {
			Log.e(TAG, "playback error: " + e.getLocalizedMessage());
			Toast.makeText(getApplicationContext(),getString(R.string.error_playback) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			this.finish();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		videoView.start();
		getBanners();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		PlayerDRMActivity.this.finish();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(getApplicationContext(), getString(R.string.error)+extra+" - "+what, Toast.LENGTH_SHORT).show();
		this.finish();
		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseMP();
	}
	
	public void getBanners(){
		isGetBanners=true;
			if(dirBanners.exists() && dirBanners.listFiles(new FileExtensionFilterImages()).length>0){
				   resetHandlerBanners();
				   timerBanners=new Timer();
				   bannersImg=null;
				   bannersImg=dirBanners.listFiles(new FileExtensionFilterImages());
				   List<File> bannerList = Arrays.asList(bannersImg);
				   Collections.shuffle(bannerList);
				   bannersImg = bannerList.toArray(new File[bannerList.size()]);
			   	   w=new ImageWorker();
				//noinspection UnusedAssignment
				int x=tiempo_entre_banners=videoView.getDuration();
			   	   tiempo_entre_banners=tiempo_entre_banners-50000;//10 seg del primer banner y 40 seg de tolerancia
			   	   x=tiempo_entre_banners/(bannersImg.length);
			   	   //int x=60000;
			   	   if(x>tiempo_visualizando_banner)
			   		   tiempo_entre_banners=x;
			   	   Log.i(TAG, "Tiempo entre banners: "+x);
			   	   updateBanner();
			       //Toast.makeText(getApplicationContext(), "time: "+DeyalShowBanner/60000, Toast.LENGTH_LONG).show();
		    }
			isGetBanners=false;
	}
	
	public void resetHandlerBanners(){
		//validationHandler.removeCallbacks(mUpdateTimeTask);
		if(timerBanners!=null){
			Log.i(TAG, "cancel timer banners");
			timerBanners.cancel();
			timerBanners.purge();
			timerBanners=null;
		}
		if(handlerBanner!=null)
			handlerBanner.removeCallbacks(delayBanner);
	}
	
	public void sendLog(){
		if(moviePath!=null && BannersShowed!=null){
			Intent returnIntent = new Intent();
			Bundle b=new Bundle();
			b.putStringArrayList("banners", BannersShowed);
			b.putString("movie",moviePath);
			returnIntent.putExtra("data", b);
			setResult(RESULT_OK,returnIntent);
		}
	}
	
	/**
	 * Release Media Player
	 */
	private void releaseMP() {
		videoView.destroyDrawingCache();
	}
	
	/**
	 * Hide the banner after the time out.
	 */
	private final Runnable delayBanner = new Runnable() {
		@Override
	      public void run() {
			  handlerBanner.removeCallbacks(delayBanner);
			  BannerView.setVisibility(View.INVISIBLE);
	      }
	};
	
	/**
	 * Display a banner every time.
	 */
	public void updateBanner() {
		//validationHandler.postDelayed(mUpdateTimeTask,DeyalShowBanner);
		timerBanners.scheduleAtFixedRate(new RemindTask(), 10000, tiempo_entre_banners);
    }
	
	class RemindTask extends TimerTask {

		    @Override
		    public void run() {
		    	
		    	PlayerDRMActivity.this.runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
						BannerView.setVisibility(View.VISIBLE);
						BannerView.setImageResource(R.drawable.transparent);
						BannerView.setImageBitmap(null);
						BannerView.destroyDrawingCache();
						handlerBanner = new Handler();
						handlerBanner.postDelayed(delayBanner, tiempo_visualizando_banner);
						if(count>=bannersImg.length)
							count=0;
						if(bannersImg[count].exists() && bannersImg[count].length()>0){
							w.loadBitmap(bannersImg[count].getPath(),BannerView, getApplicationContext(),1280, 120);
							BannersShowed.add(bannersImg[count].getName());
						}
						count++;
		            }
		    });

		    }
	}
	
	@Override
	public void onBackPressed() {
		if(videoView!=null){
			if(videoView.getCurrentPosition()>=1800000)
				sendLog();
		}
		PlayerDRMActivity.this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try{
			MainActivity.udpBroadcast.setListener(null, null);
			MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
		} catch(Exception ex){
			Log.e(TAG, "onResume: ", ex);
		}
		hideNavigationBar(PlayerDRMActivity.this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		hideNavigationBar(PlayerDRMActivity.this);
	}
}
