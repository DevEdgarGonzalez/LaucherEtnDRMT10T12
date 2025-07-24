package com.actia.music_ninos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.Song;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 28/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public abstract class MusicNinosAdapter extends BaseAdapter {

    private final Context context;
    public ArrayList<Song> alistSongs=null;

    public MusicNinosAdapter(Context context, ArrayList<Song> alistSongs) {
        this.context = context;
        this.alistSongs = alistSongs;
    }

    @Override
    public int getCount() {
        return alistSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return alistSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View List;

        if(convertView==null){
//			List = new View(context);
            List = inflater.inflate(R.layout.item_song_child, null);

        }else List= convertView;

        onEntrada (alistSongs.get(position), List);
        return List;
    }

    public abstract void onEntrada (Song song, View view);

}
