package com.actia.audiolibros;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actia.mexico.launcher_t12_generico_2018.R;

/**
 * An adapter spinner to show genres
 */
public class SpinAdapterGenre extends ArrayAdapter<String>{

	public static String[] Genres;
	private final LayoutInflater inflater;
	private final ArrayList<String> colores;
	private int count=0;

	public SpinAdapterGenre(Context context, int resource, int textViewResourceId, String[] Genres, ArrayList<String> colores) {
		super(context, resource, textViewResourceId, Genres);
		SpinAdapterGenre.Genres=Genres;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.colores=colores;
	}
	
	@Override 
	public View getDropDownView(int position, View cnvtView, @NonNull ViewGroup prnt){
		return getCustomView(position, cnvtView, prnt);
		}
	
	@NonNull
	@Override public View getView(int pos, View cnvtView, @NonNull ViewGroup prnt){
		return getCustomView(pos, cnvtView, prnt);
		}
	
	private View getCustomView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_spinner_genre, null);
			vh.mText= convertView.findViewById(R.id.text_item_spinnes);
			vh.bg= convertView.findViewById(R.id.bg_item_spinner);
			convertView.setTag(vh);
		}else vh = (ViewHolder)convertView.getTag();

		vh.mText.setText(Genres[position]);
		if(count>(colores.size()-1))
			count=0;
		vh.bg.setBackgroundColor(Color.parseColor(colores.get(count++)));
		return convertView; 
		}	
	
	static class ViewHolder{
	    TextView mText;
	    LinearLayout bg;
	}
}
