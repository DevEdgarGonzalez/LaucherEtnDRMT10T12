package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilterVideo_en implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith("_en.3gp") || name.endsWith("_en.3GP")
                || name.endsWith("_en.mp4") || name.endsWith("_en.MP4")
                || name.endsWith("_en.mpg") || name.endsWith("_en.MPG")
                || name.endsWith("_en.rmvb") || name.endsWith("_en.RMVB")
                || name.endsWith("_en.avi") || name.endsWith("_en.AVI")
                || name.endsWith("_en.m4v") || name.endsWith("_en.M4V")
                || name.endsWith("_en.m2ts") || name.endsWith("_en.M2TS")
                || name.endsWith("_en.VOB") || name.endsWith("_en.vob")
                || name.endsWith("_en.TS") || name.endsWith("_en.ts")
                || name.endsWith("_en.flv") || name.endsWith("_en.FLV")
                || name.endsWith("_en.mov") || name.endsWith("_en.MOV")
                || name.endsWith("_en.3gpp") || name.endsWith("_en.3GPP")
                || name.endsWith("_en.dat") || name.endsWith("_en.DAT")
                || name.endsWith("_en.mpeg") || name.endsWith("_en.MPEG")
                || name.endsWith("_en.m2v") || name.endsWith("_en.M2V")
                || name.endsWith("_en.iso") || name.endsWith("_en.ISO")
                || name.endsWith("_en.3g2") || name.endsWith("_en.3G2")
                || name.endsWith("_en.flc") || name.endsWith("_en.FLC")
                || name.endsWith("_en.mlv") || name.endsWith("_en.MLV")
                || name.endsWith("_en.mkv") || name.endsWith("_en.MKV"));
    }
}
