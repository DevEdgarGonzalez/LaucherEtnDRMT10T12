package com.actia.utilities.ContentType;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.actia.help_movie.PlayAdvertisingpActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.imageslider.ImageSliderFragment;
import com.actia.multimedia.MultimediaShowTextFragment;
import com.actia.multimedia.Properties_WebHelpMultimedia;
import com.actia.multimedia.ShowDirsImagesFragment;
import com.actia.multimedia.WebViewFragment;
import com.actia.utilities.UtilitiesParser;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_file.FileExtensionFilterVideo;
import com.actia.utilities.utilities_file.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileFilter;

public class UtilsContentType {

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_IMAGES = 1;
    public static final int TYPE_DIR_IMAGES = 2;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_TEXT = 5;
    public static final int TYPE_WEB = 6;

    private Context context;
    File fileRoot;
    File pathLongContent;
    File[] filesImages;
    File[] dirsImages;
    File[] filesVideo;
    File fWeb;
    ConfigMasterMGC configMasterMGC;


    public UtilsContentType(Context context, String pathRoot, ConfigMasterMGC configMasterMGC) {
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
                if (pathname.isDirectory()) {
                    return true;
                }
                return false;
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


    public MultimediaShowTextFragment getFragmentTypeText() {
        File pathShortContent = new File(fileRoot, configMasterMGC.getNameTitleTxt());

        MultimediaShowTextFragment multimediaShowTextFragment = MultimediaShowTextFragment.newInstance(pathLongContent.getPath(), pathShortContent.getPath());
        return multimediaShowTextFragment;

    }

    public ImageSliderFragment getFragmentTypeImage() {
        ImageSliderFragment showImageFragment = ImageSliderFragment.newInstance(fileRoot.getPath());
        return showImageFragment;
    }

    public Fragment getFragmentTypeImgDir() {
        boolean showBtnClose = true;
        if (fileRoot.getPath().equals(configMasterMGC.getPLUS_MEDIA_PATH())) {
            showBtnClose = false;
        }
        ShowDirsImagesFragment dirsImageFragment = ShowDirsImagesFragment.newInstance(fileRoot.getPath(), showBtnClose);
        return dirsImageFragment;
    }

    public WebViewFragment getWebViewFragment() {

        String textFileMovie = FileUtils.getTextFileMovie(fWeb.getPath());
        try {
            Properties_WebHelpMultimedia properties = new Gson().fromJson(textFileMovie, Properties_WebHelpMultimedia.class);
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
