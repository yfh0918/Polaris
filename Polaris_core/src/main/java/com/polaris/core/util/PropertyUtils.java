package com.polaris.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.ClassUtils;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerSupport;

public class PropertyUtils {

	private PropertyUtils() {
	}
	
    /** 
     * classpath下的路径 
     * @param fileDir  文件路径 
     * @return 
     */  
    public static String getFullPath(String fileDir) throws IOException {  
    	ClassLoader classLoader = PropertyUtils.class.getClassLoader();
        URL url = classLoader.getResource("");
        if (StringUtil.isEmpty(fileDir)) {
        	return java.net.URLDecoder.decode(url.getPath(),"utf-8");
        }
        return java.net.URLDecoder.decode(url.getPath(),"utf-8") + File.separator + fileDir;
    }
    
	
	public static String readData(String content, String key, String defaultValue) throws IOException{
		
		String[] contents = content.split(Constant.LINE_SEP);
		for (String line : contents) {
			String[] keyvalue = ConfHandlerSupport.getKeyValue(line);
			if (keyvalue != null) {
				if (keyvalue[0].equals(Constant.LOG_CONFIG)) {
					return keyvalue[1];
				}
			}
		}
		return defaultValue;
}

	@SuppressWarnings("rawtypes")
	public static String getPropertiesFileContent(String fileName) {
		StringBuffer buffer = new StringBuffer();
		try (InputStream in = getStream(fileName)) {
			if (in == null) {
				return null;
			}
          Properties p = new Properties();
          try (InputStreamReader read = new InputStreamReader(in, Charset.defaultCharset())) {
              p.load(read);
              for (Map.Entry entry : p.entrySet()) {
                  String key = (String) entry.getKey();
                  buffer.append(key + "=" + entry.getValue());
                  buffer.append(Constant.LINE_SEP);
              }
          }
      } catch (IOException e) {
      	e.printStackTrace();
      }
	  return buffer.toString();
	}
	
	public static InputStream getStream(String fileName) throws IOException {
		
		//先判断目录下的文件夹
		//file:/C:/projects/bin/xxxxx/yyyy.jar!/BOOT-INF/classes!/
		String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		if (path.startsWith("file:")) {
			path = path.substring(5);
			path = path.split("!")[0];
			if (path.endsWith(".jar")) {
				path = new File(path).getParent();
			}
		}
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
}
