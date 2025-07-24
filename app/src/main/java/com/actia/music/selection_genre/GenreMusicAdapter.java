package com.actia.music.selection_genre;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilsFonts;

/**
 *An Adapter to show the genres in a GridView.
 */
public class GenreMusicAdapter extends BaseAdapter {
	
	private final Context mContext;
//	static boolean flag_init=false;
//	boolean init=true;
//	int count=0;
	static ArrayList<String> allmThumbIds;
//	ArrayList<Integer> numbersRamdom;
	
    public GenreMusicAdapter(Context c, ArrayList<String> allmThumbIds) {
        mContext = c;
		//noinspection AccessStaticViaInstance
		this.allmThumbIds=allmThumbIds;
//        this.numbersRamdom=numbersRamdom;
    }

	public int getCount() {
        return allmThumbIds.size();
    }

    public Object getItem(int position) {
        return allmThumbIds.get(position);
    }

    public long getItemId(int position) {
    return position;
    }

	@SuppressLint({ "NewApi", "InflateParams" })
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View gridView;

			if (convertView == null) {
//				gridView = new View(mContext);
				gridView = inflater.inflate(R.layout.cell_music, null);
				TextView textView = gridView
						.findViewById(R.id.grid_item_text_music);
				textView.setGravity(Gravity.CENTER);
				textView.setPadding(0, 0, 0, 0);
//				int number=numbersRamdom.get(position);
				textView.setText(allmThumbIds.get(position));
//				textView.setTypeface(UtilsFonts.getTypefaceCategoryMusic(mContext));

				if(!allmThumbIds.get(position).equals("")){
					gridView.setBackgroundResource(R.drawable.bg_rectangle);
				}
				
				gridView.setId(position);
					
				if(position %2==1 ){
					textView.setTextSize(26);
				}
				
			} else {
				gridView= convertView;
			}
			return gridView;
    }

}

