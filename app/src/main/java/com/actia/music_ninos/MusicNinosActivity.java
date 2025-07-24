package com.actia.music_ninos;

import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.generic_2018_t10_t12.ErrorActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.MyMediaPlayerService;
import com.actia.music.Song;
import com.actia.utilities.utilities_ui.ColorsList;
import com.actia.utilities.utilities_external_storage.CheckExternalStorage;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;
import com.actia.utilities.utilities_media_player.UtilitiesGeneralMediaPlayer;
import com.actia.utilities.utilities_media_player.UtilsMediaPlayer;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MusicNinosActivity extends BaseActivity implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        SeekBar.OnSeekBarChangeListener,
        BaseActivity.CategoryNavigationListener,
        BaseActivity.PressBackHeader, BaseActivity.FuctionRandomListener {
    private final String TAG = this.getClass().getSimpleName();
    ImageButton btn_play_children;
    ImageButton btn_back_chi;
    ImageButton btn_next_chi;
    Button btnBackChildMusic;
    TextView name_song_children, name_artist, time_initial_children, time_final_children;
    RelativeLayout wraper_time;
    SeekBar songProgressBar_Children;
    ListView list_music_chi;

    ArrayList<Song> canciones;

    ArrayList<String> colores;
    ColorsList palette = new ColorsList();
    Handler mHandler = new Handler();
    boolean flagError = false;
    public static MediaPlayer mp;
    private UtilsMediaPlayer utils;
    int count = 0;
    private int currentSongIndex = 0;
    boolean flag_play = false;
    boolean flag_init = true;

    private ItemsHome itemCategory;
    private int positionCategory;

    //Random variables
    public static boolean randomPlayer = false;
    private static final int ARG_NEXT = 1;
    private static final int ARG_BACK = 2;
    private ArrayList<String> listRandom;
    private ImageView mVolumeUpImageView, mVolumeDownImageView;
    private SeekBar mVolumeSeekbar;
    private AudioManager mAudioManager;
    private View decorView;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_ninos);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getInfoCategory();
        startTopBar(this, UtilsLanguage.getTitleCategoryByLanguage(itemCategory), ContextCompat.getDrawable(this, R.drawable.ico_music_child), this);
        addListenersSensors_iSeat(listenersPlayers);
        startElementsUI();

        try {
            implementacionCodigo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(MusicNinosActivity.this);
                }
            }
        });

    }

    private void getInfoCategory(){

        for (int i = 0; i< ContextApp.categoriesChildren.size(); i++){
            if (ContextApp.categoriesChildren.get(i).getClassName().contains(this.getClass().getName())){
                itemCategory = ContextApp.categoriesChildren.get(i);
                positionCategory = i;
                return;
            }
        }

    }

    private void startElementsUI() {
        btn_play_children = findViewById(R.id.btn_play_children);
        btn_next_chi = findViewById(R.id.btn_next_chi);
        btn_back_chi = findViewById(R.id.btn_back_chi);
        btnBackChildMusic = findViewById(R.id.btnBackChildMusic);

        name_song_children = findViewById(R.id.name_song_children);
        name_artist = findViewById(R.id.name_artist);
        time_initial_children = findViewById(R.id.time_initial_children);
        time_final_children = findViewById(R.id.time_final_children);

        wraper_time = findViewById(R.id.wraper_time);
        songProgressBar_Children = findViewById(R.id.songProgressBar_Children);

        list_music_chi = findViewById(R.id.list_music_chi);

        mVolumeUpImageView = findViewById(R.id.volume_image_view);
        mVolumeDownImageView = findViewById(R.id.volume_down_image_view);
        mVolumeSeekbar = findViewById(R.id.volume_seek_bar);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        songProgressBar_Children.setEnabled(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void implementacionCodigo() throws IOException {


        CheckExternalStorage sdcard = new CheckExternalStorage();
        String stateSDCard = sdcard.getExternalStorageState();
        ReadFileExternalStorage externalStorage = new ReadFileExternalStorage();

        if (stateSDCard.equals(ConstantsApp.STATE_SD_CARD_MOUNTED)) {
            ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
            if (externalStorage.existDirectory(configSingleton.getMUSIC_CHILDREN_PATH())) {
                colores = palette.getColorsMusicChildrens();
                canciones = externalStorage.getSongsChildrens();

                if (canciones==null) {
                    this.finish();
                    Toast.makeText(context, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
                    return;
                }

                updateNameAndAuthorAndTime(canciones.get(0).Name, canciones.get(0).Author, canciones.get(0).Time);
                songProgressBar_Children.setOnSeekBarChangeListener(this);
                utils = new UtilsMediaPlayer();

                MusicNinosAdapter adaptador = new MusicNinosAdapter(this, canciones) {

                    @Override
                    public void onEntrada(Song song, View view) {

                        if (song != null) {

                            RelativeLayout rlytSongChild = view.findViewById(R.id.rlytSongChild);
                            TextView txtvTitleSongChild = view.findViewById(R.id.txtvTitleSongChild);
                            TextView txtvTimeSongChild = view.findViewById(R.id.txtvTimeSongChild);
                            TextView txtvAuthorSongChild = view.findViewById(R.id.txtvAuthorSongChild);


                            if (count > (colores.size() - 1))
                                count = 0;

                            rlytSongChild.setBackgroundColor(Color.parseColor(colores.get(count++)));

                            if (txtvTitleSongChild != null && txtvTimeSongChild != null && txtvAuthorSongChild != null) {
                                txtvTitleSongChild.setText(song.Name);
                                txtvTimeSongChild.setText(song.Time);
                                txtvAuthorSongChild.setText(song.Author);
                            }
                        }
                    }
                };
                list_music_chi.setSelected(true);
                list_music_chi.setFocusable(false);
//                list_music_chi.setBackground(getResources().getDrawable(R.drawable.state_item_focus_control_remote_list));
                list_music_chi.setAdapter(adaptador);
                list_music_chi.performItemClick(
                        list_music_chi.getAdapter().getView(currentSongIndex, null, null),
                        currentSongIndex,
                        list_music_chi.getAdapter().getItemId(currentSongIndex));

                list_music_chi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        songProgressBar_Children.setEnabled(true);
//                        updateNameAndAuthorAndTime(MusicAdapterChildren.songs.get(position).Name,MusicAdapterChildren.songs.get(position).Author,MusicAdapterChildren.songs.get(position).Time);
                        updateNameAndAuthorAndTime(canciones.get(position).Name, canciones.get(position).Author, canciones.get(position).Time);
                        currentSongIndex = position;
                        playSong(position);

                        if (!flag_play) {
                            btn_play_children.setImageResource(R.drawable.state_button_pause_child);
                            flag_play = true;
                        }

                    }
                });


                btn_play_children.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        songProgressBar_Children.setEnabled(true);
                        if (!flag_play) {

                            if (mp != null && !flag_init)
                                mp.start();
                            else if (flag_init) {
                                playSong(currentSongIndex);
                                flag_init = false;
                            }

//                            btn_play_children.setBackgroundResource(R.drawable.state_button_pause_child);
                            btn_play_children.setImageResource(R.drawable.state_button_pause_child);
                            flag_play = true;
                        } else {

                            if (mp != null)
                                mp.pause();
//                            btn_play_children.setBackgroundResource(R.drawable.ico_player_child_play_n);
                            btn_play_children.setImageResource(R.drawable.ico_player_child_play_n);
                            flag_play = false;
                        }
                    }
                });

                btn_back_chi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMusic();
                    }
                });


                btn_next_chi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextMusic();
                    }
                });

                btnBackChildMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                        MusicNinosActivity.this.finish();
                    }
                });

            } else
                Toast.makeText(getApplicationContext(), getString(R.string.error_no_path_music_children), Toast.LENGTH_SHORT).show();
        } else {

            Intent errorIntent = new Intent(this, ErrorActivity.class);
            errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            errorIntent.putExtra("state", getString(R.string.error_sd_not_mounted) + stateSDCard);
            MusicNinosActivity.this.finish();
            startActivity(errorIntent);
        }

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


    public void updateNameAndAuthorAndTime(String name, String author, String time) {
        name_song_children.setText(name);
        name_artist.setText(author);
        time_final_children.setText(time);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mp != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            if (!flagError) {
                int totalDuration = mp.getDuration();
                int currentPosition = UtilsMediaPlayer.progressToTimer(seekBar.getProgress(), totalDuration);
                mp.seekTo(currentPosition);
                updateProgressBar();
            }
        }
    }

    /**
     * Runanable to set interfaces name, time and author.
     */
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (!flagError) {
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();
                String auxTotal = "" + utils.milliSecondsToTimer(totalDuration);
                String auxCurrent = "" + utils.milliSecondsToTimer(currentDuration);
                time_final_children.setText(auxTotal);
                time_initial_children.setText(auxCurrent);
                int progress = utils.getProgressPercentage(currentDuration, totalDuration);
                songProgressBar_Children.setProgress(progress);
                mHandler.postDelayed(this, 1000);
            } else mHandler.removeCallbacks(mUpdateTimeTask);
        }
    };


    /**
     * Play a song
     *
     * @param songIndex index in a ListView
     */
    @SuppressWarnings("deprecation")
    public void playSong(int songIndex) {
        if (songIndex < list_music_chi.getFirstVisiblePosition() || songIndex > list_music_chi.getLastVisiblePosition() - 1) {
            list_music_chi.setSelection(songIndex);
        }


        try {
            if (UtilitiesGeneralMediaPlayer.isMyMediaPlayerServiceRunning(this)) {//Verifica si el servicio de música esta corriendo
                Intent intent = new Intent(this, MyMediaPlayerService.class);
                intent.addCategory(MyMediaPlayerService.TAG);
                this.stopService(intent);
            }
            releaseMediaPlayer();
            mp = new MediaPlayer();
            mp.setDataSource(canciones.get(songIndex).Path);
            mp.setOnErrorListener(this);
            mp.setOnPreparedListener(this);
            mp.setOnCompletionListener(this);
            mp.setWakeMode(getApplicationContext(), PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
            songProgressBar_Children.setProgress(0);
            songProgressBar_Children.setMax(100);
            mp.prepare();
            flagError = false;
            updateProgressBar();

        } catch (Exception e) {
            songProgressBar_Children.setEnabled(false);
            flagError = true;
            releaseMediaPlayer();
            btn_play_children.setBackgroundResource(R.drawable.ico_player_child_play_n);
            Toast.makeText(getApplicationContext(), getString(R.string.error_play_file), Toast.LENGTH_SHORT).show();
            Log.e("Error rep de niños: ", e.toString());
        }
    }

    /**
     * Release media player
     */
    private void releaseMediaPlayer() {

        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        MusicNinosActivity.mp = mp;
        releaseMediaPlayer();
        if (currentSongIndex < (canciones.size() - 1)) {
            playSong(currentSongIndex + 1);
            currentSongIndex = currentSongIndex + 1;
            list_music_chi.performItemClick(
                    list_music_chi.getAdapter().getView(currentSongIndex, null, null),
                    currentSongIndex,
                    list_music_chi.getAdapter().getItemId(currentSongIndex));
        } else {
            // play first song
            playSong(0);
            currentSongIndex = 0;
            list_music_chi.performItemClick(
                    list_music_chi.getAdapter().getView(currentSongIndex, null, null),
                    currentSongIndex,
                    list_music_chi.getAdapter().getItemId(currentSongIndex));
        }
        updateNameAndAuthorAndTime(canciones.get(currentSongIndex).Name,
                canciones.get(currentSongIndex).Author,
                canciones.get(currentSongIndex).Time);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(getPackageName(), String.format("Error(%s%s)", what, extra));
        MusicNinosActivity.mp = mp;
        songProgressBar_Children.setEnabled(false);
        flagError = true;
        releaseMediaPlayer();
        btn_play_children.setBackgroundResource(R.drawable.ico_player_child_play_n);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        MusicNinosActivity.mp = mp;
        MusicNinosActivity.mp.start();
        flag_play = true;
        btn_play_children.setBackgroundResource(R.drawable.ico_player_child_pause_n);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        releaseMediaPlayer();

        if (list_music_chi != null)
            list_music_chi.setAdapter(null);
        android.os.Debug.stopMethodTracing();
    }

    @Override
    public void previousCategory() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }
        goCategorySelectedInBottomBarChildren(MusicNinosActivity.this, positionCategory-1);
        MusicNinosActivity.this.finish();
    }

    @Override
    public void nextCategory() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }
        goCategorySelectedInBottomBarChildren(MusicNinosActivity.this, positionCategory+1);
        MusicNinosActivity.this.finish();
    }

    @Override
    public void clickBackHeader() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }
        UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(this);
        this.finish();
    }


    @Override
    protected void goChildrensActivity() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }
        super.goChildrensActivity();
    }

    @Override
    public void helpFuction() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }
        super.helpFuction();
    }

    @Override
    protected void home() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }
        super.home();
    }

    @Override
    public void randomInteraction() {
        if (canciones == null || canciones.size() == 0) {
            return;
        }


        if (canciones != null) {
            Random random = new Random();
            int indexRandom = random.nextInt(canciones.size());
            if (mp == null) {
                currentSongIndex = indexRandom;
                playSong(currentSongIndex);
                list_music_chi.setItemChecked(0, true);

            }


        }


        enableDisableRandom();

        if (randomPlayer) {
            imgBtnRandomBotBar.setImageDrawable(getResources().getDrawable(R.drawable.ico_random_white));
//            tvRandomBotBar.setTextColor(getResources().getColor(R.color.blue_bot_bar));
        } else {
            imgBtnRandomBotBar.setImageDrawable(getResources().getDrawable(R.drawable.ico_random_black));
//            tvRandomBotBar.setTextColor(getResources().getColor(R.color.white));
        }

        //fun: hace un random a la lista y las reproduce de arriba hacia abajo
       /* if (canciones == null || canciones.size() == 0) {
            return;
        }

        list_music_chi.setAdapter(null);
        Collections.shuffle(canciones);
        MusicNinosAdapter adaptador = new MusicNinosAdapter(this, canciones) {

            @Override
            public void onEntrada(Song song, View view) {

                if (song != null) {

                    RelativeLayout rlytSongChild = (RelativeLayout) view.findViewById(R.id.rlytSongChild);
                    TextView txtvTitleSongChild = (TextView) view.findViewById(R.id.txtvTitleSongChild);
                    TextView txtvTimeSongChild = (TextView) view.findViewById(R.id.txtvTimeSongChild);
                    TextView txtvAuthorSongChild = (TextView) view.findViewById(R.id.txtvAuthorSongChild);


                    if (count > (colores.size() - 1))
                        count = 0;

                    rlytSongChild.setBackgroundColor(Color.parseColor(colores.get(count++)));

                    if (txtvTitleSongChild != null && txtvTimeSongChild != null && txtvAuthorSongChild != null) {
                        txtvTitleSongChild.setText(song.Name);
                        txtvTimeSongChild.setText(song.Time);
                        txtvAuthorSongChild.setText(song.Author);
                    }
                }
            }
        };


        list_music_chi.setAdapter(adaptador);

        songProgressBar_Children.setEnabled(true);
        updateNameAndAuthorAndTime(canciones.get(0).Name, canciones.get(0).Author, canciones.get(0).Time);
        currentSongIndex = 0;
        playSong(0);
        list_music_chi.setItemChecked(0, true);
*/
    }


    //fixme: descomentar si se quiere que la musica se Detenga al perder el foco la pantalla (al iniciar una movie random)
//    @Override
//    protected void onPause() {
//        if (mp != null) {
//            mp.pause();
//            btn_play_children.setBackgroundResource(R.drawable.ico_player_child_play_n);
//            flag_play = false;
//        }
//        super.onPause();
//    }
    private void nextMusic() {
        if (randomPlayer==false){
            currentSongIndex++;
            if (currentSongIndex >= canciones.size()){
                currentSongIndex=0;
            }
        }else{
            currentSongIndex=getPositionRandom(ARG_NEXT);
        }

        list_music_chi.performItemClick(
                list_music_chi.getAdapter().getView(currentSongIndex, null, null),
                currentSongIndex,
                list_music_chi.getAdapter().getItemId(currentSongIndex));

        updateNameAndAuthorAndTime(canciones.get(currentSongIndex).Name,
                canciones.get(currentSongIndex).Author,
                canciones.get(currentSongIndex).Time);
    }

    private void backMusic(){
        if (randomPlayer==false){

            currentSongIndex--;
            if (currentSongIndex<0){
                currentSongIndex= canciones.size()-1;
            }



        }else{
            //fun: en caso de Random activado
            currentSongIndex=getPositionRandom(ARG_BACK);

        }
        list_music_chi.performItemClick(
                list_music_chi.getAdapter().getView(currentSongIndex, null, null),
                currentSongIndex,
                list_music_chi.getAdapter().getItemId(currentSongIndex));
        updateNameAndAuthorAndTime(canciones.get(currentSongIndex).Name,
                canciones.get(currentSongIndex).Author,
                canciones.get(currentSongIndex).Time);

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
            for (int i = 0; i < canciones.size(); i++) {
                listRandom.add(canciones.get(i).Name);
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
            if (listRandom.get(j).contains(canciones.get(currentSongIndex).Name)) {
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
        for (int h = 0; h < canciones.size(); h++) {
            if (canciones.get(h).Name.contains(listRandom.get(indexInRandom))) {
                indexInPlayer = h;
            }
        }


        return indexInPlayer;
    }

    BaseActivity.ListenersPlayers listenersPlayers = new ListenersPlayers() {
        @Override
        public void statusSensors(boolean isPassengerSitting, boolean isSafetyBeltPlaced) {
            if (mp==null){
                return;
            }
            if (isPassengerSitting && isSafetyBeltPlaced){
                if (!mp.isPlaying()){
                    mp.start();
                }
            }else {
                if (mp.isPlaying()){
                    mp.pause();
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }
        hideNavigationBar(MusicNinosActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(MusicNinosActivity.this);
    }

}
