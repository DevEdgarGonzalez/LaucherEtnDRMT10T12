package com.actia.drm;

import android.media.MediaCodec;
import android.util.Log;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.util.VerboseLogUtil;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import exoplayer.DemoPlayer;

public class EventLogger implements DemoPlayer.Listener, DemoPlayer.InfoListener,
    DemoPlayer.InternalErrorListener{

    private static  final String TAG = "EventLogger";
    private static final NumberFormat TIME_FORMAT;

    static {
        TIME_FORMAT = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
    }

    private long sessionStartTimeMs;
    private final long[] loadStartTimeMs;
    private long[] seekRangeValuesUs;

    public EventLogger(){
        loadStartTimeMs = new long[DemoPlayer.RENDERER_COUNT];
    }

    public void startSession(){
        sessionStartTimeMs = android.os.SystemClock.elapsedRealtime();
        Log.d(TAG, "start [0]");
    }

    public void endSession(){
        Log.d(TAG, "end [" + getSessionTimeString() + "]");
    }

    private String getSessionTimeString(){
        return getTimeString(android.os.SystemClock.elapsedRealtime() - sessionStartTimeMs);
    }

    private String getTimeString(long timeMs){
        return TIME_FORMAT.format((timeMs) / 1000f);
    }

    @Override
    public void onVideoFormatEnabled(Format format, int trigger, int mediaTimeMs) {
        Log.d(TAG, "videoFormat [" + getSessionTimeString() + ", " + format.id + ", " + trigger + "]");
    }

    @Override
    public void onAudioFormatEnabled(Format format, int trigger, int mediaTimeMs) {
        Log.d(TAG, "audioFormat [" + getSessionTimeString() + ", " + format.id + ", " + trigger + "]");
    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
        Log.d(TAG, "bandwidth [" + getSessionTimeString() + ", " + count + "]");
    }

    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
        Log.d(TAG, "videoFormat [" + getSessionTimeString() + ", " + bytes + ", " + getTimeString(elapsedMs) + ", " + bitrateEstimate + "]");
    }

    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, int mediaStartTimeMs, int mediaEndTimeMs) {
        loadStartTimeMs[sourceId] = android.os.SystemClock.elapsedRealtime();
        if(VerboseLogUtil.isTagEnabled(TAG)){
            Log.d(TAG, "loadStart [" + getSessionTimeString() + ", " + sourceId + ", " + type + ", " + mediaStartTimeMs +
                    ", " + mediaEndTimeMs + "]");
        }
    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, int mediaStartTimeMs, int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
        if(VerboseLogUtil.isTagEnabled(TAG)){
            long downloadTime = android.os.SystemClock.elapsedRealtime() - loadStartTimeMs[sourceId];
            Log.d(TAG, "loadEnd [" + getSessionTimeString() + ", " + sourceId + ", " + downloadTime + "]");
        }
    }

    @Override
    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
        Log.d(TAG, "decoderInitializated [" + getSessionTimeString()+ ", " + decoderName + "]");
    }

    @Override
    public void onSeekRangeChanged(TimeRange seekRange) {
        seekRangeValuesUs = seekRange.getCurrentBoundsUs(seekRangeValuesUs);
        Log.d(TAG, "seekRange [" + seekRange.type + ", " + seekRangeValuesUs[0] + ", "
        + seekRangeValuesUs[1] + "]");
    }

    private void printInternalError(String type, Exception e){
        Log.e(TAG, "internalError [" + getSessionTimeString() + ", " + type + "]", e);
    }

    @Override
    public void onRendererInitializationError(Exception e) {
        printInternalError("rendererInitError", e);
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        printInternalError("audioTrackInitializationError", e);
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
        printInternalError("audioTrackWriteError", e);
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        printInternalError("decoderInitializationError", e);
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        printInternalError("cryptoError", e);
    }

    @Override
    public void onLoadError(int sourceId, IOException e) {
        printInternalError("loadError", e);
    }

    @Override
    public void onDrmSessionManagerError(Exception e) {
        printInternalError("drmSessionManagerError", e);
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "state [" + getSessionTimeString() + ", " + playWhenReady + ", "
        + getStateString(playbackState) + "]");
    }

    private String getStateString(int playbackState) {
        switch (playbackState){
            case ExoPlayer.STATE_BUFFERING:
                return "B";
            case ExoPlayer.STATE_ENDED:
                return "E";
            case ExoPlayer.STATE_IDLE:
                return "I";
            case ExoPlayer.STATE_PREPARING:
                return "P";
            case ExoPlayer.STATE_READY:
                return "R";
            default:
                return "?";
        }
    }

    @Override
    public void onError(Exception e) {
        if(e != null)
        Log.e("playerFailed [" + getSessionTimeString() + "]",e.toString());
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        Log.d(TAG, "videoSizeChanged [" + width + ", " + height + ", " + pixelWidthHeightRatio + "]");
    }
}
