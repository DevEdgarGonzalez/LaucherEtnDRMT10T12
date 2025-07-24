package com.actia.utilities.utilities_file;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ItemsHome;
import com.actia.utilities.AsyncIntents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileUtils {

    public FileUtils() {
    }
    
    
    public static Typeface getFontFutura(Context ctx){
        return Typeface.createFromAsset(ctx.getAssets(),
                "FuturaLTBold.ttf");
    }
    
    public static Typeface getFontFuturaCondensed(Context ctx){
    	return Typeface.createFromAsset(ctx.getAssets(),
    			"FuturaLTCondensedBold.ttf");
    }
    

    public static String getExtensionFromFilePath(String fullPath) {
        String[] filenameArray = fullPath.split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public static int getAPIVersion() {
        int res;
        res = Build.VERSION.SDK_INT;

        return res;
    }

    public static String getFileName(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
    }
    public static String removeExtension(String nameFile) {
        String nameWithExtension = nameFile.substring(nameFile.lastIndexOf(File.separator) + 1);
        if (nameWithExtension.contains(".")){
            String[] split = nameWithExtension.split("\\.");
            if (split!=null){
                return split[0];
            }


        }
        return nameWithExtension;
    }

    public static String getFileParent(String fullPath) {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }

    public static String getMimeTypeFromFilePath(String filePath) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                getExtensionFromFilePath(filePath));
        return (mimeType == null) ? "*/*" : mimeType;
    }

    public static boolean isImage(String fullPath) {

        File imageFile = new File(fullPath);
        if (!imageFile.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    public static void launchIntent(ItemsHome g1, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(g1.getPackageName(), g1.getClassName());
        changeActivity(activity,intent,false);
    }

    private static void changeActivity(Activity activity, Intent intent, boolean destroyActivity){
        new AsyncIntents(activity,destroyActivity).execute(intent);
    }

    /**
     * Read a text file
     * @param path the text file path
     * @return the file's text
     */
    public static String getTextFileMovie(String path){
        File file=new File(path);
        String text ="NO DISPONIBLE";
        FileChannel fc=null;
        MappedByteBuffer bb=null;
        if(file.exists() && file.isFile() && file.length()>0 && file.canRead()){
            FileInputStream stream=null;
            try {
                stream = new FileInputStream(file);
                fc = stream.getChannel();
                bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                text= Charset.forName("UTF-8").decode(bb).toString();
            } catch (IOException e) {
                e.printStackTrace();
                text= "No disponible";
            }finally{
                try {
                    if(stream!=null)
                        stream.close();
                    if(fc!=null)
                        fc.close();
                    if(bb!=null)
                        bb.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    public static void moveEstrenos(Context context){

        Intent intentBoradcast = new Intent();
        intentBoradcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentBoradcast.setAction("com.actia.launcher");
        intentBoradcast.putExtra("msg", "showdialog");
        boolean inSd = false;

        try {

            ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
            File from = new File(Config.getPATH_SDCARD());
            File[] files = from.listFiles();
            String nameEstrenosDir;

            for (File file : files) {
                if (file.getName().contains("Estreno") || file.getName().contains("estreno")) {
                    inSd = true;
                    nameEstrenosDir = file.getName();
                    File newPath = new File(Config.getVIDEO_PATH(), nameEstrenosDir);
                    if (!newPath.exists()) {
                        newPath.mkdirs();
                    }

                    for(File estrenosFiles : file.listFiles()){
                        File filePath = new File (newPath.getPath() + "/" + estrenosFiles.getName());
                        estrenosFiles.renameTo(filePath);
                    }
                    file.delete();
                    intentBoradcast.putExtra("msgDialog", "Se han pasado todos los archivos a la carpeta de estrenos");
                    intentBoradcast.putExtra("event", 1);
                    //msgDialogo(context, "Se han pasado todos los archivos a la carpeta de estrenos");
                }
            }
            if(!inSd){
                File videoPath = new File(Config.getVIDEO_PATH());
                File[] filesVideoPath = videoPath.listFiles();

                for (File file : filesVideoPath) {
                    if (file.getName().contains("Estreno") || file.getName().contains("estreno")) {
                        inSd = true;
                        intentBoradcast.putExtra("msgDialog", "Ya existe una carpeta con el nombre " + file.getName() + " en /video");
                        intentBoradcast.putExtra("event", 3);
                        break;
                    }
                }
            }

            if(!inSd){
                intentBoradcast.putExtra("msgDialog", "No se encontro la carpeta estrenos en /sd ni en en /video");
                intentBoradcast.putExtra("event", 2);
            }
        }
        catch (Exception ex){
            intentBoradcast.putExtra("msgDialog", "Ha ocurrido algo al pasar los archivos");
            intentBoradcast.putExtra("event", 2);
            Log.e(context.getClass().getSimpleName(), "onClick: " + ex.getMessage());
        }
        context.sendBroadcast(intentBoradcast);
    }


}