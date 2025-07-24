package com.actia.utilities.utilities_external_storage;

import android.media.MediaMetadataRetriever;

import java.io.IOException;

public class MetaTag {
	public MetaTag(){
		
	}
	
	/**
	 * Get author tag
	 * @param path path file
	 * @return author
	 */
	public String getAuthorTag(String path) throws IOException {
		String Author;
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path); 
		Author=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
	    mmr.release();
		if(Author!=null)
		return Author;
		else return "<unknown>";
	}
	
	/**
	 * Get DUration
	 * @param path path file
	 * @return duration
	 */
	public String getDurationTag(String path) throws IOException {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		String TagDuration =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
	    if(TagDuration!=null){
			long dur = Long.parseLong(TagDuration);
		    String seconds = String.valueOf((dur % 60000) / 1000);
		    String minutes = String.valueOf(dur / 60000);
		    mmr.release();
		    if (seconds.length() == 1) {
		        return "0" + minutes + ":0" + seconds;
		    }else {
		        return "0" + minutes + ":" + seconds;
		    }
	    }else return "<unknown>";
	}
	
	/**
	 * Check if file has audio
	 * @param path path file
	 * @return true if it has audio
	 */
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	public boolean hasAudio(String path){
//		String state;
//		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//		mmr.setDataSource(path);
//		state=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
//	    mmr.release();
//		if(state!=null)
//			 return true;
//		else return false;
//	}
	
	/**
	 * Check if file has audio
	 * @param path path file
	 * @return true if it has video
	 */
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	public boolean hasVideo(String path){
//		String state;
//		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//		mmr.setDataSource(path);
//		state=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
//	    mmr.release();
//		if(state!=null)
//			 return true;
//		else{
//			Log.e("MetaTag Music", "Archivo sin audio");
//			return false;
//		}
//	}

}
