package com.actia.peliculas;
/**
 *Object with all characteristics of the movie file
 */
public class Movie {
	public int id;
	public String name;
	public String path;
	public String genre;
	public boolean isMovie;
	public boolean isDRM;
	public String textFile;
	public String image;

	/**
	 *Object with all characteristics of the movie file
	 *@param id id movie
	 *@param name movie name
	 *@param path movie path
	 *@param genre movie genre
	 *@param textFile A text file with a synopsis.
	 *@param image movie image
	 *@param isMovie if the item at the Gridview is a Movie.
	 */
	public Movie(int id,String name,String path,String genre,String textFile,String image,boolean isMovie, boolean isDRM){
		this.id=id;
		this.name=name;
		this.path=path;
		this.genre=genre;
		this.textFile=textFile;
		this.image=image;
		this.isMovie=isMovie;
        this.isDRM = isDRM;
	}

	@Override
	public String toString() {
		return name + '\'' +
				", genre='" + genre + '\''
				;
	}
}
