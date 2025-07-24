package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilterVideo_es implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith("_es.3gp") || name.endsWith("_es.3GP")
                || name.endsWith("_es.mp4") || name.endsWith("_es.MP4")
                || name.endsWith("_es.mpg") || name.endsWith("_es.MPG")
                || name.endsWith("_es.rmvb") || name.endsWith("_es.RMVB")
                || name.endsWith("_es.avi") || name.endsWith("_es.AVI")
                || name.endsWith("_es.m4v") || name.endsWith("_es.M4V")
                || name.endsWith("_es.m2ts") || name.endsWith("_es.M2TS")
                || name.endsWith("_es.VOB") || name.endsWith("_es.vob")
                || name.endsWith("_es.TS") || name.endsWith("_es.ts")
                || name.endsWith("_es.flv") || name.endsWith("_es.FLV")
                || name.endsWith("_es.mov") || name.endsWith("_es.MOV")
                || name.endsWith("_es.3gpp") || name.endsWith("_es.3GPP")
                || name.endsWith("_es.dat") || name.endsWith("_es.DAT")
                || name.endsWith("_es.mpeg") || name.endsWith("_es.MPEG")
                || name.endsWith("_es.m2v") || name.endsWith("_es.M2V")
                || name.endsWith("_es.iso") || name.endsWith("_es.ISO")
                || name.endsWith("_es.3g2") || name.endsWith("_es.3G2")
                || name.endsWith("_es.flc") || name.endsWith("_es.FLC")
                || name.endsWith("_es.mlv") || name.endsWith("_es.MLV")
                || name.endsWith("_es.mkv") || name.endsWith("_es.MKV"));
    }
}
