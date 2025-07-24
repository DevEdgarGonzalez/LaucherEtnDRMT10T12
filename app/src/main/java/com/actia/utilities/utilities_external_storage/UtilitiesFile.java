package com.actia.utilities.utilities_external_storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Edgar Gonzalez on 26/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesFile {

    public static String getTextFileMovie(String path, String textError) {
        File file = new File(path);
        String text = textError;
        FileChannel fc = null;
        MappedByteBuffer bb = null;
        if (file.exists() && file.isFile() && file.length() > 0 && file.canRead()) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                fc = stream.getChannel();
                bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                text = StandardCharsets.UTF_8.decode(bb).toString();
            } catch (IOException e) {
                e.printStackTrace();
                text = textError;
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                    if (fc != null)
                        fc.close();
                    if (bb != null)
                        bb.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    /**
     * Read a text file
     *
     * @param path the text file path
     * @return the file's text
     */
    public static String getTextFileMovie(String path) {
        File file = new File(path);
        String text = "NO DISPONIBLE";
        FileChannel fc = null;
        MappedByteBuffer bb = null;
        if (file.exists() && file.isFile() && file.length() > 0 && file.canRead()) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                fc = stream.getChannel();
                bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                text = StandardCharsets.UTF_8.decode(bb).toString();
            } catch (IOException e) {
                e.printStackTrace();
                text = "No disponible";
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                    if (fc != null)
                        fc.close();
                    if (bb != null)
                        bb.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    public static void setExtSdCard(Context context) {
        File[] filesDirectory = context.getExternalFilesDirs(null);
        String extSdCard = "";
        SharedPreferences preferences;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_extsd_path), Context.MODE_PRIVATE);
        String preference = preferences.getString(context.getString(R.string.preference_extsd_path), "");
        SharedPreferences.Editor editor = preferences.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (TextUtils.isEmpty(preference)) {
                for (File file : filesDirectory) {
                    if (!TextUtils.isEmpty(file.getAbsolutePath()) && file.getAbsolutePath().contains("storage")) {
                        File extsd = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("/Android/data/")), "/config/config.json");
                        if (extsd.exists()) {
                            extSdCard = file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("/Android/data/"));
                            editor.putString(context.getString(R.string.preference_extsd_path), extSdCard);
                            editor.apply();
                        }
                    }
                }
            } else {
                for (File file : filesDirectory) {
                    if (file.getAbsolutePath().contains("storage")
                            && !TextUtils.isEmpty(file.getAbsolutePath())
                            && !preference.equals(file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("/Android/data/")))) {
                        File extsd = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("/Android/data/")), "/config/config.json");
                        if (extsd.exists()){
                            extSdCard = file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("/Android/data/"));
                            editor.putString(context.getString(R.string.preference_extsd_path), extSdCard);
                            editor.apply();
                        }
                    }
                }
            }
        } else {
            editor.putString(context.getString(R.string.preference_extsd_path), "/mnt/extsd");
            editor.apply();
        }
    }

}
