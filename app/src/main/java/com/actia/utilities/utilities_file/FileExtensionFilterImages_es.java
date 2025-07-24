package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilterImages_es implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith("_es.png") || name.endsWith("_es.PNG") || name.endsWith("_ES.jpg") || name.endsWith("_ES.JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpg") || name.endsWith(".JPG"));
    }
}
