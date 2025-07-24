package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FileFilter;

public class TokenFilter implements FileFilter{

	@Override
	public boolean accept(File file) {
		return file.isFile() && file.length() > 0 && file.getName().endsWith(".xml");
	}

}
