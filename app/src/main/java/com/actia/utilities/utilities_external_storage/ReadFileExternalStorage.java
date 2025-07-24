package com.actia.utilities.utilities_external_storage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.actia.audiolibros.AudioBook;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.music.Song;
import com.actia.peliculas.Movie;
import com.actia.utilities.utilities_file.FileExtensionFilterForFile;
import com.actia.utilities.utilities_file.FileExtensionFilterMusic;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utils_language.UtilsLanguage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is necessary to read all files from the external sdcard.
 */
public class ReadFileExternalStorage {

    public ReadFileExternalStorage() {
    }

    /**
     * Get the device configuration
     *
     * @param doc A xml document
     * @return An array string with iddevice and idbus
     */
    public String[] getConfig(Document doc) {
        NodeList nl = doc.getElementsByTagName("tactil");
        if (nl.getLength() > 0) {
            Element e = (Element) nl.item(0);
            if (e.getElementsByTagName("device_id").getLength() > 0) {
                NodeList nodo = e.getElementsByTagName("device_id");
                Element i = (Element) nodo.item(0);
                String text = i.getTextContent();
                if (text != null && !text.equals("") && !text.isEmpty() && text.length() > 0) {
                    String idBus = text.substring(text.indexOf("U") + 1, text.indexOf("A"));
                    String idDevice = text.substring(text.indexOf("A") + 1);
                    if (idBus != null && !idBus.equals("") && !idBus.isEmpty() && idBus.length() > 0 && !idBus.equals("00000") &&
                            idDevice != null && !idDevice.equals("") && !idDevice.isEmpty() && idDevice.length() > 0 && !idDevice.equals("00")) {
                        String[] result = new String[2];
                        result[0] = idBus;
                        result[1] = idDevice;
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get music genres from the external sdcard.
     *
     * @return array string with genres.
     */
    public String[] getAllGenre() {
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        File home = new File(configSingleton.getMUSIC_PATH());
        ArrayList<String> Genre = new ArrayList<>();
        File[] files = home.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isDirectory() && file.listFiles(new FileExtensionFilterMusic()).length > 0) {
                    Genre.add(file.getName());
                }
            }
        }
        return Genre.toArray(new String[Genre.size()]);
    }

    public String[] getGenresByName(String category) {

        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        File home = new File(configSingleton.getVIDEO_PATH());
        ArrayList<String> Genre = new ArrayList<String>();
        File[] files = home.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isDirectory() && (file.listFiles(new FileExtensionFilterVideo()).length > 0)) {
                    if (file.getName().equals(category) || file.getName().equals(category) || file.getName().equals(category) || file.getName().equals(category))
                        Genre.add(file.getName());
                }
            }
        }
        return Genre.toArray(new String[Genre.size()]);
    }

    /**
     * Get movies genres
     *
     * @return An array string with genres
     */
    public String[] getGenresMovie(boolean isGenreMoviesActivity) {

        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        File home = new File(configSingleton.getVIDEO_PATH());
        ArrayList<String> Genre = new ArrayList<String>();
        File[] files = home.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {

                    if (file.listFiles(new FileExtensionFilterVideo()).length > 0) {
                        Genre.add(file.getName());
                    } else {

                        File[] subFilesDir = file.listFiles(new FileExtensionFilterForFile());
                        if (subFilesDir != null && subFilesDir.length > 0) {
                            for (File dirSubFile : subFilesDir) {
                                if (dirSubFile.listFiles(new FileExtensionFilterVideo()).length > 0) {
                                    Genre.add(file.getName());
                                    break;
                                }
                            }

                        }
                    }


                }
            }
        }

        if (ConstantsApp.addChildrenInMoviesGnres && isGenreMoviesActivity) {
            String nameChildren = getNameCategoryChildrens();
            if (nameChildren != null && !nameChildren.equals("")) {
                Genre.add(nameChildren);
            }

        }

        return Genre.toArray(new String[Genre.size()]);
    }

    /**
     * obtiene los generos (dirs) que se encuentran en una ruta
     * <p>
     * para tomar en cuenta si es genero es necesario que sea un Dir y que tenga archivos de video
     *
     * @param path
     * @return
     */
    public String[] getGenresMovieByPath(File path) {

        ArrayList<String> Genre = new ArrayList<>();
        File[] files = path.listFiles();

        if (files == null || files.length == 0) return Genre.toArray(new String[Genre.size()]);


        for (File genreDir : files) {

            if (genreDir.isDirectory()){
                if (genreDir.listFiles(new FileExtensionFilterVideo()).length > 0){
                    Genre.add(genreDir.getName());
                }else if (genreDir.listFiles(new FileExtensionFilterForFile()).length > 0){
                    Genre.add(genreDir.getName());
                }

            }

        }

        return Genre.toArray(new String[Genre.size()]);
    }


    /**
     * Get songs for children
     *
     * @return An arrayList with Song objects.
     */
    public ArrayList<Song> getSongsChildrens() throws IOException {
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        int countSong = 0;
        MetaTag tags = new MetaTag();
        ArrayList<Song> Songs = null;
        File home = new File(configSingleton.getMUSIC_CHILDREN_PATH());
        if (home.listFiles(new FileExtensionFilterMusic()).length > 0) {
            Songs = new ArrayList<>();
            for (File file : home.listFiles(new FileExtensionFilterMusic())) {
                if (file.length() > 0 && file.canRead()) {
                    Songs.add(new Song(countSong++,
                            file.getName().substring(0, (file.getName().length() - 4)),
                            file.getPath(),
                            tags.getAuthorTag(file.getPath()),
                            tags.getDurationTag(file.getPath()),
                            null)
                    );
                }
            }
        }

        return Songs;
    }

    /**
     * Get song by Genre
     *
     * @param Genre Genre name
     * @return An arrayList with Song objects.
     */
    public ArrayList<Song> getSongsByGenre(String Genre) throws IOException {
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        int countSong = 0;
        MetaTag tags = new MetaTag();
        ArrayList<Song> Songs = null;
        File home = new File(configSingleton.getMUSIC_PATH() + "/" + Genre);
        if (home.listFiles(new FileExtensionFilterMusic()).length > 0) {
            Songs = new ArrayList<>();
            for (File file : home.listFiles(new FileExtensionFilterMusic())) {
                if (file.length() > 0 && file.canRead())
                    Songs.add(new Song(countSong++,
                            file.getName().substring(0, (file.getName().length() - 4)),
                            file.getPath(),
                            tags.getAuthorTag(file.getPath()),
                            tags.getDurationTag(file.getPath()),
                            Genre)
                    );
            }
        }

        return Songs;
    }

    /**
     * Get movies genre
     *
     * @param Genre genre name
     * @return An arrayList with Movie objects.
     */
    public ArrayList<Movie> getMovieByGenre(String Genre) {
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        int countMovie = 0;
        ArrayList<Movie> Movies = null;
        String pathGenre = configSingleton.getVIDEO_PATH() + "/" + Genre;
        String nameFile;
        File home = new File(pathGenre);
        if (home.listFiles(new FileExtensionFilterVideo()).length > 0) {
            Movies = new ArrayList<>();
            for (File file : home.listFiles(new FileExtensionFilterVideo())) {
                if (file.length() > 0 && file.canRead()) {
                    boolean isDRM;
                    String nameFileExt = file.getName();
                    String extension = nameFileExt.substring(nameFileExt.lastIndexOf(".") + 1);
                    nameFile = file.getName().substring(0, file.getName().lastIndexOf('.'));

                    isDRM = extension.equals("mlv") || extension.equals("MLV");

                    Movies.add(new Movie(countMovie++,
                            nameFile,
                            file.getPath(),
                            Genre,
                            UtilitiesFile.getTextFileMovie(pathGenre + "/" + nameFile + ".txt"),
                            getImageMovie(pathGenre + "/" + nameFile),
                            true, isDRM)
                    );
                }
            }
        }

        return Movies;
    }

    public ArrayList<Movie> getMovieByGenreByPath(String path, String Genre, Context context) {
        int countMovie = 0;
        ArrayList<Movie> Movies = new ArrayList<Movie>();
        String pathGenre = path + "/" + Genre;
        String nameFile;
        String duration;
        File home = new File(pathGenre);
        File[] allSubCategories = home.listFiles(new FileExtensionFilterForFile());

        if (allSubCategories != null && allSubCategories.length > 0) {
            for (File subcategory : allSubCategories) {
                Movies.addAll(getMovieByGenreByPath(subcategory.getParent(), subcategory.getName(), context));
            }

        } else if (home.listFiles(UtilsLanguage.getFileExtensionMovieByLanguage(home)).length > 0) {
            for (File file : home.listFiles(UtilsLanguage.getFileExtensionMovieByLanguage(home))) {
                if (file.length() > 0 && file.canRead()) {
                    boolean isDRM;
                    String nameFileExt = file.getName();
                    String extension = nameFileExt.substring(nameFileExt.lastIndexOf(".") + 1);
                    nameFile = file.getName().substring(0, file.getName().lastIndexOf('.'));

                    isDRM = extension.equals("mlv") || extension.equals("MLV");

                    Movies.add(new Movie(countMovie++,
                            nameFile,
                            file.getPath(),
                            Genre,
                            UtilitiesFile.getTextFileMovie(pathGenre + "/" + nameFile + ".txt"),
                            getImageMovie(pathGenre + "/" + nameFile),
                            true, isDRM)
                    );
                }
            }
        }

        return Movies;
    }

    /**
     * Get movie genre for kids
     *
     * @return An arrayList with Movie objects.
     */
    public ArrayList<Movie> getMovieChildrenByGenre() {
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        int countMovie = 0;
        ArrayList<Movie> Movies = null;
        String pathGenre = configSingleton.getVIDEO_CHILDREN_PATH();
        String nameFile;
        File home = new File(pathGenre);
        File[] files = home.listFiles(UtilsLanguage.getFileExtensionMovieByLanguage(home));
        if (files != null && files.length > 0) {
            Movies = new ArrayList<>();
            for (File file : files) {
                if (file.length() > 0 && file.canRead()) {
                    boolean isDRM;
                    nameFile = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    String nameFileExt = file.getName();
                    String extension = nameFileExt.substring(nameFileExt.lastIndexOf(".") + 1);

                    isDRM = extension.equals("mlv") || extension.equals("MLV");

                    Movies.add(new Movie(countMovie++,
                            nameFile,
                            file.getPath(),
                            null,
                            UtilitiesFile.getTextFileMovie(pathGenre + "/" + nameFile + ".txt"),
                            getImageMovie(pathGenre + "/" + nameFile),
                            true, isDRM)
                    );
                }
            }
        }

        return Movies;
    }

    /**
     * Get audio books from the external sdcard
     *
     * @param pathDir    audio books path
     * @param isChildren true if is a kids directory
     * @return An arrayList with AudioBook objects.
     */
    public ArrayList<AudioBook> getAudioBooks(String pathDir, boolean isChildren) {
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();
        int countAudioBook = 0;
        String pathParent;
//        pathParent="";
        ArrayList<AudioBook> AudioBooks = null;
        if (isChildren)
            pathParent = pathDir;
        else pathParent = configSingleton.getAUDIOBOOKS_PATH() + "/" + pathDir;
        Log.i("ReadFileExternal", pathParent);
        String pathGenre = pathParent;
        String nameFile;
        File home = new File(pathGenre);
        if (home.exists() && home.listFiles(new FileExtensionFilterMusic()).length > 0) {
            AudioBooks = new ArrayList<>();
            for (File file : home.listFiles(new FileExtensionFilterMusic())) {
                if (file.length() > 0 && file.canRead()) {
                    nameFile = file.getName().substring(0, (file.getName().length() - 4));
                    AudioBooks.add(new AudioBook(countAudioBook++,
                            nameFile,
                            file.getPath(),
                            null,
                            pathGenre + "/" + nameFile + ".txt",
                            getImageMovie(pathGenre + "/" + nameFile),
                            true)
                    );
                }
            }
        }
        return AudioBooks;
    }


    public ArrayList<AudioBook> getAudioBooksByPath(String pathDir) {
        int countAudioBook = 0;
        ArrayList<AudioBook> AudioBooks = null;
        Log.i("ReadFileExternal", pathDir);
        String nameFile = null;
        File home = new File(pathDir);
        if (home.exists() && home.listFiles(new FileExtensionFilterMusic()).length > 0) {
            AudioBooks = new ArrayList<AudioBook>();
            for (File file : home.listFiles(new FileExtensionFilterMusic())) {
                if (file.length() > 0 && file.canRead()) {
                    nameFile = file.getName().substring(0, (file.getName().length() - 4));
                    AudioBooks.add(new AudioBook(countAudioBook++,
                            nameFile,
                            file.getPath(),
                            null,
                            pathDir + "/" + nameFile + ".txt",
                            getImageMovie(pathDir + "/" + nameFile),
                            true)
                    );
                }
            }
        }
        return AudioBooks;
    }

	/*public String getImageMovie(String name){
		File file=new File(name+".jpg");
		if(file.exists() && file.isFile())
			return file.getPath();
		else{ 
			file=new File(name+".png");
			if(file.exists() && file.isFile())
				return file.getPath();
		}
		return null;
	}*/

    /**
     * Getimage movie
     *
     * @param name image path without extension.
     * @return image path completed.
     */
    private String getImageMovie(String name) {
        File file1 = new File(name + ".jpg");
        File file2 = new File(name + ".JPG");
        File file3 = new File(name + ".png");
        File file4 = new File(name + ".PNG");

        if (file1.exists() && file1.isFile())
            return file1.getPath();
        else if (file2.exists() && file2.isFile())
            return file2.getPath();
        else if (file3.exists() && file3.isFile())
            return file3.getPath();
        else if (file4.exists() && file4.isFile())
            return file4.getPath();
        else
            return null;
    }

    /**
     * check if the directory exist and it´s not empty
     *
     * @return true if exist and it´s not empty
     */
    public boolean existDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory() && file.list().length > 0 && file.canRead();
    }

    private String getNameCategoryChildrens() {
        File fileMoviesChildren = new File(new ConfigMasterMGC().VIDEO_CHILDREN_PATH);
        if (!fileMoviesChildren.exists()) {
            return null;

        }
        File[] filesMoviesChildren = fileMoviesChildren.listFiles();
        for (File fileInChildrenMovies : filesMoviesChildren) {
            if (ConstantsApp.nameCategoryChildren != null && fileInChildrenMovies.getName().contains(ConstantsApp.nameCategoryChildren)) {
                if (fileInChildrenMovies.getName().contains(".")) {
                    String[] hg = fileInChildrenMovies.getName().split("\\.");
                    return hg[0];
                } else {
                    return fileInChildrenMovies.getName();
                }
            }
        }
        return null;
    }
}
