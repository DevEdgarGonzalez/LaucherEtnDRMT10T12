package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * only filter type image files
 */

public class FileExtensionFilterImages implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpg") || name.endsWith(".JPG"));
    }
}
