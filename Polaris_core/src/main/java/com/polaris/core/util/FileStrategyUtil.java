package com.polaris.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.polaris.core.config.reader.ConfReaderStrategy;
import com.polaris.core.config.reader.ConfReaderStrategyDefault;

public abstract class FileStrategyUtil {
	public static String getFileContent(String fileName) {
		ConfReaderStrategy reader = ConfReaderStrategyDefault.INSTANCE;
		InputStream is = null;
		String content = null;
		try {
			
			//从path获取
    		File contentFile = reader.getFile(fileName);
    		if (contentFile != null) {
    			is = new FileInputStream(contentFile);
    		} else {
    			
    			//从classpath获取
    			is = reader.getInputStream(fileName);
    		}
    		if (is != null) {
    			content = FileUtil.read(is);
    		}
		} catch (IOException ex) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return content;
	}
}
