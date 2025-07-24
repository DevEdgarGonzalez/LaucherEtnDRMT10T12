package com.actia.utilities.utilities_logs_in_sd;

import android.content.Context;
import android.content.SharedPreferences;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class FileWritteLogErrorToken {
    public static void writteInLog(String txtError, Context context){

        try {
            Calendar cal = Calendar.getInstance();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateError =dateFormat.format(cal.getTime());

            SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_extsd_path), Context.MODE_PRIVATE);
            String preference = preferences.getString(context.getString(R.string.preference_extsd_path), "");

            File root = new File(preference + "/logs");

            if (!root.exists()){
                root.mkdirs();
            }
            File fileLogError = new File(root, "LogErrorValidateTokens.txt");
            if (!fileLogError.exists()){
                fileLogError.createNewFile();
            }

            FileInputStream stream = null;
            stream = new FileInputStream(fileLogError);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb;
            bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());


            StringBuilder sb = new StringBuilder();
            sb.append(Charset.defaultCharset().decode(bb).toString());
            sb.append(txtError+"\t"+ dateError+"\n");
            FileWriter writer = new FileWriter(fileLogError);
            writer.append(sb);
            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }





    }


}
