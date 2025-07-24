package com.actia.utilities.utilities_order_arrays;

import com.actia.peliculas.Movie;

import java.util.Comparator;

/**
 * Created by Edgar Gonzalez on 09/05/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class MovieComparator implements Comparator<Movie> {
    @Override
    public int compare(Movie movieLeft, Movie movieRigth) {
        return movieLeft.name.compareTo(movieRigth.name);
    }
}
