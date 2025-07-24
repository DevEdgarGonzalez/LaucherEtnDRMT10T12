package com.actia.audiolibros;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AudioBook implements Parcelable, Serializable {
	int id;
	private final String name;
	private final String path;
	private final String genre;
	private final boolean isAudioBook;
	private final String pathText;
	private final String pathImage;
	
    public AudioBook(int id,String name,String path,String genre,String pathText,String pathImage,boolean isAudioBook){
		this.id=id;
		this.name=name;
		this.path=path;
		this.genre=genre;
		this.pathText=pathText;
		this.pathImage=pathImage;
		this.isAudioBook=isAudioBook;
	}

	/**Get id
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get audio book name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get path
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get genre name
	 * @return genre name
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * check if it's audio books
	 * @return true if it's audio book
	 */
	public boolean isAudioBook() {
		return isAudioBook;
	}

	/**
	 * Get synopsis text
	 * @return synopsis
	 */
	public String getPathText() {
		return pathText;
	}
	
/**
 * Get Image path of an audio book
 * @return path
 */
	public String getPathImage() {
		return pathImage;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.name);
		dest.writeString(this.path);
		dest.writeString(this.genre);
		dest.writeByte(this.isAudioBook ? (byte) 1 : (byte) 0);
		dest.writeString(this.pathText);
		dest.writeString(this.pathImage);
	}

	protected AudioBook(Parcel in) {
		this.id = in.readInt();
		this.name = in.readString();
		this.path = in.readString();
		this.genre = in.readString();
		this.isAudioBook = in.readByte() != 0;
		this.pathText = in.readString();
		this.pathImage = in.readString();
	}

	public static final Creator<AudioBook> CREATOR = new Creator<AudioBook>() {
		@Override
		public AudioBook createFromParcel(Parcel source) {
			return new AudioBook(source);
		}

		@Override
		public AudioBook[] newArray(int size) {
			return new AudioBook[size];
		}
	};
}
