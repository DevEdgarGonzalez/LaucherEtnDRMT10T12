package com.actia.utilities.utils_language;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.utilities.utilities_file.FileExtensionFilterImages_en;
import com.actia.utilities.utilities_file.FileExtensionFilterImages_es;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo_en;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo_es;
import com.actia.utilities.utilities_file.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class UtilsLanguage {

    private static final String PREFIX_SPANISH = "_es";
    private static final String PREFIX_ENGLISH = "_en";


    public static void setLanguage(String language, Context context) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }

    /**
     * Este metodo toma el nombre del directorio dependiendo del idioma seleccionado
     * se separan con "_"
     *
     * @param name nombre del directorio
     * @return parte del nombre del directorio en su respectivo idioma
     */
    public static String getName(String name) {

        if (!name.contains("_")) {
            return name;
        }

        String[] s = name.split("_");
        if (s.length >= 2) {

            if (isAppInEnglish()) {
                return s[ConstantsApp.LANGUAGE_NAMEDIR_POSITION_EN];
            } else {
                return s[ConstantsApp.LANGUAGE_NAMEDIR_POSITION_ES];
            }
        } else {
            return name;
        }
    }


    public static Boolean isAppInEnglish() {
        Locale locale = Locale.getDefault();

        return locale.getLanguage().contains(Locale.ENGLISH.getLanguage());

    }


    public static FilenameFilter getFileExtensionImageByLanguage(File rootPath) {
        if (ConstantsApp.MULTILANGUAGE_ENABLE) {
            if (UtilsLanguage.isAppInEnglish()) {
                if (rootPath.listFiles(new FileExtensionFilterImages_en()).length > 0) {
                    return new FileExtensionFilterImages_en();
                }

            }


        }

        return new FileExtensionFilterImages_es();

    }

    /**
     * obtiene el filtro dependiendo del lenguaje seleccionado
     *
     * @return filtro para peliculas en ingles o español
     */
    public static FilenameFilter getFileExtensionMovieByLanguage(File rootPath) {
        if (ConstantsApp.MULTILANGUAGE_ENABLE) {
            if (UtilsLanguage.isAppInEnglish()) {

                if (rootPath.listFiles(new FileExtensionFilterVideo_en()).length > 0) {
                    return new FileExtensionFilterVideo_en();
                }
            }
        }

        return new FileExtensionFilterVideo_es();
    }

    /**
     * Obtiene el nombre de una imagen dependiendo del idioma seleccionado
     *
     * @param rootPath      path root
     * @param nameFile      nombre del archivo
     * @param extensionFile indicar si es png, jpg, mp4, etc
     * @return en caso de ser imagen en ingles y no existir regresara en español
     */
    public static File getFileImgByLanguage(String rootPath, String nameFile, String extensionFile) {
        if (isAppInEnglish()) {
            File fileEnglish = new File(rootPath, nameFile + PREFIX_ENGLISH + extensionFile);
            if (fileEnglish.exists()) {
                return fileEnglish;
            }
        }

        File fileSpanish = new File(rootPath, nameFile + PREFIX_SPANISH + extensionFile);
        return fileSpanish;

    }

    public static File getPathByNormalPath(String path) {

        if (path == null) return new File("");

        File file = new File(path);
        String root = file.getParent();
        String nameWithExtension = file.getName();
        String onlyname = FileUtils.removeExtension(nameWithExtension);

        String extension = FileUtils.getExtensionFromFilePath(nameWithExtension);
        if (!extension.contains(".")) {
            extension = "." + extension;
        }

        return getFileImgByLanguage(root, onlyname, extension);
    }

    public static File getPathImgCategoryByLanguage(ItemsHome category) {
        if (isAppInEnglish()) {
            if (category.pathImg_en != null && !category.pathImg_en.isEmpty()) {
                File file = new File(category.pathImg_en);

                if (file.exists()) {
                    return file;
                }
            }

        }


        if (category.pathImg != null) {
            return new File(category.pathImg);
        } else {
            return null;
        }

    }


    //    private static String getPathImgByLanguage(ItemsHome itemsHome, boolean isAppInEnglish){
    private static String getFileImgByLanguage(ItemsHome itemsHome) {
        if (isAppInEnglish()) {
            return itemsHome.getPathImg_en();
        } else {
            return itemsHome.getPathImg();
        }

    }

    public static String getTitleCategoryByLanguage(ItemsHome itemsHome) {
        if (isAppInEnglish()) {
            return itemsHome.getTitle_en();
        } else {
            return itemsHome.getTitle();
        }

    }

    private static String getNameCoverAboutUsByLanguage(File currentItemNosotros, String name) {
        String prefixLanguage;
        if (ConstantsApp.MULTILANGUAGE_ENABLE) {
            if (UtilsLanguage.isAppInEnglish()) {
                prefixLanguage = PREFIX_ENGLISH;
            } else {
                prefixLanguage = PREFIX_SPANISH;
            }
        } else {
            prefixLanguage = "";
        }
        return currentItemNosotros + "/" + currentItemNosotros.getName() + prefixLanguage + ".png";
    }

    private static String getPrefixLanguage() {
        if (ConstantsApp.MULTILANGUAGE_ENABLE) {
            if (UtilsLanguage.isAppInEnglish()) {
                return PREFIX_ENGLISH;
            } else {
                return PREFIX_SPANISH;
            }
        } else {
            return "";
        }
    }

    private static FilenameFilter getFileExtensionVideoByLanguage() {
        if (UtilsLanguage.isAppInEnglish()) {
            return new FileExtensionFilterVideo_en();
        } else {
            return new FileExtensionFilterVideo_es();
        }

    }

    public static String removePrefixLanguageOfName(String nameFile) {
        if (nameFile == null) return "";
        String nameLowerCase = nameFile.toLowerCase();
        if (nameLowerCase.contains(PREFIX_ENGLISH) || nameLowerCase.contains(PREFIX_SPANISH)) {
            if (nameLowerCase.length() > 3) {
                return nameFile.substring(0, nameFile.length() - 3);
            }
        }

        return nameFile;
    }
}
