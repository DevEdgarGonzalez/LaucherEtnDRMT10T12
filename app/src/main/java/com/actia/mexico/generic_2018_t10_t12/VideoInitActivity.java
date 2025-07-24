package com.actia.mexico.generic_2018_t10_t12;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.menu_maintance.MenuMaintanceDialog;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.LocaleHelper;
import com.actia.utilities.UtilitiesComunicationServiceActia;
import com.actia.utilities.UtilitiesOpenExternalApps;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.utilities.utilities_dialog_brightness.DialogBrightnessFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;

/**
 * Start the promotional videos.
 */

public class VideoInitActivity extends BaseActivity implements SurfaceHolder.Callback, OnErrorListener, OnCompletionListener, OnPreparedListener, MediaPlayerControl {

    private final String TAG_ACTIVITY = "VideoInitActivity";
    private final String TAG01 = "00VideoPlayerNoPause";
    private final String TAG02 = "01VideoPlayerNoPause";
    private final String TAG = "02VideoPlayerNoPause";
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private MediaController mediaController;
    private boolean isSurfaceAvailable = false;
    public MediaPlayer mMediaPlayer = null;
    private ProgressDialog dialogMovie = null;
    private long delaySkip = 0;
    private final Handler validationHandler = new Handler();

    private View decorView;
    private ImageView mBrightImageView;
    private ImageView mVolumeUpImageView;
    private ImageView mVolumeDownImageView;
    private ImageView mLogoHeader;
    private SeekBar mVolumeSeekbar;
    private AudioManager mAudioManager;
    private FrameLayout mShadowFrameLayout;
    private Button mSkipButton;
    private Handler handlerMediaControl = new Handler();
    private final Runnable runnableMediaControl = new Runnable() {
        @Override
        public void run() {
            mShadowFrameLayout.setVisibility(View.GONE);
        }
    };

    boolean isEnglish = false;

    //	private Handler handlerMediaControl = new Handler();
    private File[] videos = null;
    private int count = 0;
    private String keyC2 = "";
    boolean isPause = false;
    private boolean isSkipVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_init);

        UtilitiesComunicationServiceActia.installApkBroadcast(this);

        addListenersSensors_iSeat(listenersPlayers);

        mPreview = findViewById(R.id.surfaceMovieInit);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        mediaController = new MediaController(this);
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        mBrightImageView = findViewById(R.id.bright_image_view);
        mVolumeUpImageView = findViewById(R.id.volume_image_view);
        mVolumeDownImageView = findViewById(R.id.volume_down_image_view);
        mLogoHeader = findViewById(R.id.logoHeader);
        mVolumeSeekbar = findViewById(R.id.volume_seek_bar);
        mShadowFrameLayout = findViewById(R.id.shadow_frame_layout);
        mSkipButton = findViewById(R.id.skip_button);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        File v = new File(configSingleton.getPathAdvertising());
        videos = v.listFiles(new FileExtensionFilterVideo());

        if (ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES && videos != null) {
            Arrays.sort(videos);
        }

        mShadowFrameLayout.setVisibility(View.VISIBLE);
        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    hideNavigationBar(VideoInitActivity.this);
                }
            }
        });

        handlerMediaControl.postDelayed(runnableMediaControl, 10000);

        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mShadowFrameLayout.setVisibility(View.VISIBLE);
                handlerMediaControl.removeCallbacks(runnableMediaControl);
                handlerMediaControl = new Handler();
                handlerMediaControl.postDelayed(runnableMediaControl, 5000);
            }
        });


        mBrightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrightnessDialog();
            }
        });

        try {

            final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            final int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mVolumeSeekbar.setMax(streamMaxVolume);
            mVolumeSeekbar.setProgress(currentVolume);

            mVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    if (progress > mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                        mVolumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

            });

            mVolumeUpImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    mVolumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                }
            });

            mVolumeDownImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    mVolumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                }
            });
        } catch (Exception exception) {
            Log.e(TAG, "Something is wrong with the volume: ", exception);
        }

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCompletion(mMediaPlayer);
            }
        });

        mLogoHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startHome();
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 111) {
            return super.onKeyDown(keyCode, event);
        }

        keyC2 = keyC2 + keyCode;
        String keyC1 = ConstantsApp.PASSWORD_TACTIL;
        Log.i(TAG, "onKeyDown.keyC1: " + keyC1);
        Log.i(TAG, "onKeyDown.keyC2: " + keyC2);
        if (keyC2.length() > 6) {
            keyC2 = "";
            keyC2 = keyC2 + keyCode;
            Log.i(TAG01, "keyC2 clean");
            Toast.makeText(getApplicationContext(), getText(R.string.clear), Toast.LENGTH_SHORT).show();
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.i(TAG01, "onKeyDown.KeyEvent: KEYCODE_MENU");
            if (Integer.parseInt(keyC2) == Integer.parseInt(keyC1)) {
                Log.i(TAG02, "onKeyDown.KeyEvent: KEYCODE_MENU");
                startHome();
            }
            return true;
        }

        //mVideoView.pause();
//finish();

//		 if ( keyCode ==  KeyEvent.KEYCODE_BACK)
//		 {
//			 //mVideoView.pause();
//			 //finish();
//			 return true;
//		 }
//		 return super.onKeyDown(keyCode, event);

        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
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
                    mMediaPlayer.setOnErrorListener(VideoInitActivity.this);
                    mMediaPlayer.setOnPreparedListener(VideoInitActivity.this);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnCompletionListener(VideoInitActivity.this);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
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
        validationHandler.removeCallbacks(mUpdateTimeTask);
        Log.i(TAG_ACTIVITY, "onDestroy");
        releaseMP();
        android.os.Debug.stopMethodTracing();
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        Log.i(TAG_ACTIVITY, "onStop");
        if (mMediaPlayer != null) {
            Log.i(TAG_ACTIVITY, "pause video");
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG_ACTIVITY, "OnStart");
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            Log.i(TAG_ACTIVITY, "start video");
            isPause = false;
        }
    }

    @Override
    public void onBackPressed() {
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
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
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
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer = mp;
            startVideoPlayback();
            mediaController.setMediaPlayer(VideoInitActivity.this);
            mediaController.setAnchorView(findViewById(R.id.surfaceMovieInit));
            mediaController.setEnabled(true);
        }
        dismissAllDialog();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        dismissAllDialog();
        mMediaPlayer = mp;
        releaseMP();
        if (count < videos.length) {
            if(videos[count].getPath().contains("skip")){
                delaySkip = Integer.valueOf(videos[count].getName().split("_")[2].replace(".mp4", "").trim());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSkipVisible = true;
                        mSkipButton.setVisibility(View.VISIBLE);
                        updateProgress();
                    }
                }, (delaySkip*1000));
            }
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

        Intent homeIntent = new Intent(VideoInitActivity.this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        VideoInitActivity.this.finish();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer = mp;
        dismissAllDialog();
        releaseMP();
        alert(getString(R.string.error_current_file) + what + " /" + extra);
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
        if(isSkipVisible)
            mSkipButton.setVisibility(View.GONE);
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
        if(videos[count].getPath().contains("skip")){
            delaySkip = Integer.valueOf(videos[count].getName().split("_")[2].replace(".mp4", "").trim());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isSkipVisible = true;
                    mSkipButton.setVisibility(View.VISIBLE);
                    updateProgress();
                }
            }, (delaySkip*1000));
        }
        new Thread(new RunnableInitMP(videos[count].getPath())).start();
        count++;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e(TAG_ACTIVITY, "SurfaceHolder change");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG_ACTIVITY, "SurfaceHolder destroyed");
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

        Log.i(TAG_ACTIVITY, String.format("Scaled to %dx%d", width, height));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreview.getLayoutParams();
//		params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPreview.setLayoutParams(params);
        holder.setFixedSize(width, height);

    }

    BaseActivity.ListenersPlayers listenersPlayers = new BaseActivity.ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            manageVideoPlayerWithSensors(isPassengerSitting, isSafetyBeltPlaced, mMediaPlayer);
        }
    };

    protected void showBrightnessDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogBrightnessFragment dialog = DialogBrightnessFragment.newInstance();
        dialog.show(ft, "dialog");
    }

    protected void onResume() {
        super.onResume();
        try{
                MainActivity.udpBroadcast.setListener(null, null);
                MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(VideoInitActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(VideoInitActivity.this);
    }

    public void updateProgress() {
        validationHandler.postDelayed(mUpdateTimeTask, 3000);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };
}
