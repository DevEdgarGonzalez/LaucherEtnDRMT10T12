package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * only filter type video files
 */

public class FileExtensionFilterVideo implements FilenameFilter {
    public boolean accept(File dir, String name) {
    	return (name.endsWith(".3gp") || name.endsWith(".3GP")
        		|| name.endsWith(".mp4") || name.endsWith(".MP4")
        		|| name.endsWith(".mpg") || name.endsWith(".MPG")
        		|| name.endsWith(".rmvb") || name.endsWith(".RMVB")
        		|| name.endsWith(".avi") || name.endsWith(".AVI")
        		|| name.endsWith(".m4v") || name.endsWith(".M4V")
        		|| name.endsWith(".m2ts") || name.endsWith(".M2TS")
        		|| name.endsWith(".VOB") || name.endsWith(".vob")
        		|| name.endsWith(".TS") || name.endsWith(".ts")
        		|| name.endsWith(".flv") || name.endsWith(".FLV")
        		|| name.endsWith(".mov") || name.endsWith(".MOV")
        		|| name.endsWith(".3gpp") || name.endsWith(".3GPP")
        		|| name.endsWith(".dat") || name.endsWith(".DAT")
        		|| name.endsWith(".mpeg") || name.endsWith(".MPEG")
        		|| name.endsWith(".m2v") || name.endsWith(".M2V")
        		|| name.endsWith(".iso") || name.endsWith(".ISO")
        		|| name.endsWith(".3g2") || name.endsWith(".3G2")
        		|| name.endsWith(".flc") || name.endsWith(".FLC")
				|| name.endsWith(".mlv") || name.endsWith(".MLV")
        		|| name.endsWith(".mkv") || name.endsWith(".MKV"));
    }
}

