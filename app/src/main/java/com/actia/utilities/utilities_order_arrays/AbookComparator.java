package com.actia.utilities.utilities_order_arrays;

import com.actia.audiolibros.AudioBook;

import java.util.Comparator;

/**
 * Created by Edgar Gonzalez on 16/08/2018.
 * Actia de Mexico S.A. de C.V..
 */
public class AbookComparator implements Comparator<AudioBook>{
    @Override
    public int compare(AudioBook abookLeft, AudioBook abookRigth) {
        return abookLeft.getName().compareTo(abookRigth.getName());
    }
}
