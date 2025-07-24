package com.actia.drm.auto_tokens;

import com.actia.infraestructure.ConfigMasterMGC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class StorageContentNewTokens {
    public static String getXmlInStringNewTokens() {
        ConfigMasterMGC config = new ConfigMasterMGC();
        String json = null;
        File jsonFile = new File(config.getCONFIG_DIRECTORY() + "/dbxmlmultimedia.xml");
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(jsonFile);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb;
            bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            json = Charset.defaultCharset().decode(bb).toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;

    }

}
