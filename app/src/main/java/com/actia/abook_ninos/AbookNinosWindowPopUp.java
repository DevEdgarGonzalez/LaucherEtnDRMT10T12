package com.actia.abook_ninos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.audiolibros.AudioBook;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_ui.UtilsTime;
import com.actia.utilities.utilities_media_player.UtilitiesGeneralMediaPlayer;
import com.actia.utilities.utilities_media_player.UtilsMediaPlayer;

import java.io.File;
import java.io.IOException;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * Created by Edgar Gonzalez on 27/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class AbookNinosWindowPopUp implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener {
    private View popUpView;
    private final Context context;
    private final Activity activityParent;
    private boolean isPlaying = false;

    private boolean isResume = false;
    private MediaPlayer mediaPlayer;
    private final Handler seekBarHandler = new Handler();
    private boolean flagError;

    private PopupWindow windowAbook;
    private ImageView imgvCoverAbookChild;
    private ImageButton btnPlayAbookchild;
    private ImageButton btnBackPlayAbookChild;
    private TextView txtvInitTimeAbookChild;
    private TextView txtvFinalTimeAbookChild;
    private TextView txtvTitleAbookChild;
    private JustifyTextView txtvDescAbookChild;
    private SeekBar seekbAbookChild;
    private final AudioBook audioBook;
    private ImageView mVolumeUpImageView, mVolumeDownImageView;
    private SeekBar mVolumeSeekbar;
    private AudioManager mAudioManager;
    private View decorView;


    public AbookNinosWindowPopUp(Activity activityParent, AudioBook audioBook) {
        this.activityParent = activityParent;
        this.context = activityParent.getApplicationContext();
        this.audioBook = audioBook;


    }

    public void showPopUp() {

        startComponentsUI();
        drawDurationAbook();
        drawElementsUI();
        addListeners();

        hideNavigationBar(activityParent);

        decorView = activityParent.getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(activityParent);
                }
            }
        });
        try {
            context.registerReceiver(receiverStopProcess,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void startComponentsUI() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popUpView = inflater.inflate(R.layout.popup_abook_child, null, false);
        windowAbook = new PopupWindow(popUpView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        windowAbook.setBackgroundDrawable(new BitmapDrawable());
        windowAbook.setOutsideTouchable(true);
        windowAbook.showAtLocation(activityParent.findViewById(R.id.llytParentAbookChild), Gravity.CENTER_VERTICAL, 0, -40);

        imgvCoverAbookChild = popUpView.findViewById(R.id.imgvCoverAbookChild);
        btnPlayAbookchild = popUpView.findViewById(R.id.btnPlayAbookchild);
        btnBackPlayAbookChild = popUpView.findViewById(R.id.btnBackPlayAbookChild);
        seekbAbookChild = popUpView.findViewById(R.id.seekbAbookChild);
        txtvInitTimeAbookChild = popUpView.findViewById(R.id.txtvInitTimeAbookChild);
        txtvFinalTimeAbookChild = popUpView.findViewById(R.id.txtvFinalTimeAbookChild);
        txtvTitleAbookChild = popUpView.findViewById(R.id.txtvTitleAbookChild);
        txtvDescAbookChild = popUpView.findViewById(R.id.txtvDescAbookChild);

        mVolumeUpImageView = popUpView.findViewById(R.id.volume_image_view);
        mVolumeDownImageView = popUpView.findViewById(R.id.volume_down_image_view);
        mVolumeSeekbar = popUpView.findViewById(R.id.volume_seek_bar);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


    }

    private void drawElementsUI() {

        if (audioBook.getPathImage() != null) {
            File filePathImgAbook = new File(audioBook.getPathImage());
            if (filePathImgAbook.exists() && filePathImgAbook.length() > 0) {
                ImageWorker imgWorker = new ImageWorker();
                imgWorker.loadBitmap(filePathImgAbook.getPath(), imgvCoverAbookChild, context, 500, 210);
            }
        }
        txtvTitleAbookChild.setText(audioBook.getName());
        txtvDescAbookChild.setText(UtilitiesFile.getTextFileMovie(audioBook.getPathText(), context.getString(R.string.error_not_avaliable)) + "\n\n");

    }

    private void addListeners() {
        btnPlayAbookchild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isPlaying) {
                    isPlaying = true;
                    btnPlayAbookchild.setImageDrawable(context.getResources().getDrawable(R.drawable.pausa_audiolibro));
                    if (!isResume) {
                        releaseMP();
                        seekbAbookChild.setEnabled(true);
                        new Thread(new RunnableInitMP(audioBook.getPath())).start();
                    } else {
                        mediaPlayer.start();
                    }
                } else {
                    isPlaying = false;
                    isResume = true;
                    mediaPlayer.pause();
                    btnPlayAbookchild.setImageDrawable(context.getResources().getDrawable(R.drawable.play_audiolibro));
                }
            }
        });
        btnBackPlayAbookChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowAbook.dismiss();
            }
        });

        seekbAbookChild.setOnSeekBarChangeListener(this);

        windowAbook.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(context);
                isPlaying = false;
                isResume = false;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                try {
                    context.unregisterReceiver(receiverStopProcess);
                } catch (Exception e) {
                    e.printStackTrace();
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
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();
                String auxTotal = "" + UtilsTime.milliSecondsToTimer(totalDuration);
                String auxCurrent = "" + UtilsTime.milliSecondsToTimer(currentDuration);
                txtvFinalTimeAbookChild.setText(auxTotal);
                txtvInitTimeAbookChild.setText(auxCurrent);
                int progress = UtilsTime.getProgressPercentage(currentDuration, totalDuration);
                seekbAbookChild.setProgress(progress);
                seekBarHandler.postDelayed(this, 100);
            } else {
                seekBarHandler.removeCallbacks(mUpdateTimeTask);
            }

        }
    };


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
                    mediaPlayer.setOnErrorListener(AbookNinosWindowPopUp.this);
                    mediaPlayer.setOnPreparedListener(AbookNinosWindowPopUp.this);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(AbookNinosWindowPopUp.this);
                    mediaPlayer.setWakeMode(context, PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                    activityParent.finish();
                }
            }
        }
    }

    private void alert(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer = mp;
        flagError = true;
        releaseMP();
        alert(context.getString(R.string.error_in_this_file) + what + "-" + extra);
        activityParent.finish();
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
        btnPlayAbookchild.setImageDrawable(context.getResources().getDrawable(R.drawable.play_audiolibro));
        isPlaying = false;
        isResume = false;
        releaseMP();
        seekbAbookChild.setEnabled(false);
        seekbAbookChild.setProgress(0);
        txtvInitTimeAbookChild.setText(context.getString(R.string.time_default));
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
                btnPlayAbookchild.setImageDrawable(context.getResources().getDrawable(R.drawable.pausa_audiolibro));
                mediaPlayer.start();
                mediaPlayer.seekTo(currentPosition);
                isPlaying = true;
                updateProgressBar();
            }
        }
    }

    private void drawDurationAbook() {
        File f = new File(audioBook.getPath());
        if (f.exists() && f.isFile() && f.canRead() && f.length() > 0) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioBook.getPath());
                mediaPlayer.prepare();

                long totalDuration = mediaPlayer.getDuration();

                String auxTotal = "" + UtilsTime.milliSecondsToTimer(totalDuration);
                txtvFinalTimeAbookChild.setText(auxTotal);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    BaseActivity.ListenersPlayers listenersPlayers = new BaseActivity.ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            if (mediaPlayer == null) {
                return;
            }
            if (isPassengerSitting && isSafetyBeltPlaced) {
                if (!mediaPlayer.isPlaying()) {
                    isPlaying = true;
                    mediaPlayer.start();
                    btnPlayAbookchild.setImageDrawable(context.getResources().getDrawable(R.drawable.pausa_audiolibro));
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    isPlaying = false;
                    isResume = true;
                    mediaPlayer.pause();
                    btnPlayAbookchild.setImageDrawable(context.getResources().getDrawable(R.drawable.play_audiolibro));
                }
            }

        }
    };
    IntentFilter filter = new IntentFilter(ConstantsApp.ARG_NEW_INSTANCE_APP);
    BroadcastReceiver receiverStopProcess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mediaPlayer!=null&& mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }
    };

    protected void onResume() {
        hideNavigationBar(activityParent);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        hideNavigationBar(activityParent);
    }


}
