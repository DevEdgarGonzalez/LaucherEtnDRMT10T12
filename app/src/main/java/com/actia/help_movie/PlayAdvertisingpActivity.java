package com.actia.help_movie;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actia.audiolibros.PlayAbookActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.home_categories.MainActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.PlayMusicActivity;
import com.actia.music_ninos.MusicNinosActivity;
import com.actia.peliculas.VideoControllerView;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.File;
import java.io.IOException;

public class PlayAdvertisingpActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, VideoControllerView.TextViewsControls {
    private final String TAG = this.getClass().getSimpleName();
    public static final String ARG_PATH_ROOT_VIDEO_ADVERTISING= "pathRootAdvertising";

    private SurfaceView mPreview;
    private SurfaceHolder holder;
    //    private MediaController mediaController;
    private VideoControllerView mediaController;
    private boolean isSurfaceAvailable = false;
    public MediaPlayer mMediaPlayer = null;
    private ProgressDialog dialogMovie = null;
    private final Handler handlerMediaControl = new Handler();


    //	private Handler handlerMediaControl = new Handler();
    private File[] videos = null;
    private int count = 0;
    boolean isPause = false;

    private String pathRootVideoAdvertising;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_init);

//        startWithoutBottomBar(this);
        addListenersSensors_iSeat(listenersPlayers);

        Bundle extras = getIntent().getExtras();

        if (extras!=null && extras.containsKey(ARG_PATH_ROOT_VIDEO_ADVERTISING)){
            pathRootVideoAdvertising = extras.getString(ARG_PATH_ROOT_VIDEO_ADVERTISING);
        }else{
            home();
            return;
        }

        if (PlayAbookActivity.mediaPlayer != null) {
            PlayAbookActivity.mediaPlayer.stop();
        }


        if(PlayMusicActivity.mServ != null){
            if(PlayMusicActivity.mServ.isMediaPlayerPlaying()){
                PlayMusicActivity.mServ.pauseMusic();
                PlayMusicActivity.mServ.releasemp();
            }
        }

        if(MusicNinosActivity.mp != null){
                MusicNinosActivity.mp.stop();
        }

        mPreview = findViewById(R.id.surfaceMovieInit);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        mediaController = new VideoControllerView(this, this);

        File filePathRoot = new File(pathRootVideoAdvertising);
        videos = filePathRoot.listFiles(UtilsLanguage.getFileExtensionMovieByLanguage(filePathRoot));

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(PlayAdvertisingpActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mediaController != null)
            mediaController.show();
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

    /**
     * Set mediaplayer
     */
    public class RunnableInitMP implements Runnable {
        private final String path;

        /**
         * Set mediaplayer
         *
         * @param path video path
         */
        public RunnableInitMP(String path) {
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
                    mMediaPlayer.setOnErrorListener(PlayAdvertisingpActivity.this);
                    mMediaPlayer.setOnPreparedListener(PlayAdvertisingpActivity.this);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnCompletionListener(PlayAdvertisingpActivity.this);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    startHome();
                } catch (SecurityException e) {
                    e.printStackTrace();
                    startHome();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    startHome();
                } catch (IOException e) {
                    e.printStackTrace();
                    startHome();
                }
            } else startHome();
        }
    }


    /**
     * Start video
     */
    private void startVideoPlayback() {
        dismissAllDialog();
        setScreenVideo();

        if (isSurfaceAvailable) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("VideoInitctivity", "onDestroy");
        releaseMP();
        android.os.Debug.stopMethodTracing();
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        Log.i("VideoInitctivity", "onStop");
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


    @Override
    public void start() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null)
            mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getDuration();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null)
            return mMediaPlayer.isPlaying();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer = mp;
            startVideoPlayback();
            mediaController.setMediaPlayer(PlayAdvertisingpActivity.this);
            mediaController.setAnchorView((ViewGroup) findViewById(R.id.videoSurfaceVidinitContainer));
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
        releaseMP();
        if (count < videos.length) {
            new Thread(new RunnableInitMP(videos[count].getPath())).start();
            count++;
        } else {
            startHome();
        }
    }

    /**
     * Launche the Home
     */
    public void startHome() {
        Intent homeIntent = new Intent(PlayAdvertisingpActivity.this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        PlayAdvertisingpActivity.this.finish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer = mp;
        dismissAllDialog();
        releaseMP();
        alert(getString(R.string.error_current_file)+ what + " /" + extra);
        startHome();
        return false;
    }

    /**
     * Show a Toast.
     *
     * @param text the text to display
     */
    public void alert(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Release the mediaplayer
     */
    private void releaseMP() {
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.getHolder().removeCallback(this);
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Dismiss all dialog opened.
     */
    public void dismissAllDialog() {
        if (dialogMovie != null && dialogMovie.isShowing())
            dialogMovie.dismiss();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceAvailable = true;
        dialogMovie = new ProgressDialog(this);
        dialogMovie.setMessage(getString(R.string.load_wait_please));
        dialogMovie.show();
        new Thread(new RunnableInitMP(videos[count].getPath())).start();
        count++;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e("VideoInitActiviy", "SurfaceHolder change");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("VideoInitActiviy", "SurfaceHolder destroyed");
        releaseMP();
        isSurfaceAvailable = false;
        this.finish();
    }

    /**
     * Set the screen before of start the video.
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
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreview.getLayoutParams();
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPreview.setLayoutParams(params);
        holder.setFixedSize(width, height);

    }


    @Override
    public void backMovie() {
//        if (mMediaPlayer != null) {
//            if (mMediaPlayer.getCurrentPosition() >= 1800000){
//
//            }
//        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        PlayAdvertisingpActivity.this.finish();
//        home();
        startHome();
    }

    BaseActivity.ListenersPlayers listenersPlayers = new ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            manageVideoPlayerWithSensors(isPassengerSitting,isSafetyBeltPlaced, mMediaPlayer);
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
        hideNavigationBar(PlayAdvertisingpActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(PlayAdvertisingpActivity.this);
    }
}
