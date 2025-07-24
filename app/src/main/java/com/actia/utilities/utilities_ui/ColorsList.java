package com.actia.utilities.utilities_ui;

import java.util.ArrayList;

/**
 * Store all colors used in the mediaplayer's listview.
 */
public class ColorsList {

	public ColorsList(){  
	}
	
	/**
	 * Get color to general media player
	 * @return ArrayList with colors
	 */
	public ArrayList<String> getColorsGeneral(){
		ArrayList<String> colorGeneral= new ArrayList<>();
		colorGeneral.add("#1A9CBA");
		colorGeneral.add("#26A4C6");
		colorGeneral.add("#36ADD6");
		colorGeneral.add("#49B7E8");
		colorGeneral.add("#60BFF4");
		colorGeneral.add("#49B7E8");
		colorGeneral.add("#36ADD6");
		colorGeneral.add("#26A4C6");
		colorGeneral.add("#1A9CBA");
		colorGeneral.add("#1193A8"); 
		return colorGeneral;	
	}
	
	/**
	 * Get colors to movie spinner.
	 * @return ArrayList with colors.
	 */
	public ArrayList<String> getColorsSpinnerMovie(){
		ArrayList<String> colorSpinner = new ArrayList<>();
		colorSpinner.add("#16D683");
		colorSpinner.add("#1AC678");
		colorSpinner.add("#18B56A");
		colorSpinner.add("#16A35C");
		colorSpinner.add("#18B56A");
		colorSpinner.add("#1AC678");
		colorSpinner.add("#16D683");
		colorSpinner.add("#1AE290");
		return colorSpinner;	
	}
	
	/**
	 * Get colors to  audio book spinner.
	 * @return ArrayList with colors.
	 */
	public ArrayList<String> getColorsSpinnerABook(){
		ArrayList<String> colorSpinner = new ArrayList<>();
		colorSpinner.add("#E83A3A");
		colorSpinner.add("#D62A2A");
		colorSpinner.add("#C11E1E");
		colorSpinner.add("#AA1212");
		colorSpinner.add("#C11E1E");
		colorSpinner.add("#D62A2A");
		colorSpinner.add("#E83A3A");
		colorSpinner.add("#ED5A5A");
		return colorSpinner;	
	}
	
	/**
	 * Get colors to listview music children
	 * @return ArrayList with color
	 */
	public ArrayList<String> getColorsMusicChildrens(){
		ArrayList<String> colorMusicChildrens = new ArrayList<>();
		colorMusicChildrens.add("#D12626");
		colorMusicChildrens.add("#F98732");
		colorMusicChildrens.add("#F4AE00");
		colorMusicChildrens.add("#1DDD6B");
		colorMusicChildrens.add("#60BFF4");
		colorMusicChildrens.add("#8677E0");
		colorMusicChildrens.add("#60BFF4");
		colorMusicChildrens.add("#1DDD6B");
		colorMusicChildrens.add("#F4AE00");
		colorMusicChildrens.add("#F98732"); 
		return colorMusicChildrens;	
		
		
	}
}
