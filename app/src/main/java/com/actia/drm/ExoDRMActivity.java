package com.actia.drm;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.VideoControllerView;
import com.actia.utilities.AsyncIntents;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.Util;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.media.PlaylistProxy;
import com.intertrust.wasabi.media.PlaylistProxyListener;

import java.io.File;
import java.io.FilenameFilter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import exoplayer.DemoPlayer;
import exoplayer.DemoPlayer.RendererBuilder;
import exoplayer.ExtractorRendererBuilder;
import exoplayer.HlsRendererBuilder;

/*
 * Copyright (C) 2014 The Android Open Source Project
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

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
public class ExoDRMActivity extends BaseActivity implements SurfaceHolder.Callback,
        DemoPlayer.Listener, DemoPlayer.CaptionListener, DemoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener, onPersonalizeListener, PlaylistProxyListener, VideoControllerView.MediaPlayerControl, VideoControllerView.TextViewsControls {

//    public static final int TYPE_DASH = 0;
//    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;

//    public static final String CONTENT_TYPE_EXTRA = "content_type";
//    public static final String CONTENT_ID_EXTRA = "content_id";

    private static final String TAG = "ExoDRMActivity";
//    private static final int MENU_GROUP_TRACKS = 1;
//    private static final int ID_OFFSET = 2;

    private static final CookieManager defaultCookieManager;
    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }
    private EventLogger eventLogger;
    private VideoControllerView mediaController;
    //  private View debugRootView;
    private View shutterView;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    //  private TextView debugTextView;
//  private TextView playerStateTextView;
    private SubtitleLayout subtitleLayout;

    private DemoPlayer player;
    //  private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsPrepare;
    private boolean isPaused = false;

    private long playerPosition;
    @SuppressWarnings("unused")
    private boolean enableBackgroundAudio;

    private Uri contentUri;
    private int contentType;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;

    private PlaylistProxy playerProxy;
    private ImageView BannerView;

    private ConfigMasterMGC singletonConfig;

//    public ProgressDialog dialogMovie;
    int count=0;
//	private boolean isSurfaceAvailable=false;

    private File dirBanners;
//    private boolean isGetBanners=false;
    private File[] bannersImg=null;
    private ImageWorker w;
    private final int DelayTimeBanner=8000;
    private int DeyalShowBanner=900000; //15 min
//    private int DelayShowPromoVid = 1200000;
    protected Handler handlerBanner=null;
    //	private ImageView BannerView;
    private ScheduledExecutorService validationExecutor;

    public static final String keyPathDRM="drm_path";
    private String moviePath=null;
    private final ArrayList<String> BannersShowed = new ArrayList<>();
    private final int timeToSaveLogMovies=900000; //15 min;
    private View decorView;

    private SeekBar mVolumeSeekbar;
    AudioManager mAudioManager;
    private final Handler handlerMediaControl = new Handler();

    @Override
    public void start() {
        if(player!=null) {
            player.setPlayWhenReady(true);
            isPaused = false;
        }
    }

    ///PRUEBA

    @Override
    public void pause() {
        if (player!= null){
                player.setPlayWhenReady(false);
                isPaused = true;
        }
    }

    @Override
    public int getDuration() {
        if(player!=null)
            return (int) player.getDuration();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(player!=null)
            return (int) player.getCurrentPosition();
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if(player!=null)
            player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return !isPaused;
//        return !isPaused;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

//    @Override
//    public boolean canSeekBackward() {
//        return false;
//    }
//
//    @Override
//    public boolean canSeekForward() {
//        return false;
//    }
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




    enum ContentTypes {
//        DASH("application/dash+xml"), HLS("application/vnd.apple.mpegurl"),
        PDCF("video/mp4");
//        M4F("video/mp4"), DCF("application/vnd.oma.drm.dcf"), BBTS("video/mp2t");
        String mediaSourceParamsContentType = null;

        ContentTypes(String mediaSourceParamsContentType) {
            this.mediaSourceParamsContentType = mediaSourceParamsContentType;
        }

        public String getMediaSourceParamsContentType() {
            return mediaSourceParamsContentType;
        }
    }

    // Activity lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.playerdrm_activity);
        View root = findViewById(R.id.root);

        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
//                    return mediaController.dispatchKeyEvent(event);
//                }
//                return false;
                return keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && mediaController.dispatchKeyEvent(event);
            }
        });
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);

        shutterView = findViewById(R.id.shutter);
//	    debugRootView = findViewById(R.id.controls_root);

        videoFrame = findViewById(R.id.video_frame);
        surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
//	    debugTextView = (TextView) findViewById(R.id.debug_text_view);
        BannerView= findViewById(R.id.banner);

//	    playerStateTextView = (TextView) findViewById(R.id.player_state_view);
        subtitleLayout = findViewById(R.id.subtitles);

//        mediaController = new MediaController(this);
//        mediaController.setAnchorView(root);
        mediaController = new VideoControllerView(this,this);
        mediaController.setAnchorView((ViewGroup) findViewById(R.id.root));



        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }


        Bundle extras = getIntent().getExtras();

        if (extras != null){

            singletonConfig = ConfigMasterMGC.getConfigSingleton();

            moviePath = extras.getString(keyPathDRM);
            dirBanners=new File(singletonConfig.getPathBanners());

        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.null_path), Toast.LENGTH_SHORT).show();
            this.finish();
        }

        DRM drm = new DRM(ExoDRMActivity.this);
        drm.personalizeDevice(this);

        Toast.makeText(ExoDRMActivity.this, getString(R.string.class_exo), Toast.LENGTH_SHORT).show();

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(ExoDRMActivity.this);
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(ExoDRMActivity.this);
        configureSubtitleView();

        DRM drm = new DRM(ExoDRMActivity.this);
        drm.personalizeDevice(this);

        // The player will be prepared on receiving audio capabilities.
//    audioCapabilitiesReceiver.register();
    }

    @Override
    public void onPause() {
        super.onPause();



        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            player.setBackgrounded(true);
        }
        audioCapabilitiesReceiver.unregister();
        shutterView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();

    }


    // AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            this.audioCapabilities = audioCapabilities;
            releasePlayer();
//      preparePlayer();
        } else {
            player.setBackgrounded(false);
        }
    }

    // Internal methods

    private RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(this, getString(R.string.class_exo_demo));
        switch (contentType) {
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(this, userAgent, contentUri);
            case TYPE_HLS:
                return new HlsRendererBuilder(this, userAgent, contentUri.toString(), audioCapabilities);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer() {
        if (player == null) {

            String proxy_url;

            if(contentUri.toString().endsWith(".mlv") || contentUri.toString().endsWith(".MLV")) {
                ContentTypes contentTypes = ContentTypes.PDCF;
                PlaylistProxy.MediaSourceParams parames = new PlaylistProxy.MediaSourceParams();
                parames.sourceContentType = contentTypes.getMediaSourceParamsContentType();
                try {
                    proxy_url = playerProxy.makeUrl(contentUri.toString(), PlaylistProxy.MediaSourceType.SINGLE_FILE, parames);
                    contentUri = Uri.parse(proxy_url);
                    contentType = TYPE_HLS;

                } catch (ErrorCodeException e) {
                    e.printStackTrace();
                }
            }else{
                contentType = TYPE_OTHER;
            }


            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(ExoDRMActivity.this);
            mediaController.setEnabled(true);
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
//      debugViewHelper = new DebugTextViewHelper(player, debugTextView);
//      debugViewHelper.start();
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);

        handlerMediaControl.post(new Runnable() {

            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });

    }

    private void releasePlayer() {
        if (player != null) {
//      debugViewHelper.stop();
//      debugViewHelper = null;
      playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
//    if (playbackState == ExoPlayer.STATE_ENDED) {
//      showControls();
//    }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                sendLog(); //FIXME
                ExoDRMActivity.this.finish();
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";

                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                if(validationExecutor == null)
                    getBanners();

                break;
            default:
                text += "unknown";
                break;
        }
//    playerStateTextView.setText(text);
    }

    public void sendLog(){
        File movie = new File(moviePath);
        if(movie!=null && BannersShowed!=null){
            Intent returnIntent = new Intent();
            Bundle b=new Bundle();
            b.putStringArrayList("banners", BannersShowed);
            b.putString("movie", movie.getName());
            returnIntent.putExtra("data", b);
            setResult(RESULT_OK,returnIntent);
        }
    }

    @Override
    public void onError(Exception e) {
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
            Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
//    showControls();

//    ExoDRMActivity.this.finish();

    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
        shutterView.setVisibility(View.GONE);
        /*videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);*/
    }

    // User controls

//    private boolean haveTracks(int type) {
//        return player != null && player.getTrackCount(type) > 0;
//    }
//
//    public void showVideoPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        configurePopupWithTracks(popup, null, DemoPlayer.TYPE_VIDEO);
//        popup.show();
//    }
//
//    public void showAudioPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        Menu menu = popup.getMenu();
//        menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.enable_background_audio);
//        final MenuItem backgroundAudioItem = menu.findItem(0);
//        backgroundAudioItem.setCheckable(true);
//        backgroundAudioItem.setChecked(enableBackgroundAudio);
//        OnMenuItemClickListener clickListener = new OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item == backgroundAudioItem) {
//                    enableBackgroundAudio = !item.isChecked();
//                    return true;
//                }
//                return false;
//            }
//        };
//        configurePopupWithTracks(popup, clickListener, DemoPlayer.TYPE_AUDIO);
//        popup.show();
//    }
//
//    public void showTextPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        configurePopupWithTracks(popup, null, DemoPlayer.TYPE_TEXT);
//        popup.show();
//    }
//
//    public void showVerboseLogPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        Menu menu = popup.getMenu();
//        menu.add(Menu.NONE, 0, Menu.NONE, R.string.logging_normal);
//        menu.add(Menu.NONE, 1, Menu.NONE, R.string.logging_verbose);
//        menu.setGroupCheckable(Menu.NONE, true, true);
//        menu.findItem((VerboseLogUtil.areAllTagsEnabled()) ? 1 : 0).setChecked(true);
//        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == 0) {
//                    VerboseLogUtil.setEnableAllTags(false);
//                } else {
//                    VerboseLogUtil.setEnableAllTags(true);
//                }
//                return true;
//            }
//        });
//        popup.show();
//    }
//
//    private void configurePopupWithTracks(PopupMenu popup,
//                                          final OnMenuItemClickListener customActionClickListener,
//                                          final int trackType) {
//        if (player == null) {
//            return;
//        }
//        int trackCount = player.getTrackCount(trackType);
//        if (trackCount == 0) {
//            return;
//        }
//        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                return (customActionClickListener != null
//                        && customActionClickListener.onMenuItemClick(item))
//                        || onTrackItemClick(item, trackType);
//            }
//        });
//        Menu menu = popup.getMenu();
//        // ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0)
//        menu.add(MENU_GROUP_TRACKS, DemoPlayer.DISABLED_TRACK + ID_OFFSET, Menu.NONE, R.string.off);
//        if (trackCount == 1 && TextUtils.isEmpty(player.getTrackName(trackType, 0))) {
//            menu.add(MENU_GROUP_TRACKS, DemoPlayer.PRIMARY_TRACK + ID_OFFSET, Menu.NONE, R.string.on);
//        } else {
//            for (int i = 0; i < trackCount; i++) {
//                menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE, player.getTrackName(trackType, i));
//            }
//        }
//        menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
//        menu.findItem(player.getSelectedTrackIndex(trackType) + ID_OFFSET).setChecked(true);
//    }
//
//    private boolean onTrackItemClick(MenuItem item, int type) {
//        if (player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
//            return false;
//        }
//        player.selectTrack(type, item.getItemId() - ID_OFFSET);
//        return true;
//    }

    private void toggleControlsVisibility()  {
        if (mediaController.isShowing()) {
            mediaController.hide();
//      debugRootView.setVisibility(View.GONE);
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaController.show();
//    debugRootView.setVisibility(View.VISIBLE);
    }

    // DemoPlayer.CaptionListener implementation

    @Override
    public void onCues(List<Cue> cues) {
        subtitleLayout.setCues(cues);
    }

    // DemoPlayer.MetadataListener implementation

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (TxxxMetadata.TYPE.equals(entry.getKey())) {
                TxxxMetadata txxxMetadata = (TxxxMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s",
                        TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
            } else if (PrivMetadata.TYPE.equals(entry.getKey())) {
                PrivMetadata privMetadata = (PrivMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s",
                        PrivMetadata.TYPE, privMetadata.owner));
            } else if (GeobMetadata.TYPE.equals(entry.getKey())) {
                GeobMetadata geobMetadata = (GeobMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename,
                        geobMetadata.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    private void configureSubtitleView() {
        CaptionStyleCompat captionStyle;
        float captionFontScale;
        if (Util.SDK_INT >= 19) {
            captionStyle = getUserCaptionStyleV19();
            captionFontScale = getUserCaptionFontScaleV19();
        } else {
            captionStyle = CaptionStyleCompat.DEFAULT;
            captionFontScale = 1.0f;
        }
        subtitleLayout.setStyle(captionStyle);
        subtitleLayout.setFontScale(captionFontScale);
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }


    @Override
    public void onPersonalized(boolean error) {
        try {
            EnumSet<PlaylistProxy.Flags> flags = EnumSet.noneOf(PlaylistProxy.Flags.class);
            playerProxy = new PlaylistProxy(flags, this, new Handler());
            playerProxy.start();


            audioCapabilitiesReceiver.register();

            Log.i(TAG, "es nula");

            playVideo();

        } catch (ErrorCodeException e) {
            Log.e("ON PERSONALIZED", "playlist proxy error: " + e.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), getString(R.string.error_playlist_proxy) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    @Override
    public void onErrorNotification(int errorCode, String errorString) {


        Log.e("Errornotification", "PlaylistProxy Event: Error Notification, error code = " +
                errorCode + ", error string = " +
                errorString);
        Toast.makeText(getApplicationContext(), getString(R.string.PlaylistProxy_Event) +
                errorCode + ", "+getString(R.string.error_string) +
                errorString, Toast.LENGTH_LONG).show();
        this.finish();


    }






    public void getBanners(){
//        isGetBanners=true;
        FilenameFilter fileExtensionImageByLanguage = UtilsLanguage.getFileExtensionImageByLanguage(dirBanners);
        if(dirBanners.exists() && dirBanners.listFiles(fileExtensionImageByLanguage).length>0){
            resetHandlerBanners();
            bannersImg=null;
            bannersImg=dirBanners.listFiles(fileExtensionImageByLanguage);
            List<File> bannerList = Arrays.asList(bannersImg);
            Collections.shuffle(bannerList);
            bannersImg = bannerList.toArray(new File[bannerList.size()]);
            w=new ImageWorker();
            if(player !=null){
                int x;
                if(bannersImg.length>1)
                    x=DeyalShowBanner=(int) (player.getDuration()/(bannersImg.length));
                else
                    x = DeyalShowBanner=(int) (player.getDuration()/2);
                //int x=15000;
                if(x>DelayTimeBanner)
                    DeyalShowBanner=x;
                updateBanner();
            }

            //Toast.makeText(getApplicationContext(), "time: "+DeyalShowBanner/60000, Toast.LENGTH_LONG).show();
        }
//        isGetBanners=false;
    }

    public void resetHandlerBanners(){
        BannerView.setVisibility(View.INVISIBLE);
//	validationHandler.removeCallbacks(mUpdateTimeTask);
//	validationExecutor.shutdownNow();
        if(validationExecutor != null){
            validationExecutor.shutdown();
            validationExecutor = null;
        }

        if(handlerBanner!=null)
            handlerBanner.removeCallbacks(delayBanner);
    }

    /**
     * Display a banner every time.
     */
    public void updateBanner() {
//	validationHandler.postDelayed(mUpdateTimeTask,DeyalShowBanner);
        if(validationExecutor == null){
            validationExecutor = Executors
                    .newScheduledThreadPool(1);
            validationExecutor.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    updateTimeTaskMethod();
                }
            }, 10000, DeyalShowBanner, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Runnable to show the banner
     */
    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {

            BannerView.setVisibility(View.VISIBLE);
            BannerView.setImageResource(R.drawable.transparent);
            BannerView.setImageBitmap(null);
            BannerView.destroyDrawingCache();
            handlerBanner = new Handler();
            handlerBanner.postDelayed(delayBanner, DelayTimeBanner);
            if(count>=bannersImg.length)
                count=0;
            if(bannersImg[count].exists() && bannersImg[count].length()>0){
                w.loadBitmap(bannersImg[count].getPath(),BannerView, getApplicationContext(),1280, 120);
                BannersShowed.add(bannersImg[count].getName());
            }
            count++;

            updateBanner();
        }
    };

    private void updateTimeTaskMethod(){
        this.runOnUiThread(mUpdateTimeTask);
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


    public void playVideo(){

        new InitMediaplayer().execute(moviePath);

    }


    class InitMediaplayer {


        protected String execute(String... params) {
            File source=new File(params[0]);

            if(source.exists() && source.length()>0){

                contentUri = Uri.parse(Uri.fromFile(source).toString());
                contentType = TYPE_HLS;
//                String contentId = "";

                preparePlayer();

            }else{
                ExoDRMActivity.this.finish();
            }
            return null;
        }

    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                backMovie();
                return false;
            default:
                mediaController.show();
                return false;
        }
    }

    @Override
    public void backMovie() {
        if (player!=null){
            if (player.getCurrentPosition()>=timeToSaveLogMovies){
                sendLog();
            }
        }
        releasePlayer();
        ExoDRMActivity.this.finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(ExoDRMActivity.this);
    }

}

