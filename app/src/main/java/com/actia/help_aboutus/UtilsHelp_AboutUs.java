package com.actia.help_aboutus;

import android.content.Context;
import android.content.Intent;

import com.actia.help_movie.PlayAdvertisingpActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.imageslider.ImageSliderFragment;
import com.actia.nosotros.NosotrosShowTextFragment;
import com.actia.nosotros.ShowDirsImagesFragment;
import com.actia.utilities.UtilitiesParser;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileFilter;

public class UtilsHelp_AboutUs {

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_IMAGES = 1;
    public static final int TYPE_DIR_IMAGES = 2;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_TEXT = 5;
    public static final int TYPE_WEB = 6;

    private final Context context;
    File fileRoot;
    File pathLongContent;
    File[] filesImages;
    File[] dirsImages;
    File[] filesVideo;
    File fWeb;
    ConfigMasterMGC configMasterMGC;


    public UtilsHelp_AboutUs(Context context, String pathRoot, ConfigMasterMGC configMasterMGC) {
        this.context = context;
        this.configMasterMGC = configMasterMGC;

        fileRoot = new File(pathRoot);


        //Text
        pathLongContent = new File(fileRoot, configMasterMGC.getNameMessageTxt());

        //Images
        filesImages = fileRoot.listFiles(new FileExtensionFilterImages());
        dirsImages = fileRoot.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        //Movie
        filesVideo = fileRoot.listFiles(new FileExtensionFilterVideo());
        fWeb = new File(pathRoot, configMasterMGC.getNameJsonWebView());
    }


    public int getTypeContent() {
        if (filesVideo != null && filesVideo.length > 0) {
            return TYPE_VIDEO;
        } else if (pathLongContent != null && pathLongContent.exists()) {
            return TYPE_TEXT;
        } else if (filesImages != null && filesImages.length > 0) {
            return TYPE_IMAGES;
        } else if (dirsImages != null && dirsImages.length > 0) {
            return TYPE_DIR_IMAGES;
        } else if (fWeb.exists() && fWeb.canRead()) {
            return TYPE_WEB;
        } else {
            return TYPE_UNKNOWN;
        }

    }


    public Intent getIntentVideo() {
        Intent intent = new Intent(context, PlayAdvertisingpActivity.class);
        intent.putExtra(PlayAdvertisingpActivity.ARG_PATH_ROOT_VIDEO_ADVERTISING, fileRoot.getPath());
        return intent;
    }


    public NosotrosShowTextFragment getFragmentTypeText() {
        File pathShortContent = new File(fileRoot, configMasterMGC.getNameTitleTxt());

        NosotrosShowTextFragment nosotrosShowTextFragment = NosotrosShowTextFragment.newInstance(pathLongContent.getPath(), pathShortContent.getPath());
        return nosotrosShowTextFragment;

    }

    public ImageSliderFragment getFragmentTypeImage() {
        ImageSliderFragment showImageFragment = ImageSliderFragment.newInstance(fileRoot.getPath());
        return showImageFragment;
    }

    public ShowDirsImagesFragment getFragmentTypeImgDir() {
        boolean showBtnClose = true;
        if (fileRoot.getPath().equals(configMasterMGC.getNOSOTROS_PATH())) {
            showBtnClose = false;
        }
        ShowDirsImagesFragment dirsImageFragment = ShowDirsImagesFragment.newInstance(fileRoot.getPath(), showBtnClose);
        return dirsImageFragment;
    }

    public WebViewFragment getWebViewFragment() {

        String textFileMovie = UtilitiesFile.getTextFileMovie(fWeb.getPath());
        try {
            Properties_WebHelpAboutUs properties = new Gson().fromJson(textFileMovie, Properties_WebHelpAboutUs.class);
            return WebViewFragment.newInstance(
                    properties.getUrl(),
                    UtilitiesParser.parseIntToBoolean(properties.getEnableNavigation()),
                    UtilitiesParser.parseIntToBoolean(properties.getShowUrl()),
                    UtilitiesParser.parseIntToBoolean(properties.getShowTitle())
            );
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
