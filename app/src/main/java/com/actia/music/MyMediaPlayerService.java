package com.actia.music;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Is a service. In this service are loaded all songs of a Genre. When the user clicked a song,
 * all songs of this genre are loaded in the service.
 * This service reproduce all songs in background.
 *
 * @see Song
 */

@SuppressLint("NewApi")
public class MyMediaPlayerService extends Service implements MediaPlayer.OnErrorListener,
                                                             MediaPlayer.OnPreparedListener,
                                                             MediaPlayer.OnCompletionListener,
                                                             AudioManager.OnAudioFocusChangeListener {

    public static final String TAG = "MyServiceTag";
    private MediaPlayer mediaPlayer = null;
    //	HashMap<String, ArrayList<Song>> genreService=null;
    private boolean isPlaying = false;
    private boolean flagPause = false;
    //	public boolean isLoadSong=false;
    boolean bound = false;
    private final IBinder mBinder = new ServiceBinder();
    private int length = 0;
    AudioManager audioManager;
    ArrayList<Song> currentPlayListService = null;
    int currentIdSong = 0;
    Notification notification;
    String path = null;
    private int songCurrentDuration;
    private String nameGenre = null;

    //Random variables
    public static boolean randomPlayer = false;
    private static final int ARG_NEXT = 1;
    private static final int ARG_BACK = 2;
    private ArrayList<String> listRandom;

    /**
     * Link a service with MusicActivity
     */
    public class ServiceBinder extends Binder {
        public MyMediaPlayerService getService() {
            return MyMediaPlayerService.this;
        }
    }

    private static final int classID = 579; // just a number

    public static String START_PLAY = "START_PLAY";

    @Override
    public IBinder onBind(Intent intent) {
        bound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        if (flagPause) {
            stopForeground(true);
            stopSelf();
	    	/*setNameGenre(null);
	    	setcurrentIdSong(0);
	    	setCurrentGenre(null);*/
            flagPause = false;

        }
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        bound = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.i("Audio focus", "lo perdio");
        } else Log.i("Audio focus", "lo gano");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(START_PLAY, false)) {
                Log.i("MyMediaPlayerService", "onStartCommand");
            }
        }
        return Service.START_STICKY;
    }

    /**
     * Call to initMediaPlayer function
     *
     * @param currentGenre  song List
     * @param currentIdSong index of the song List song
     */
    public void startMusic(ArrayList<Song> currentGenre, int currentIdSong) {
        Log.i("MyMediaPlayerService", "Genero: " + currentGenre.size() + " Genero: " + getNameGenre());
        this.currentPlayListService = currentGenre;
        this.currentIdSong = currentIdSong;
        path = currentGenre.get(currentIdSong).Path;

        releasemp();

        initMediaPlayer(path);

        Intent intent2 = new Intent(this, PlayMusicActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent2, 0);

        notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getApplicationContext().getString(R.string.my_music_player))
                .setContentText(getApplicationContext().getString(R.string.now_playing))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent2);
        } else {
            startForeground(classID, notification);
        }

    }

    /**
     * Init mediaplayer
     *
     * @param localPath the song path
     */
    public void initMediaPlayer(String localPath) {
        try {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(localPath));
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
//            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getBaseContext().getString(R.string.error_play_file), Toast.LENGTH_SHORT).show();
            Log.e("Error rep gral música", e.toString());
        }
    }

    /**
     * Get genre name
     *
     * @return genre name
     */
    public String getNameGenre() {
        return nameGenre;
    }

    /**
     * Set genre name
     *
     * @param nameGenre genre name
     */
    public void setNameGenre(String nameGenre) {
        this.nameGenre = nameGenre;
    }

    /**
     * Check if Mediaplayer is running
     *
     * @return true if Mediaplayer is running
     */
    public boolean isMediaPlayerPlaying() {

        return mediaPlayer != null && isPlaying;
    }

    /**
     * Pause Media Player
     *
     * @return tue current position in the song list
     */
    public int pauseMusic() {
        if (isPlaying) {
            mediaPlayer.pause();
            flagPause = true;
            isPlaying = false;
            length = mediaPlayer.getCurrentPosition();
            songCurrentDuration = mediaPlayer.getDuration();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return length;
    }

    /**
     * Get the total duration of the file music
     *
     * @return total duration in miliseconds
     */
    public int getTotalDuration() {
        if (mediaPlayer != null)
            return mediaPlayer.getDuration();
        else return songCurrentDuration;
    }

    /**
     * Get the current time of the song
     *
     * @return the current time in miliseconds
     */
    public int getCurrentTime() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        else return length;
    }

    /**
     * Resume Music
     *
     * @param currentLength the resume time in millisecond.
     */
    public void resumeMusic(int currentLength) {
        if (!isPlaying && flagPause && mediaPlayer == null) {

            try {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
//                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.seekTo(currentLength);
                mediaPlayer.start();
                flagPause = false;
                isPlaying = true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getBaseContext().getString(R.string.error_play_file), Toast.LENGTH_SHORT).show();
                Log.e("Error en rep gral músic", e.toString());
            }

        }
    }

    /**
     * update position on the seek bar
     *
     * @param position position on the seek bar
     */
    public void resumeMusicSeekBar(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    /**
     * Stop music
     */
    public void stopMusic() {
        if (isPlaying) {
            releasemp();
            isPlaying = false;
            notification = null;
            stopForeground(true);
        }
    }

    /**
     * Start the next file music in the song list
     *
     * @return a song object.
     */
    public Song nextMusic() {
        flagPause = false;
        if (randomPlayer == false) {
            if (currentIdSong == (currentPlayListService.size() - 1))
                currentIdSong = 0;
            else {
                currentIdSong = currentIdSong + 1;
            }
        } else {
            if (listRandom == null || listRandom.isEmpty()) {
                return null;
            }
            currentIdSong = getPositionRandom(ARG_NEXT);
        }


        path = currentPlayListService.get(currentIdSong).Path;
        initMediaPlayer(path);

        Intent intent2 = new Intent(this, PlayMusicActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent2, 0);

        notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getBaseContext().getString(R.string.my_music_player))
                .setContentText(getBaseContext().getString(R.string.now_playing))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .build();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent2);
        } else {
            startForeground(classID, notification);
        }
        return currentPlayListService.get(currentIdSong);
    }

    /**
     * Start the previous file music in the song list
     *
     * @return a song object.
     */
    public Song backMusic() {
        flagPause = false;
        if (randomPlayer == false) {
            if (currentIdSong == 0)
                currentIdSong = currentPlayListService.size() - 1;
            else currentIdSong = currentIdSong - 1;

        } else {
            if (listRandom == null || listRandom.isEmpty()) {
                return null;
            }
            currentIdSong = getPositionRandom(ARG_BACK);
        }


        path = currentPlayListService.get(currentIdSong).Path;
        initMediaPlayer(path);

        Intent intent2 = new Intent(this, PlayMusicActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent2, 0);

        notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getBaseContext().getString(R.string.my_music_player))
                .setContentText(getBaseContext().getString(R.string.now_playing))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .build();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent2);
        } else {
            startForeground(classID, notification);
        }
        return currentPlayListService.get(currentIdSong);
    }

    /**
     * Cambia la bandera de Rando a true o false
     * en caso de habilitar el random se crea una arreglo con las canciones random
     * en caso de deshabilitar el random: borra el arreglo random
     */
    public void enableDisableRandom() {
        randomPlayer = !randomPlayer;
        if (randomPlayer == true) {
            listRandom = new ArrayList<>();
            listRandom.clear();
            for (int i = 0; i < currentPlayListService.size(); i++) {
                listRandom.add(currentPlayListService.get(i).Name);
            }
            Collections.shuffle(listRandom);
            listRandom.size();
        }

    }

    /**
     * @param opcPlayer indica si se le dio back o next
     * @return regresa la posicion de la cancion random en el arrglo original
     */
    private int getPositionRandom(int opcPlayer) {
//        int currentPositionInArrayRandom = listRandom.indexOf(currentIdSong);
        int indexInRandom = 0;


        for (int j = 0; j < listRandom.size(); j++) {
            if (listRandom.get(j).contains(currentPlayListService.get(currentIdSong).Name)) {
                indexInRandom = j;
            }
        }

        if (opcPlayer == ARG_BACK) {
            indexInRandom--;
            if (indexInRandom < 0) {
                indexInRandom = listRandom.size() - 1;
            }

        } else if (opcPlayer == ARG_NEXT) {
            indexInRandom++;
            if (indexInRandom >= listRandom.size()) {
                indexInRandom = 0;
            }
        }

        int indexInPlayer = 0;
        for (int h = 0; h < currentPlayListService.size(); h++) {
            if (currentPlayListService.get(h).Name.contains(listRandom.get(indexInRandom))) {
                indexInPlayer = h;
            }
        }


        return indexInPlayer;
    }

    /**
     * Get playlist
     *
     * @return an arraylist of songs.
     */
    public ArrayList<Song> getCurrentPlayList() {
        return currentPlayListService;
    }

    /**
     * Get the current id song
     *
     * @return current id song playing
     */
    public int getcurrentIdSong() {
        return currentIdSong;
    }

    /**
     * Get current song playing.
     *
     * @return a song
     */
    public Song getCurrentSong() {
        return currentPlayListService.get(currentIdSong);
    }

    /**
     * A HashMap witg song list
     * @return A HashMap witg song list
     */
//	public HashMap<String, ArrayList<Song>> getGenreService(){
//		return genreService;
//	}

    /**
     * Set the song List
     *
     * @param currentGenre song List
     */
    public void setCurrentPlayListService(ArrayList<Song> currentGenre) {
        this.currentPlayListService = currentGenre;
    }

    /**
     * Set current Id Song
     *
     * @param currentIdSong current id song
     */
    public void setcurrentIdSong(int currentIdSong) {
        this.currentIdSong = currentIdSong;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getApplicationContext(), getBaseContext().getString(R.string.destroy_service), Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null) {
            try {
                releasemp();
            } finally {
                mediaPlayer = null;
            }
        }
        stopForeground(true);

    }

    @Override
    public boolean onError(MediaPlayer mp, int arg1, int error) {
        mediaPlayer = mp;
        Log.e(getPackageName(), String.format("Error(%s%s)", arg1, error));
        releasemp();
        if (mediaPlayer != null) {
            try {
                releasemp();
            } finally {
                mediaPlayer = null;
            }
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
        if (mediaPlayer != null && !isPlaying) {
            mp.start();
            isPlaying = true;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                Log.i("AudioManager", "AUDIOFOCUS_GAIN");
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Log.i("AudioManager", "AUDIOFOCUS_LOSS");
                if (mediaPlayer != null) {
                    releasemp();
                    audioManager.abandonAudioFocus(this);
                    stopForeground(true);
                    stopSelf();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                Log.i("AudioManager", "AUDIOFOCUS_LOSS_TRANSIENT");
                if (mediaPlayer != null) {
                    releasemp();
                    audioManager.abandonAudioFocus(this);
                    stopForeground(true);
                    stopSelf();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                Log.i("AudioManager", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                if (mediaPlayer != null) {
                    releasemp();
                    audioManager.abandonAudioFocus(this);
                    stopForeground(true);
                    stopSelf();
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer = mp;
        if (!bound) {
            stopMusic();
            nextMusic();
            Log.i("Music Player Actia", "La interfaz NO se esta mostrando");
        } else {
            Intent i = new Intent("android.intent.action.onCompletionListener").putExtra("completion", "completionListener");
            this.sendBroadcast(i);
            Log.i("Music Player Actia", "La interfaz SI se esta mostrando");
        }
    }

    /**
     * Release MediaPlayer
     */
    public void releasemp() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
