package com.actia.utilities.utilities_order_arrays;

import com.actia.music.Song;

import java.util.Comparator;

/**
 * Created by Edgar Gonzalez on 09/05/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class SongComparator implements Comparator<Song> {
    @Override
    public int compare(Song songLeft, Song songRigth) {
        return songLeft.Name.compareTo(songRigth.Name);
    }
}
