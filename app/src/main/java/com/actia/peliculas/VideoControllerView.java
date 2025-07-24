/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actia.peliculas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_dialog_brightness.DialogBrightnessFragment;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

/**
 * A view containing controls for a MediaPlayer. Typically contains the
 * buttons like "Play/Pause", "Rewind", "Fast Forward" and a progress
 * slider. It takes care of synchronizing the controls with the state
 * of the MediaPlayer.
 * <p>
 * The way to use this class is to instantiate it programatically.
 * The MediaController will create a default set of controls
 * and put them in a window floating above your application. Specifically,
 * the controls will float above the view specified with setAnchorView().
 * The window will disappear if left idle for three seconds and reappear
 * when the user touches the anchor view.
 * <p>
 * Functions like show() and hide() have no effect when MediaController
 * is created in an xml layout.
 * 
 * MediaController will hide and
 * show the buttons according to these rules:
 * <ul>
 * <li> The "previous" and "next" buttons are hidden until setPrevNextListeners()
 *   has been called
 * <li> The "previous" and "next" buttons are visible but disabled if
 *   setPrevNextListeners() was called with null listeners
 * <li> The "rewind" and "fastforward" buttons are shown unless requested
 *   otherwise by using the MediaController(Context, boolean) constructor
 *   with the boolean set to false
 * </ul>
 */
public class VideoControllerView extends FrameLayout {
    private static final String TAG = "VideoControllerView";
    
    private MediaPlayerControl  mPlayer;
    private final Context mContext;
    private ViewGroup mAnchor;
    private View mRoot;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private boolean             mShowing;
    private boolean             mDragging;
    private static final int    sDefaultTimeout = 5000;
    private static final int    FADE_OUT = 1;
    private static final int    SHOW_PROGRESS = 2;
//    private final boolean             mUseFastForward;
//    private boolean             mFromXml;
//    private OnClickListener mNextListener, mPrevListener;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private ImageButton mPauseButton;
    SeekBar mVolumeSeekbar;
    AudioManager mAudioManager;
    ImageView mBrightImageView;
    ImageView mVolumeUpImageView;
    ImageView mVolumeDownImageView;
//    private ImageButton mFfwdButton;
//    private ImageButton mRewButton;
//    private ImageButton mNextButton;
//    private ImageButton mPrevButton;
    private final Handler mHandler = new MessageHandler(this);
    private TextViewsControls listener;

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        mContext = context;
//        mUseFastForward = true;
//        mFromXml = true;
        
        Log.i(TAG, TAG);
    }

    @SuppressWarnings("UnusedParameters")
    private VideoControllerView(Context context, boolean useFastForward, TextViewsControls lis) {
        super(context);
        mContext = context;
        listener = lis;
//        mUseFastForward = useFastForward;
        
        Log.i(TAG, TAG);
    }

    public VideoControllerView(Context context, TextViewsControls textControls) {
        this(context, true, textControls);

        Log.i(TAG, TAG);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }
    
    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
//        updateFullScreen();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     * @return The controller view.
     */
    @SuppressLint("InflateParams")
    private View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    @SuppressLint("WrongViewCast")
    private void initControllerView(View v) {
        mPauseButton = v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mProgress = v.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = v.findViewById(R.id.time);
        ImageView back = v.findViewById(R.id.back);
        mBrightImageView = v.findViewById(R.id.brightness);


        if(UtilsLanguage.isAppInEnglish()){
            back.setImageResource(R.drawable.state_btn_back_header_player_en);
            mBrightImageView.setImageResource(R.drawable.state_button_brightness_bar);
        }
        else{
            back.setImageResource(R.drawable.state_btn_back_header_player);
            mBrightImageView.setImageResource(R.drawable.state_button_brightness_bar);
        }
//        TextView home = (TextView) v.findViewById(R.id.home);

        back.setOnClickListener(mBackTextListener);
//        home.setOnClickListener(mHomeTextListener);

        mCurrentTime = v.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        //installPrevNextListeners();


        mVolumeUpImageView = v.findViewById(R.id.volume_image_view);
        mVolumeDownImageView = v.findViewById(R.id.volume_down_image_view);

        try {
            mVolumeSeekbar = v.findViewById(R.id.volume_seek_bar);
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mVolumeSeekbar.setMax(streamMaxVolume);
            mVolumeSeekbar.setProgress(currentVolume);


            mVolumeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    if(progress > mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
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
        } catch (Exception exception) {
            Log.e(TAG, "Something is wrong with the volume: ", exception);
        }

        mBrightImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrightnessDialog();
            }
        });

        mVolumeUpImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND| AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                mVolumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        });

        mVolumeDownImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND| AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                mVolumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        });
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        if (mPlayer == null) {
            return;
        }
        
        try {
            if (mPauseButton != null && !mPlayer.canPause()) {
                mPauseButton.setEnabled(false);
            }
//            if (mRewButton != null && !mPlayer.canSeekBackward()) {
//                mRewButton.setEnabled(false);
//            }
//            if (mFfwdButton != null && !mPlayer.canSeekForward()) {
//                mFfwdButton.setEnabled(false);
//            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }
    
    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    private void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            LayoutParams tlp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.BOTTOM
            );
            
            mAnchor.addView(this, tlp);
            mShowing = true;
        }
        updatePausePlay();
//        updateFullScreen();
        
        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }
    
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress( (int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }
        
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    private final OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    private final OnClickListener mBackTextListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.backMovie();
        }
    };

//    private final OnClickListener mHomeTextListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(getContext(),"INICIO", Toast.LENGTH_LONG).show();
//        }
//    };
//
//    private OnClickListener mFullscreenListener = new OnClickListener() {
//        public void onClick(View v) {
//            doToggleFullscreen();
//            show(sDefaultTimeout);
//        }
//    };

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || mPlayer == null) {
            return;
        }
//FIXME
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_play);
        }
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }
        
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

//    private void doToggleFullscreen() {
//        if (mPlayer == null) {
//            return;
//        }
//
//        mPlayer.toggleFullScreen();
//    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mPlayer == null) {
                return;
            }
            
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo( (int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime( (int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
//        if (mFfwdButton != null) {
//            mFfwdButton.setEnabled(enabled);
//        }
//        if (mRewButton != null) {
//            mRewButton.setEnabled(enabled);
//        }
//        if (mNextButton != null) {
//            mNextButton.setEnabled(enabled && mNextListener != null);
//        }
//        if (mPrevButton != null) {
//            mPrevButton.setEnabled(enabled && mPrevListener != null);
//        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

//    private OnClickListener mRewListener = new OnClickListener() {
//        public void onClick(View v) {
//            if (mPlayer == null) {
//                return;
//            }
//
//            int pos = mPlayer.getCurrentPosition();
//            pos -= 5000; // milliseconds
//            mPlayer.seekTo(pos);
//            setProgress();
//
//            show(sDefaultTimeout);
//        }
//    };
//
//    private OnClickListener mFfwdListener = new OnClickListener() {
//        public void onClick(View v) {
//            if (mPlayer == null) {
//                return;
//            }
//
//            int pos = mPlayer.getCurrentPosition();
//            pos += 15000; // milliseconds
//            mPlayer.seekTo(pos);
//            setProgress();
//
//            show(sDefaultTimeout);
//        }
//    };

    private void installPrevNextListeners() {
//        if (mNextButton != null) {
//            mNextButton.setOnClickListener(mNextListener);
//            mNextButton.setEnabled(mNextListener != null);
//        }
//
//        if (mPrevButton != null) {
//            mPrevButton.setOnClickListener(mPrevListener);
//            mPrevButton.setEnabled(mPrevListener != null);
//        }
    }

//    public void setPrevNextListeners(OnClickListener next, OnClickListener prev) {
////        mNextListener = next;
////        mPrevListener = prev;
//        boolean mListenersSet = true;
//
//        if (mRoot != null) {
//            installPrevNextListeners();
//
////            if (mNextButton != null && !mFromXml) {
////                mNextButton.setVisibility(View.VISIBLE);
////            }
////            if (mPrevButton != null && !mFromXml) {
////                mPrevButton.setVisibility(View.VISIBLE);
////            }
//        }
//    }
    
    public interface MediaPlayerControl {
        void    start();
        void    pause();
        int     getDuration();
        int     getCurrentPosition();
        void    seekTo(int pos);
        boolean isPlaying();
        int     getBufferPercentage();
        boolean canPause();
//        boolean canSeekBackward();
//        boolean canSeekForward();
//        boolean isFullScreen();
//        void    toggleFullScreen();
    }

    public interface TextViewsControls{
        void backMovie();
    }
    
    private static class MessageHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        MessageHandler(VideoControllerView view) {
            mView = new WeakReference<>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }
            
            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.setProgress();
                    if (!view.mDragging && view.mShowing && view.mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }

    protected void showBrightnessDialog() {
        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogBrightnessFragment dialog = DialogBrightnessFragment.newInstance();
        dialog.show(ft, "dialog");
    }
}