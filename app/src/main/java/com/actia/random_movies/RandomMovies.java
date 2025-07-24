package com.actia.random_movies;

import android.content.Context;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.peliculas.Movie;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Edgar Gonzalez on 23/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class RandomMovies {
    private final ArrayList<File> alistMovies;

    public RandomMovies() {
        alistMovies =  new ArrayList<>();
    }


    public ArrayList<Movie> getRandomAllGenres(Context context){
        ArrayList<Movie> result = new ArrayList<Movie>();
        String[] dirs;
        ReadFileExternalStorage readFileExternalStorage = new ReadFileExternalStorage();


        //get genres
        dirs = readFileExternalStorage.getGenresMovie(false);

        for(String dir: dirs) {
            ArrayList<Movie> sublistMovies = readFileExternalStorage.getMovieByGenreByPath(ConfigMasterMGC.getConfigSingleton().getVIDEO_PATH(), dir, context);
            if (sublistMovies!=null && sublistMovies.size()!=0){
                result.addAll(sublistMovies);

            }
        }


        //ChildrenMovies
        ArrayList<Movie> moviesChildren = getMoviesChildren();
        if (moviesChildren!=null && moviesChildren.size()>0){
            result.addAll(moviesChildren);
        }


//		}

        Collections.shuffle(result);
        return result;
    }


    public ArrayList<Movie> getMoviesChildren(){
        ArrayList<Movie> alistRandomMovie = null;
        ReadFileExternalStorage readFileExternalStorage = new ReadFileExternalStorage();
        alistRandomMovie = readFileExternalStorage.getMovieChildrenByGenre();
        Collections.shuffle(alistRandomMovie);
        return alistRandomMovie;
    }



    public ArrayList<Movie> getRandomMoviesByRootPath(Context context,String pathRoot, boolean includeChildren){
        ArrayList<Movie> result = new ArrayList<Movie>();
        String[] dirs;
        ReadFileExternalStorage readFileExternalStorage = new ReadFileExternalStorage();


        //AllMovies
        dirs = readFileExternalStorage.getGenresMovieByPath(new File(pathRoot));

        for(String dir: dirs) {
            ArrayList<Movie> sublistMovies = readFileExternalStorage.getMovieByGenreByPath(pathRoot, dir, context);
            if (sublistMovies!=null && sublistMovies.size()!=0){
                result.addAll(sublistMovies);

            }
        }

        //ChildrenMovies
        if (includeChildren){
            ArrayList<Movie> moviesChildren = getMoviesChildren();
            if (moviesChildren!=null && moviesChildren.size()>0){
                result.addAll(moviesChildren);
            }
        }

        Collections.shuffle(result);
        return result;
    }

    private void walk(String path) {

        File root = new File( path );
        File[] list = root.listFiles(new FileExtensionFilterVideo());

        if (list == null) return;

        for ( File f : list ) {
            if (f.isDirectory()) {
                walk( f.getAbsolutePath() );
                System.out.println( "Dir:" + f.getAbsoluteFile());
            }else {
                System.out.println( "File:" + f.getAbsoluteFile() );
                alistMovies.add(f);
            }
        }
    }
}
