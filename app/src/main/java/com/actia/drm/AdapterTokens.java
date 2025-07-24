package com.actia.drm;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.File;

class AdapterTokens extends ArrayAdapter<File> {

	private File[] tokens=null;
	 private final LayoutInflater mInflater;
	 private SparseBooleanArray mSelectedItemsIds;
	
	 AdapterTokens(Context mContext, int resourceId, File[] tokens) {
         super(mContext,  resourceId, tokens);
         
         this.mSelectedItemsIds = new SparseBooleanArray();
		 this.tokens=tokens;
 		 mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		 //mInflater = LayoutInflater.from(mContext);
    } 
	 
	/*public AdapterTokens(Context mContext,File[] tokens){
		
		this.mContext=mContext;
		this.tokens=tokens;
		mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSelectedItemsIds = new SparseBooleanArray();
	}*/

	@Override
	public int getCount() {
		if(this.tokens.length>0)
			return this.tokens.length;
		return 0;
	}

	@Override
	public File getItem(int position) {
		return this.tokens[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = mInflater.inflate(R.layout.cell_list_token, null);
			vh.name_token= convertView.findViewById(R.id.txt_token);
	        convertView.setTag(vh);
		}else vh= (ViewHolder)convertView.getTag(); 
		
		 convertView.setId(position);
		 vh.name_token.setText(this.tokens[position].getName());
		 
        return convertView;
	}
	
	public static class ViewHolder{
	    TextView name_token;
	}
	
 
    void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
 
    private void selectView(int position, boolean value) {
        if (value)
			//noinspection ConstantConditions
			mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
 
    SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    void removeSelection() {
    	mSelectedItemsIds=null;
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}
    
//    public int getSelectedCount() {
//		return mSelectedItemsIds.size();
//	}

}
