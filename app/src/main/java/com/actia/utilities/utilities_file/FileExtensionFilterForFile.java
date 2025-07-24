package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FileFilter;

/**
 * Valida si es directorio y contiene archivos
 */
public class FileExtensionFilterForFile implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()){
            File[] files = pathname.listFiles();
            return files != null && files.length > 0;
        }
        return false;
    }
}
