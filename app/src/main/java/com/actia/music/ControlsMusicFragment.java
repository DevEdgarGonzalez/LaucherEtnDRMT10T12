package com.actia.music;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilsFonts;
import com.actia.utilities.utilities_media_player.UtilsMediaPlayer;

import java.util.ArrayList;

/**
 * Fragment with Mediaplayer Controls.
 */
public class ControlsMusicFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{

	public static final String ARG_ID_ENTRADA_SELECIONADA = "item_id";
	private Song mSong;
	Song currentSong;
	String genre;
	boolean isPlaying=false;
	boolean initPlaying=false;
	boolean flagSeekBar=false;
	ArrayList<Song> currentPlayList;
	int currentIdSong;
	int currentTimePosition=0;
	ImageView imgvPlay;
	ImageView imgvNext;
	ImageView imgvBack;
	private ImageView mVolumeUpImageView, mVolumeDownImageView;
	private SeekBar mVolumeSeekbar;
	private AudioManager mAudioManager;
	int fromView;
	View rootView;
	private SeekBar songProgressBar;
	private final Handler mHandler = new Handler();
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private UtilsMediaPlayer utils;
	private BroadcastReceiver mReceiver;
	IntentFilter intentFilter;
	public CallChangeItemsListMsuic mCallbacksControls = CallbacksVaciosControl;

	public ControlsMusicFragment() {

	}

	/**
	 * Interface definition for a callback to be invoked when a song have finished and
	 * the next song in the list has to start so the item Listview has to change of color
	 * representing the current song.
	 * This fragment call to interface to communicate with the MusicActivty then the activity 
	 * calls to setItemListCurrentSong method of the Fragment_List so the current item song in
	 * the ListView is selected.
	 */
	public interface CallChangeItemsListMsuic {
		void onChangeItem(String genre, int message);
	}

	private static final CallChangeItemsListMsuic CallbacksVaciosControl = new CallChangeItemsListMsuic() {
		@Override
		public void onChangeItem(String genre,int message) {
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ID_ENTRADA_SELECIONADA)) {
			//dataSong[0] Genre
			//dataSong[1] id Song
			//dataSong[2] whether clicked a item from Listview songs get value 1

			String[] dataSong;
			dataSong=getArguments().getStringArray(ARG_ID_ENTRADA_SELECIONADA);
			if(PlayMusicActivity.mServ.getCurrentPlayList()!=null){
				Log.i("Fragment control", "getNameGenre: No es null");
				currentPlayList= PlayMusicActivity.mServ.getCurrentPlayList();
			}else{
				Log.i("Fragment control", "getNameGenre: Es null");
				currentPlayList = PlayMusicActivity.GlobalSongs;
				Log.i("Fragment control", "getNameGenre: "+currentPlayList.size());
			}
			if (dataSong != null) {
				currentIdSong = Integer.parseInt(dataSong[1]);
				mSong =currentPlayList.get(Integer.parseInt(dataSong[1]));
				fromView=Integer.parseInt(dataSong[2]);
			}
			genre=mSong.Genre;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.layout_fragment_detalle, container, false);

		//Broadcast to OnCompletionListener launch from service music   
		intentFilter = new IntentFilter("android.intent.action.onCompletionListener");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//extract our message from intent
				//String msg_for_me = intent.getStringExtra("completion");
				if(flagSeekBar)
					mHandler.removeCallbacks(mUpdateTimeTask);

				PlayMusicActivity.mServ.stopMusic();
				currentSong = PlayMusicActivity.mServ.nextMusic();
				updateProgressBar();
				mCallbacksControls.onChangeItem(genre, PlayMusicActivity.mServ.getcurrentIdSong());
				if (currentSong != null) {
					((TextView) rootView.findViewById(R.id.textView_titulocancion)).setText(currentSong.Name+" ");
					((TextView) rootView.findViewById(R.id.textView_autor)).setText(currentSong.Author);
				}
			}
		};

		getActivity().registerReceiver(mReceiver, intentFilter);

		//set view mediaplayer controls
		imgvPlay = rootView.findViewById(R.id.play);
		imgvNext = rootView.findViewById(R.id.adelante);
		imgvBack = rootView.findViewById(R.id.atras);
		songProgressBar = rootView. findViewById(R.id.songProgressBar);
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		songCurrentDurationLabel = rootView.findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = rootView.findViewById(R.id.songTotalDurationLabel);
		mVolumeUpImageView = rootView.findViewById(R.id.volume_image_view);
		mVolumeDownImageView = rootView.findViewById(R.id.volume_down_image_view);
		mVolumeSeekbar = rootView.findViewById(R.id.volume_seek_bar);
		mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		utils=new UtilsMediaPlayer();

		/*songTotalDurationLabel.setTypeface(UtilsFonts.getTypefaceCategoryMusic(getContext()));
		songCurrentDurationLabel.setTypeface(UtilsFonts.getTypefaceCategoryMusic(getContext()));*/

		if (mSong != null) {
			((TextView) rootView.findViewById(R.id.textView_titulocancion)).setText(mSong.Name+" ");
//			((TextView) rootView.findViewById(R.id.textView_titulocancion)).setTypeface(UtilsFonts.getTypefaceCategoryMusic(getContext()));
			((TextView) rootView.findViewById(R.id.textView_autor)).setText(mSong.Author);
//			((TextView) rootView.findViewById(R.id.textView_autor)).setTypeface(UtilsFonts.getTypefaceCategoryMusic(getContext()));
			mCallbacksControls.onChangeItem(genre, PlayMusicActivity.mServ.getcurrentIdSong());
		}

		//if clicked a item listview the music immediately playing 
		if(fromView==1){
			PlayMusicActivity.mServ.stopMusic();
			playingMedia();
		}

		imgvPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				playingMedia();
			}
		});

		imgvNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PlayMusicActivity.mServ.stopMusic();
				if(PlayMusicActivity.mServ.getCurrentPlayList()==null){
					PlayMusicActivity.mServ.setCurrentPlayListService(currentPlayList);
					PlayMusicActivity.mServ.setcurrentIdSong(0);
					mCallbacksControls.onChangeItem(PlayMusicActivity.mServ.getNameGenre(),0);
				}

				currentSong = PlayMusicActivity.mServ.nextMusic();
				if(flagSeekBar)
					mHandler.removeCallbacks(mUpdateTimeTask);
				updateProgressBar();
//	           	    imgvPlay.setBackgroundResource(R.drawable.pausa);
				imgvPlay.setImageResource(R.drawable.pausa);
				// Toast.makeText(getActivity(), "Genero next: "+PlayMusicActivity.mServ.getNameGenre(), Toast.LENGTH_SHORT).show();
				mCallbacksControls.onChangeItem(PlayMusicActivity.mServ.getNameGenre(), PlayMusicActivity.mServ.getcurrentIdSong());
				initPlaying=true;
				isPlaying=true;

				if (currentSong != null) {
					TextView textView_titulocancion = rootView.findViewById(R.id.textView_titulocancion);
					textView_titulocancion.setText(currentSong.Name);
//					textView_titulocancion.setTypeface(UtilsFonts.getTypefaceCategoryMusic(getContext()));

					TextView textView_autor = rootView.findViewById(R.id.textView_autor);
					textView_autor.setText(currentSong.Author);
//					textView_autor.setTypeface(UtilsFonts.getTypefaceCategoryMusic(getContext()));
				}
			}
		});

		imgvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlayMusicActivity.mServ.stopMusic();
				if(PlayMusicActivity.mServ.getCurrentPlayList()==null){
					PlayMusicActivity.mServ.setCurrentPlayListService(currentPlayList);
					PlayMusicActivity.mServ.setcurrentIdSong(0);
					mCallbacksControls.onChangeItem(genre,0);
				}


				currentSong = PlayMusicActivity.mServ.backMusic();
				if(flagSeekBar)
					mHandler.removeCallbacks(mUpdateTimeTask);
				updateProgressBar();
//           	    imgvPlay.setBackgroundResource(R.drawable.pausa);
				imgvPlay.setImageResource(R.drawable.pausa);
				initPlaying=true;
				isPlaying=true;
				mCallbacksControls.onChangeItem(genre, PlayMusicActivity.mServ.getcurrentIdSong());
				if (currentSong != null) {
					((TextView) rootView.findViewById(R.id.textView_titulocancion)).setText(currentSong.Name+" ");
					((TextView) rootView.findViewById(R.id.textView_autor)).setText(currentSong.Author);
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
			Log.e("ControlsMusicFragment", "Something is wrong with the volume: ", exception);
		}

		return rootView;
	}

	/**
	 * Start or resume a mp3 file.
	 */
	public void playingMedia(){

		if(!isPlaying){

//       	    imgvPlay.setBackgroundResource(R.drawable.pausa);
			imgvPlay.setImageResource(R.drawable.pausa);
			isPlaying=true;

			if(!PlayMusicActivity.mServ.isMediaPlayerPlaying() && !initPlaying){
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);
				updateProgressBar();
				PlayMusicActivity.mServ.setNameGenre(mSong.Genre);
				PlayMusicActivity.mServ.startMusic(currentPlayList,currentIdSong);
				mCallbacksControls.onChangeItem(genre, PlayMusicActivity.mServ.getcurrentIdSong());
				initPlaying=true;
			}else{
				PlayMusicActivity.mServ.resumeMusic(currentTimePosition);
			}
		}else{
			isPlaying=false;
//	       	 imgvPlay.setBackgroundResource(R.drawable.play);
			imgvPlay.setImageResource(R.drawable.play);
			currentTimePosition= PlayMusicActivity.mServ.pauseMusic();
		}
	}

	/**
	 *Update Progress Bar.
	 */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 500);
	}

	/**
	 *Set the time in the TextView and set the progressbar
	 */

	private final Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			flagSeekBar=true;
			//Log.i("Seekbar", "Run");
			long totalDuration = PlayMusicActivity.mServ.getTotalDuration();
			long currentDuration =  PlayMusicActivity.mServ.getCurrentTime();

			String auxTotal = ""+utils.milliSecondsToTimer(totalDuration);
			String auxCurrent = ""+utils.milliSecondsToTimer(currentDuration);
			// Displaying Total Duration time
			songTotalDurationLabel.setText(auxTotal);
			// Displaying time completed playing
			songCurrentDurationLabel.setText(auxCurrent);

			// Updating progress bar
			int progress = utils.getProgressPercentage(currentDuration, totalDuration);
			//Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 1000);
		}
	};

	@Override
	public void onStart() {
		getActivity().registerReceiver(mReceiver, intentFilter);
		if(flagSeekBar)
			updateProgressBar();

		if(PlayMusicActivity.mServ.isMediaPlayerPlaying()){

			currentSong= PlayMusicActivity.mServ.getCurrentSong();
//           	    imgvPlay.setBackgroundResource(R.drawable.pausa);
			imgvPlay.setImageResource(R.drawable.pausa);
			initPlaying=true;
			isPlaying=true;

			if(currentSong!=null){
				updateProgressBar();
				((TextView) rootView.findViewById(R.id.textView_titulocancion)).setText(currentSong.Name+" ");
				((TextView) rootView.findViewById(R.id.textView_autor)).setText(currentSong.Author);
			}

		}//else Toast.makeText(getActivity(), "NO Esta tocando", Toast.LENGTH_SHORT).show();
		super.onStart();
		Log.i("OnStart Frag Controls", "OnStart");

	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
		if(flagSeekBar)
			mHandler.removeCallbacks(mUpdateTimeTask);
		Log.i("OnStop Frag Controls", "OnStop");
	}

	@Override
	public void onPause(){
		//getActivity().unregisterReceiver(mReceiver);
		Log.i("OnPause Frag Controls", "OnPause");
		super.onPause();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = PlayMusicActivity.mServ.getTotalDuration();
		int currentPosition = UtilsMediaPlayer.progressToTimer(seekBar.getProgress(), totalDuration);
		PlayMusicActivity.mServ.resumeMusicSeekBar(currentPosition);
		updateProgressBar();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof CallChangeItemsListMsuic)) {
			throw new IllegalStateException("Error: La actividad debe implementar el callback del fragmento");
		}
		mCallbacksControls = (CallChangeItemsListMsuic) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacksControls = CallbacksVaciosControl;
	}
}


