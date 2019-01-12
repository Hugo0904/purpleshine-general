package com.purpleshine.general.helpers;

import java.io.File;

public class FileUtil {
	/**
	 * 取得檔名(不含父檔案名稱)
	 * @param file
	 * @return
	 */
	static public String getBaseFileName(File file)
    {
		final String name = file.getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
		    return name.substring(0, pos);
		}
		return name;
    }
	
	/**
	 * 取得副檔名
	 * @param file
	 * @return
	 */
	static public String getExtension(File file)
    {
        int startIndex = file.getName().lastIndexOf(46) + 1;
        int endIndex = file.getName().length();
        return  file.getName().substring(startIndex, endIndex);
    }
}
