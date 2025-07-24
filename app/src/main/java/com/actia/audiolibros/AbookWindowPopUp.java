package com.actia.audiolibros;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_media_player.UtilsMediaPlayer;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_ui.UtilsTime;

import java.io.File;
import java.io.IOException;

/**
 * Created by Edgar Gonzalez on 26/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class AbookWindowPopUp implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener {
    private final Activity activity;
    private final Context context;
    private TextView lblTitlePopUpAbook;
    private ImageView imgvCoverPagePopUpAbook;
    private TextView lblSynopsisPopUpAbook;
    private TextView lblTimeInitialPopUpAbook;
    private TextView lblTimeFinalPopUpAbook;
    private SeekBar seekbProgressPopUpAbook;
    private TextView btnPlayUpAbook;
    private boolean isPlaying = false;
    private boolean isResume = false;
    private View popPupView;
    private PopupWindow pw;
    private MediaPlayer mediaPlayer;
    private final Handler seekBarHandler = new Handler();
    private boolean flagError;
    private AudioBook audioBook;


    public AbookWindowPopUp(Activity activity) {
        this.activity = activity;
        context = activity.getApplicationContext();

    }



    public void showPopup(AudioBook unAudioBook, final LinearLayout linLayParent, final RecyclerView recyclerView) {
        audioBook = unAudioBook;



        linLayParent.setAlpha(0.3f);
        recyclerView.setAlpha(0.3f);


        LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popPupView = inflater2.inflate(R.layout.popup_abooks_view, null, false);
        pw = new PopupWindow(popPupView, 980, 540, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                    isPlaying = false;
                    isResume = false;
                    if (mediaPlayer!=null){
                        mediaPlayer.stop();
                    }
                linLayParent.setAlpha(1f);
                recyclerView.setAlpha(1f);


            }
        });
        pw.showAtLocation(activity.findViewById(R.id.llytParentAbook), Gravity.CENTER_VERTICAL, 0, -40);

        lblTitlePopUpAbook = popPupView.findViewById(R.id.lblTitlePopUpAbook);
        imgvCoverPagePopUpAbook = popPupView.findViewById(R.id.imgvCoverPagePopUpAbook);
        lblSynopsisPopUpAbook = popPupView.findViewById(R.id.lblSynopsisPopUpAbook);
        lblTimeInitialPopUpAbook = popPupView.findViewById(R.id.lblTimeInitialPopUpAbook);
        lblTimeFinalPopUpAbook = popPupView.findViewById(R.id.lblTimeFinalPopUpAbook);
        seekbProgressPopUpAbook = popPupView.findViewById(R.id.seekbProgressPopUpAbook);
        btnPlayUpAbook = popPupView.findViewById(R.id.btnPlayUpAbook);

        drawDurationAbook(unAudioBook);

        if (audioBook.getPathImage() != null) {
            File imgFile = new File(audioBook.getPathImage());
            if (imgFile.exists() && imgFile.length() > 0) {
                ImageWorker imageWorker = new ImageWorker();
                imageWorker.loadBitmap(imgFile.getPath(), imgvCoverPagePopUpAbook, context, 462, 616);
            }
        }
        lblTitlePopUpAbook.setText(audioBook.getName());


        lblSynopsisPopUpAbook.setText(UtilitiesFile.getTextFileMovie(audioBook.getPathText(), context.getString(R.string.error_not_avaliable)));

        btnPlayUpAbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    isPlaying = true;
                    btnPlayUpAbook.setBackgroundResource(R.drawable.pausa_audiolibro);
                    if (!isResume) {
                        releaseMP();
                        seekbProgressPopUpAbook.setEnabled(true);
                        new Thread(new RunnableInitMP(audioBook.getPath())).start();  //TODO

                    } else {
                        mediaPlayer.start();
                    }
                } else {
                    isPlaying = false;
                    isResume = true;
                    mediaPlayer.pause();
                    btnPlayUpAbook.setBackgroundResource(R.drawable.play_audiolibro);
                }
            }
        });

        seekbProgressPopUpAbook.setOnSeekBarChangeListener(this);
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
        alert(context.getString(R.string.error_in_this_file) + what + "-" + extra);
        activity.finish();
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
        btnPlayUpAbook.setBackgroundResource(R.drawable.play_audiolibro);
        isPlaying = false;
        isResume = false;
        releaseMP();
        seekbProgressPopUpAbook.setEnabled(false);
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
                btnPlayUpAbook.setBackgroundResource(R.drawable.pausa_audiolibro);
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
                    mediaPlayer.setOnErrorListener(AbookWindowPopUp.this);
                    mediaPlayer.setOnPreparedListener(AbookWindowPopUp.this);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(AbookWindowPopUp.this);
                    mediaPlayer.setWakeMode(context, PowerManager.SCREEN_BRIGHT_WAKE_LOCK);

                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                    activity.finish();
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
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();
                String auxTotal = "" + UtilsTime.milliSecondsToTimer(totalDuration);
                String auxCurrent = "" + UtilsTime.milliSecondsToTimer(currentDuration);
                lblTimeFinalPopUpAbook.setText(auxTotal);
                lblTimeInitialPopUpAbook.setText(auxCurrent);
                int progress = UtilsTime.getProgressPercentage(currentDuration, totalDuration);
                seekbProgressPopUpAbook.setProgress(progress);
                seekBarHandler.postDelayed(this, 100);
            } else {
                seekBarHandler.removeCallbacks(mUpdateTimeTask);
            }

        }
    };

    private void drawDurationAbook(AudioBook unAudioBook){
        File f = new File(unAudioBook.getPath());
        if (f.exists() && f.isFile() && f.canRead() && f.length() > 0) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(unAudioBook.getPath());
                mediaPlayer.prepare();

                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                String auxTotal = "" + UtilsTime.milliSecondsToTimer(totalDuration);
                String auxCurrent = "" + UtilsTime.milliSecondsToTimer(currentDuration);
                lblTimeFinalPopUpAbook.setText(auxTotal);

            }catch (Exception e){
                e.printStackTrace();
            }
        }




    }




}

