package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * only filter type music files
 */
public class FileExtensionFilterMusic implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3") || name.endsWith(".MP3"));
    }
}
