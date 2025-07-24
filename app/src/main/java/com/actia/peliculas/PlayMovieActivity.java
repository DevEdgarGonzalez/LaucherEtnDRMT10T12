package com.actia.peliculas;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.BaseActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.UtilitiesOpenExternalApps;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_media_player.UtilitiesGeneralMediaPlayer;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utils_language.UtilsLanguage;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
//import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.RelativeLayout;
import android.widget.Toast;
//import android.widget.MediaController.MediaPlayerControl;

/**
 * Reproduce the movie file.
 * Show banner dependent of the duration of the movie and the number of the banners in external sdcard.
 */
public class PlayMovieActivity extends BaseActivity implements SurfaceHolder.Callback, OnErrorListener, OnCompletionListener, OnPreparedListener, VideoControllerView.MediaPlayerControl,
        VideoControllerView.TextViewsControls {

    private SurfaceView mPreview;
    private SurfaceHolder holder;
    //	private MediaController mediaController;
    private VideoControllerView mediaController;
    private boolean isSurfaceAvailable = false;
    public MediaPlayer mMediaPlayer = null;
    private ProgressDialog dialogMovie = null;
    private String pathMovie = null;
    private final Handler handlerMediaControl = new Handler();
    private ConfigMasterMGC configSingleton;
    //	private Handler validationHandler = new Handler();
    private ImageView BannerView;
    protected Handler handlerBanner = null;
    private File[] bannersImg = null;
    int count = 0;
    private ImageWorker w = null;
    private final int tiempo_visualizando_banner = 8000;
    private int tiempo_entre_banners = 900000; //15 min
    private File dirBanners;
    private final int timeToSaveLogMovies = 900000; //900,000 milis = 15 min
    boolean isPause = false;
    private BroadcastReceiver receiver = null;
    private boolean isGetBanners = false;
    private ArrayList<String> BannersShowed = null;
    private File movie = null;
    Timer timerBanners = null;
    private final String TAG = "PlayMovieActivity";
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);

        addListenersSensors_iSeat(listenersPlayers);
        UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(this);
        Bundle extras = getIntent().getExtras();
        BannerView = findViewById(R.id.Banner);
        if (extras != null) {

            BannersShowed = new ArrayList<>();
            pathMovie = extras.getString("path");
            if (pathMovie != null) {
                movie = new File(pathMovie);
            }
            // and get whatever type user account id is
            mPreview = findViewById(R.id.surfaceMovie);
            holder = mPreview.getHolder();
            holder.addCallback(this);
            mediaController = new VideoControllerView(this, this);
        }
        configSingleton = ConfigMasterMGC.getConfigSingleton();
        dirBanners = new File(configSingleton.getPathBanners());

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(PlayMovieActivity.this);
                }
            }
        });
    }


    /**
     * Prepare the media player.
     */

    public class RunnableInitMP implements Runnable {
        private final String path;

        /**
         * Prepare the media player.
         *
         * @param path video path
         */
        RunnableInitMP(String path) {
            this.path = path;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            File f = new File(path);
            if (f.exists() && f.isFile() && f.canRead() && f.length() > 0) {
                try {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.setDisplay(holder);
                    mMediaPlayer.setOnErrorListener(PlayMovieActivity.this);
                    mMediaPlayer.setOnPreparedListener(PlayMovieActivity.this);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnCompletionListener(PlayMovieActivity.this);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                    PlayMovieActivity.this.finish();
                }
            }
        }
    }

    /**
     * Start the video and set the time to show the banners
     */
    private void startVideoPlayback() {
        dismissAllDialog();
        //setScreenVideo();
        if (isSurfaceAvailable) {
            mMediaPlayer.start();
        }
        getBanners();
        InitBroadcast();
    }

    public void getBanners() {
        isGetBanners = true;

        if (dirBanners.exists() && dirBanners.listFiles(new FileExtensionFilterImages()).length > 0) {
            resetHandlerBanners();
            timerBanners = new Timer();
            bannersImg = null;
            bannersImg = dirBanners.listFiles(UtilsLanguage.getFileExtensionImageByLanguage(dirBanners));
            List<File> bannerList = Arrays.asList(bannersImg);
            Collections.shuffle(bannerList);
            bannersImg = bannerList.toArray(new File[bannerList.size()]);
            w = new ImageWorker();
            @SuppressWarnings("UnusedAssignment") int x = tiempo_entre_banners = mMediaPlayer.getDuration();
            tiempo_entre_banners = tiempo_entre_banners - 50000;//10 seg del primer banner y 40 seg de tolerancia
            if (tiempo_entre_banners<20000) return;
            x = tiempo_entre_banners / (bannersImg.length);
            //int x=60000;
            if (x > tiempo_visualizando_banner)
                tiempo_entre_banners = x;
            Log.i(TAG, "Tiempo entre banners: " + x);
            updateBanner();
            //Toast.makeText(getApplicationContext(), "time: "+DeyalShowBanner/60000, Toast.LENGTH_LONG).show();
        }
        isGetBanners = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mediaController != null)
            mediaController.show();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int currentVolume = mediaController.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mediaController.mVolumeSeekbar.setProgress(currentVolume);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mediaController.mVolumeSeekbar.setProgress(currentVolume);
            return false;
        }
        if (mediaController != null)
            mediaController.show();
        if (keyCode == KeyEvent.KEYCODE_ENTER && mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                isPause = true;

            } else {
                mMediaPlayer.start();
                isPause = false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PlayMovieActivity", "OnDestroy");
        if (receiver != null)
            unregisterReceiver(receiver);
        resetHandlerBanners();
        releaseMP();
        android.os.Debug.stopMethodTracing();
    }

    public void resetHandlerBanners() {
        //validationHandler.removeCallbacks(mUpdateTimeTask);
        if (timerBanners != null) {
            Log.i(TAG, "cancel timer banners");
            timerBanners.cancel();
            timerBanners.purge();
            timerBanners = null;
        }
        if (handlerBanner != null)
            handlerBanner.removeCallbacks(delayBanner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("PlayMovieActivity", "OnStop");
        if (mMediaPlayer != null) {
            Log.i("PlayMovieActivity", "pause video");
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("PlayMovieActivity", "OnStart");
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            Log.i("PlayMovieActivity", "start video");
            isPause = false;
        }
    }


    public void sendLog() {
        if (movie != null && BannersShowed != null) {
            Intent returnIntent = new Intent();
            Bundle b = new Bundle();
            b.putStringArrayList("banners", BannersShowed);
            b.putString("movie", movie.getName());
            returnIntent.putExtra("data", b);
            setResult(RESULT_OK, returnIntent);
        }
    }

    @Override
    public boolean canPause() {
        return true;
    }

//	@Override
//	public boolean canSeekBackward() {
//		return false;
//	}
//
//	@Override
//	public boolean canSeekForward() {
//		return false;
//	}
//
//    @Override
//    public boolean isFullScreen() {
//        return false;
//    }
//
//    @Override
//    public void toggleFullScreen() {
//
//    }

//    @Override
//	public int getAudioSessionId() {
//		return 0;
//	}

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getDuration();
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null)
            mMediaPlayer.pause();
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(position);
    }

    @Override
    public void start() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer = mp;
            startVideoPlayback();
            mediaController.setMediaPlayer(PlayMovieActivity.this);
            mediaController.setAnchorView((ViewGroup) findViewById(R.id.videoSurfaceContainer));
            handlerMediaControl.post(new Runnable() {

                public void run() {
                    mediaController.setEnabled(true);
                    mediaController.show();
                }
            });
        }
        dismissAllDialog();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        dismissAllDialog();
        mMediaPlayer = mp;
        /*Intent quiz = new Intent(Intent.ACTION_VIEW);
        quiz.addCategory(Intent.CATEGORY_LAUNCHER);
        quiz.setClassName(configSingleton.getApkQuizPackage(), configSingleton.getApkQuizClass());
        new AsyncIntents(PlayMovieActivity.this, true).execute(quiz);*/
        sendLog();
        PlayMovieActivity.this.finish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer = mp;
        dismissAllDialog();
        releaseMP();
        alert(getString(R.string.error_current_file) + " " + what + " /" + extra);
        PlayMovieActivity.this.finish();
        return false;
    }

    /**
     * Show a Toast
     *
     * @param text the text to display
     */
    public void alert(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Release Media Player
     */
    private void releaseMP() {
        if (mPreview != null) {
            Log.i("PlayMovieActivity", "Destroy mPreview");
            mPreview.destroyDrawingCache();
            mPreview.getHolder().removeCallback(this);
            mPreview = null;
        }

        if (mMediaPlayer != null) {
            Log.i("PlayMovieActivity", "Release MP");
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Dismiss All dialog opened.
     */
    public void dismissAllDialog() {
        if (dialogMovie != null && dialogMovie.isShowing())
            dialogMovie.dismiss();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        isSurfaceAvailable = true;
        dialogMovie = new ProgressDialog(this);
        dialogMovie.setMessage(getString(R.string.load_wait_please));
        dialogMovie.show();
        if (pathMovie != null)
            new Thread(new RunnableInitMP(pathMovie)).start();
        else
            Toast.makeText(getApplicationContext(), getString(R.string.null_path), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        //	releaseMP();
        isSurfaceAvailable = false;
        this.finish();
    }

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

            PlayMovieActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//						FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreview.getLayoutParams();
//						params.addRule(RelativeLayout.ALIGN_TOP);
//						mPreview.setLayoutParams(params);
                    BannerView.setVisibility(View.VISIBLE);
                    BannerView.setImageResource(R.drawable.transparent);
                    BannerView.setImageBitmap(null);
                    BannerView.destroyDrawingCache();
                    handlerBanner = new Handler();
                    handlerBanner.postDelayed(delayBanner, tiempo_visualizando_banner);
                    if (count >= bannersImg.length)
                        count = 0;
                    if (bannersImg[count].exists() && bannersImg[count].length() > 0) {
                        w.loadBitmap(bannersImg[count].getPath(), BannerView, getApplicationContext(), 1280, 120);
                        BannersShowed.add(bannersImg[count].getName());
                    }
                    count++;
                }
            });

        }
    }

    /**
     * Runnable to show the banner
     */
	/*private Runnable mUpdateTimeTask = new Runnable() {
		@Override
	      public void run() {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPreview.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_TOP);
				mPreview.setLayoutParams(params);
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
				
				updateBanner();
	      }
	};*/

    /**
     * Hide the banner after the time out.
     */
    private final Runnable delayBanner = new Runnable() {
        @Override
        public void run() {
            handlerBanner.removeCallbacks(delayBanner);
            //setScreenVideo();
            BannerView.setVisibility(View.INVISIBLE);
        }
    };


    /**
     * Set the screen height and width.
     */
    public void setScreenVideo() {

        int width = mPreview.getWidth();
        int height = mPreview.getHeight();
        float boxWidth = width;
        float boxHeight = height;

        float videoWidth = mMediaPlayer.getVideoWidth();
        float videoHeight = mMediaPlayer.getVideoHeight();

        float wr = boxWidth / videoWidth;
        float hr = boxHeight / videoHeight;
        float ar = videoWidth / videoHeight;

        if (wr > hr)
            width = (int) (boxHeight * ar);
        else
            height = (int) (boxWidth / ar);

        Log.i("PlayMovieActivity", String.format("Scaled to %dx%d", width, height));
//		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreview.getLayoutParams();
//		params.addRule(RelativeLayout.CENTER_IN_PARENT);
//		mPreview.setLayoutParams(params);
        holder.setFixedSize(width, height);

    }

    public void InitBroadcast() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.actia.actiaservice");

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("msg");
                Toast.makeText(getApplicationContext(), getString(R.string.update_banners), Toast.LENGTH_SHORT).show();
                if (msg.equals("updatebanners")) {
                    if (!isGetBanners)
                        getBanners();
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.update_banners_wait_please), Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public void onBackPressed() {
        backMovie();
    }

    @Override
    public void backMovie() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.getCurrentPosition() >= timeToSaveLogMovies)
                sendLog();
        }
        PlayMovieActivity.this.finish();
    }

    @Override
    protected void onPause() {
        pause();
        super.onPause();
    }

    ListenersPlayers listenersPlayers = new ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            manageVideoPlayerWithSensors(isPassengerSitting, isSafetyBeltPlaced, mMediaPlayer);
        }
    };

    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(PlayMovieActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(PlayMovieActivity.this);
    }

}
