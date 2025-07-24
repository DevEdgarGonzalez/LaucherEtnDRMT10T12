package com.actia.music.selection_genre;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
/**
 * Load Genre asynchronously
 * @see GenreMusicAdapter
 */
public class GenreMusicTsk extends AsyncTask<Void, Void,GenreMusicAdapter>{
	
	private final OnCompleteLoadGenre interf;
	private final Context c;
	private final ArrayList<String> allmThumbIds;

	public interface OnCompleteLoadGenre {
		void OnCompleteLoadGenreListener(GenreMusicAdapter adapter);
	}
//	ArrayList<Integer> numbersRamdom;
	public GenreMusicTsk(OnCompleteLoadGenre interf, Context c, ArrayList<String> allmThumbIds){
		this.interf=interf;
		this.c=c;
		this.allmThumbIds=allmThumbIds;
//		this.numbersRamdom=numbersRamdom;
	}
	
	 @Override
	 protected void onPreExecute() {
	     super.onPreExecute();
	 }
	
	 @Override
	protected GenreMusicAdapter doInBackground(Void... params) {

		 return new GenreMusicAdapter(c,allmThumbIds);
	}
	 
	@Override
	protected void onPostExecute(GenreMusicAdapter result) {
	super.onPostExecute(result);
		interf.OnCompleteLoadGenreListener(result);
	}

}
