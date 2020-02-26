package com.polaris.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.springframework.util.ClassUtils;

import com.polaris.core.Constant;

public abstract class FileUitl {
	/** 
     * classpath下的路径 
     * @param fileDir  文件路径 
     * @return 
     */  
    public static String getFullPath(String fileDir) throws IOException {  
    	URL url = ClassUtils.getDefaultClassLoader().getResource("");
    	String path = java.net.URLDecoder.decode(url.getPath(),Charset.defaultCharset().name());
    	
		//file:/C:/projects/bin/xxxxx/yyyy.jar!/BOOT-INF/classes!/
		if (path.startsWith("file:")) {
			path = path.substring(5);
		}
		path = path.split("!")[0];
		if (path.endsWith(".jar")) {
			path = new File(path).getParent();
		}
        if (StringUtil.isEmpty(fileDir)) {
        	return path;
        }
        return path + File.separator + fileDir;
    }
    
public static InputStream getStream(String fileName) throws IOException {
		
		//先判断目录下的文件夹
		String path = getFullPath("");
		File file = new File(path + File.separator + Constant.CONFIG + File.separator + fileName);
		if (file.exists()) {
			return new FileInputStream(file);
		}
		
		//根目录下
		file = new File(path + File.separator + fileName);
		if (file.exists()) {
			return new FileInputStream(file);
		}
		
		//classpath:config/filename
		InputStream inputStream = PropertyUtils.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName);
		if (inputStream != null) {
			return inputStream;
		}
		
		//classpath:filename
		return PropertyUtils.class.getClassLoader().getResourceAsStream(fileName);
	}
	
	
	
	public static File getFileNotInJar(String fileName)  {
		File file = new File(Constant.CONFIG + File.separator + fileName);
		if (file.exists()) {
			return file;
		}
		file = new File(fileName);
		if (file.exists()) {
			return file;
		}
		return null;
	}
	
	public static String getDecryptValue(String propVal) {
		//解密操作
		try {
			EncryptUtil encrypt = EncryptUtil.getInstance();
			propVal = encrypt.decrypt(EncryptUtil.START_WITH, propVal);
		} catch (Exception ex) {
			//nothing
		}
		return propVal;
	}
}
