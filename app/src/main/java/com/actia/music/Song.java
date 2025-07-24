package com.actia.music;

public class Song {
	
	public int id;
	public String Name; 
	public String Author;
	public String Time;
	public String Path;
	public String Genre;
	  
	/**
	 * A song
	 * @param countSong index
	 * @param Name song name
	 * @param Path song path
	 * @param Author author
	 * @param Time total time
	 * @param Genre genre
	 */
	public Song (int countSong, String Name,String Path, String Author,String Time,String Genre) { 
		this.id = countSong;
	    this.Name = Name; 
	    this.Author = Author;
	    this.Time = Time; 
	    this.Genre=Genre;
	    this.Path=Path;
	}
}
