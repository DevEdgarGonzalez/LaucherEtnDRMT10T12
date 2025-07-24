package com.actia.random_movies;

import android.content.Context;
import android.net.Uri;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.utilities.UtilitiesComunicationServiceActia;
import com.actia.utilities.utilities_media_player.UtilitiesGeneralMediaPlayer;

import java.io.File;
import java.util.ArrayList;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * Created by Edgar Gonzalez on 23/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class RandomMoviewsAdapter extends PagerAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<Movie> mResources;
    private     OnSelectedListener mListener;

    public interface OnSelectedListener {
        void onSelected(Movie gen);
    }

    public RandomMoviewsAdapter(Context mContext, ArrayList<Movie> mResources) {
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        try {
            mListener = (OnSelectedListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement MoviePagerDialogAdapter");
        }

        View itemView = mLayoutInflater.inflate(R.layout.popup_random_movies, container, false);

        ImageView imgvCoverPopUpRandom = itemView.findViewById(R.id.imgvCoverPopUpRandom);
        JustifyTextView txtvTitlePopUpRandom = itemView.findViewById(R.id.txtvDescPopUpRandom);
        Button btnicoPopUpRandom = itemView.findViewById(R.id.btnicoPopUpRandom);
        Button btnPlayPopUpRandom = itemView.findViewById(R.id.btnPlayPopUpRandom);

        btnicoPopUpRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelected(mResources.get(position));
                UtilitiesComunicationServiceActia.sendLogBroadcast(mContext);
                UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(mContext);
            }
        });

        btnPlayPopUpRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelected(mResources.get(position));
                UtilitiesComunicationServiceActia.sendLogBroadcast(mContext);
                UtilitiesGeneralMediaPlayer.stopMyMediaPlayerService(mContext);
            }
        });

        if(mResources.get(position).image != null){
            File file = new File(mResources.get(position).image);
            if(file.exists()) {
                Uri mUri = Uri.parse(mResources.get(position).image);
                imgvCoverPopUpRandom.setImageURI(mUri);
            }else
                imgvCoverPopUpRandom.setImageResource(R.drawable.album_default);

        }else{
            imgvCoverPopUpRandom.setImageResource(R.drawable.album_default);
        }
        txtvTitlePopUpRandom.setText(mResources.get(position).textFile+"\n\n");

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    
}
