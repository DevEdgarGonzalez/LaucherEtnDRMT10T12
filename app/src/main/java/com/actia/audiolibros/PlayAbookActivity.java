package com.actia.audiolibros;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.BaseActivity;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilsFonts;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_ui.UtilsTime;
import com.actia.utilities.utilities_media_player.UtilsMediaPlayer;

import java.io.File;
import java.io.IOException;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class PlayAbookActivity extends BaseActivity implements
        BaseActivity.CategoryNavigationListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, BaseActivity.PressBackHeader {

    private final String TAG = this.getClass().getSimpleName();
    public static String ARG_CURRENT_ABOOK = "abookselected";

    private ImageView imgvCoverPlayAbook, imgvPlayPlayAbook;
    private TextView lblTimeInitialPlayAbook, lblTimeFinalPlayAbook, tvNamePlayAbook;
    private JustifyTextView txtDescPlayAbook;
    private SeekBar seekbProgressPlayAbook;


    private AudioBook currentAbook = null;
    private int positionCategory = ConstantsApp.CATEGORY_NO_DETECTED;
    private boolean isSubMenu = false;
    private String titleCategory = "";
    private String pathImageCategory = "";


    private boolean isPlaying = false;
    private boolean isResume = false;
    public static MediaPlayer mediaPlayer;
    private final Handler seekBarHandler = new Handler();
    private boolean flagError;

    private ImageView mVolumeUpImageView, mVolumeDownImageView;
    private SeekBar mVolumeSeekbar;
    private AudioManager mAudioManager;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_abook);

        getInfoExtras();

        startTopBar(this, titleCategory, mutableDrawableFromPath(pathImageCategory), this);


        addListenersSensors_iSeat(listenersPlayers);
        startElementsUI();
        drawElements();
    }

    private void getInfoExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentAbook = (AudioBook) getIntent().getSerializableExtra(ARG_CURRENT_ABOOK);
            positionCategory = extras.getInt(ConstantsApp.ARG_POSITION_CATEGORY);

            isSubMenu = extras.getBoolean(ConstantsApp.ARG_IS_SUBMENU, false);
            pathImageCategory = extras.getString(ConstantsApp.ARG_IMG_SUBGENRE);
            titleCategory = extras.getString(ConstantsApp.ARG_TITLE_SUBGENRE);
        }
    }

    private void startElementsUI() {
        txtDescPlayAbook = findViewById(R.id.txtDescPlayAbook);
        imgvCoverPlayAbook = findViewById(R.id.imgvCoverPlayAbook);
        imgvPlayPlayAbook = findViewById(R.id.imgvPlayPlayAbook);
        lblTimeInitialPlayAbook = findViewById(R.id.lblTimeInitialPlayAbook);
        lblTimeFinalPlayAbook = findViewById(R.id.lblTimeFinalPlayAbook);
        seekbProgressPlayAbook = findViewById(R.id.seekbProgressPlayAbook);
        tvNamePlayAbook = findViewById(R.id.tvNamePlayAbook);

        mVolumeUpImageView = findViewById(R.id.volume_image_view);
        mVolumeDownImageView = findViewById(R.id.volume_down_image_view);
        mVolumeSeekbar = findViewById(R.id.volume_seek_bar);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void drawElements() {
        //drawDurationAbook(currentAbook);

        if (currentAbook.getPathImage() != null) {
            File imgFile = new File(currentAbook.getPathImage());
            if (imgFile.exists() && imgFile.length() > 0) {
                ImageWorker imageWorker = new ImageWorker();
                imageWorker.loadBitmap(imgFile.getPath(), imgvCoverPlayAbook, context, 462, 616);
            }
        }
//        lblTitlePopUpAbook.setText(currentAbook.getName());


        txtDescPlayAbook.setText(UtilitiesFile.getTextFileMovie(currentAbook.getPathText(), getString(R.string.error_not_avaliable)) + "\n\n\n\n\n\n\n\n");
        //txtDescPlayAbook.setTypeface(UtilsFonts.getTypefaceTextNormal(this));
        //tvNamePlayAbook.setText(currentAbook.getName());

        imgvPlayPlayAbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    isPlaying = true;
                    imgvPlayPlayAbook.setImageResource(R.drawable.pausa_audiolibro);
                    if (!isResume) {
                        releaseMP();
                        seekbProgressPlayAbook.setEnabled(true);
                        new Thread(new RunnableInitMP(currentAbook.getPath())).start();  //TODO

                    } else {
                        mediaPlayer.start();
                    }
                } else {
                    isPlaying = false;
                    isResume = true;
                    mediaPlayer.pause();
                    imgvPlayPlayAbook.setImageResource(R.drawable.play);
                }
            }
        });

        seekbProgressPlayAbook.setOnSeekBarChangeListener(PlayAbookActivity.this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    hideNavigationBar(PlayAbookActivity.this);
                }
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
            Log.e("PlayBookActivity", "Something is wrong with the volume: ", exception);
        }
    }

    @Override
    public void previousCategory() {
        goCategorySelectedInBottomBar(this, positionCategory - 1);
        this.finish();
    }

    @Override
    public void nextCategory() {
        goCategorySelectedInBottomBar(this, positionCategory + 1);
        this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /**
     * Show a Toast
     *
     * @param text the text display.
     */
    private void alert(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer = mp;
        flagError = true;
        releaseMP();
        alert(getString(R.string.error_not_avaliable) + what + "-" + extra);
        this.finish();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
        mediaPlayer.start();
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer = mp;
        seekBarHandler.removeCallbacks(mUpdateTimeTask);
        imgvPlayPlayAbook.setImageResource(R.drawable.play);
        isPlaying = false;
        isResume = false;
        releaseMP();
        seekbProgressPlayAbook.setEnabled(false);
        seekbProgressPlayAbook.setProgress(0);
        lblTimeInitialPlayAbook.setText(getString(R.string.time_default));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekBarHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer != null) {
            seekBarHandler.removeCallbacks(mUpdateTimeTask);
            if (!flagError) {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = UtilsMediaPlayer.progressToTimer(seekBar.getProgress(), totalDuration);
                imgvPlayPlayAbook.setBackgroundResource(R.drawable.pausa_audiolibro);
                mediaPlayer.start();
                mediaPlayer.seekTo(currentPosition);
                isPlaying = true;
                updateProgressBar();
            }
        }
    }


    public class RunnableInitMP implements Runnable {
        private final String path;

        RunnableInitMP(String path) {
            this.path = path;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            File f = new File(path);
            if (f.exists() && f.isFile() && f.canRead() && f.length() > 0) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.setOnErrorListener(PlayAbookActivity.this);
                    mediaPlayer.setOnPreparedListener(PlayAbookActivity.this);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(PlayAbookActivity.this);
                    mediaPlayer.setWakeMode(context, PowerManager.SCREEN_BRIGHT_WAKE_LOCK);

                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                    PlayAbookActivity.this.finish();
                }
            }
        }
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateProgressBar() {
        seekBarHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (!flagError) {
//                Log.i("playabook", "mUpdateTimeTask");
                if (mediaPlayer != null) {
                    long totalDuration = mediaPlayer.getDuration();
                    long currentDuration = mediaPlayer.getCurrentPosition();
                    String auxTotal = "" + UtilsTime.milliSecondsToTimer(totalDuration);
                    String auxCurrent = "" + UtilsTime.milliSecondsToTimer(currentDuration);
                    lblTimeFinalPlayAbook.setText(auxTotal);
                    lblTimeInitialPlayAbook.setText(auxCurrent);
                    int progress = UtilsTime.getProgressPercentage(currentDuration, totalDuration);
                    seekbProgressPlayAbook.setProgress(progress);
                    seekBarHandler.postDelayed(this, 100);
                }
            } else {
                seekBarHandler.removeCallbacks(mUpdateTimeTask);
            }

        }
    };

    private void drawDurationAbook(AudioBook unAudioBook) {
        File f = new File(unAudioBook.getPath());
        if (f.exists() && f.isFile() && f.canRead() && f.length() > 0) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(unAudioBook.getPath());
                mediaPlayer.prepare();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiverStopProcess);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Edd: se deja vacio el onPause para que al suspenderse el tactil siga reproducioendo el audiolibro
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//        }


    }

    BaseActivity.ListenersPlayers listenersPlayers = new ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            if (mediaPlayer == null) {
                return;
            }
            if (isPassengerSitting && isSafetyBeltPlaced) {
                if (!mediaPlayer.isPlaying()) {
                    isPlaying = true;
                    mediaPlayer.start();
                    imgvPlayPlayAbook.setBackgroundResource(R.drawable.pausa_audiolibro);
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    isPlaying = false;
                    isResume = true;
                    mediaPlayer.pause();
                    imgvPlayPlayAbook.setBackgroundResource(R.drawable.play_audiolibro);
                }
            }
        }
    };


    IntentFilter filter = new IntentFilter(ConstantsApp.ARG_NEW_INSTANCE_APP);
    BroadcastReceiver receiverStopProcess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        try {
            registerReceiver(receiverStopProcess, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
