package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilterImages_en implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith("_en.png") || name.endsWith("_en.PNG") || name.endsWith("_EN.jpg") || name.endsWith("_EN.JPG"));
    }
}
