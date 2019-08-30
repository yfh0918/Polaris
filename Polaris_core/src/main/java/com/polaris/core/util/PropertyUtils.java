package com.polaris.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfigHandlerProvider;

public class PropertyUtils {

	private PropertyUtils() {
	}
	
    /** 
     * 判断文件是否存在. 
     * @param fileDir  文件路径 
     * @return 
     */  
    public static String getFilePath(String fileDir) throws IOException {  
    	ClassLoader classLoader = PropertyUtils.class.getClassLoader();
        URL url = classLoader.getResource("");
        return java.net.URLDecoder.decode(url.getPath(),"utf-8") + File.separator + fileDir;
    }
    
    /** 
     * 判断文件是否存在. 
     * @return 
     */  
    public static String getAppPath() {  
    	ClassLoader classLoader = PropertyUtils.class.getClassLoader();
        URL url = classLoader.getResource("");
        String path = System.getProperty("user.home");
		try {
			path = java.net.URLDecoder.decode(url.getPath(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// nothing
		}
        java.io.File file = new java.io.File(path);
        return file.getAbsolutePath();
    }

    
	/**  
	  * 根据Key 读取Value  
	  *   
	  * @param key  
	  * @return  
	  */ 
	@SuppressWarnings("unchecked")
	public static String readData(String propertyName, String key, boolean fullpath) throws IOException , ConfigurationException{
		
		
		   //创建文件夹
		   String profilepath = propertyName;
		   if (!fullpath) {
			   profilepath = getFilePath(propertyName);
		   } 
		   PropertiesConfiguration config  = new PropertiesConfiguration(profilepath);
		   config.setEncoding(Constant.UTF_CODE);
		   Object result = config.getProperty(key);
		   if(result == null) {
			   return null;
		   } else {
			   if (result instanceof ArrayList) {
				   StringBuilder strB = new StringBuilder();
				   for (String temp : ((List<String>)result)) {
					   if (StringUtil.isEmpty(strB.toString())) {
						   strB.append(temp);
					   } else {
						   strB.append(",");
						   strB.append(temp);
					   }
				   }
				   return strB.toString();
			   }
			   return result.toString();
		   }
   }
	
	/**  
	  * 根据Key 读取Value  
	  *   
	  * @param key  
	  * @return  
	  */ 
	@SuppressWarnings("unchecked")
	public static String readData(String path, String likeFileName, String suffix, String key, boolean fullpath) throws IOException , ConfigurationException{
		
		   //创建文件夹
		   String profilepath = path;
		   if (!fullpath) {
			   profilepath = getFilePath(path);
		   } 
		   File file = new File(profilepath);
		   File[] files = file.listFiles();
		   if (files == null) {
			   return null;
		   }
		   for (File f : files) {
			   if (f.isFile() && f.getName().contains(likeFileName)) {
				   if (f.getName().length() >= suffix.length() && suffix.equals(f.getName().substring(f.getName().length() - suffix.length()))) {
					   PropertiesConfiguration config  = new PropertiesConfiguration(f);
					   config.setEncoding(Constant.UTF_CODE);
					   Object result = config.getProperty(key);
					   if (result != null) {
						   if (result instanceof ArrayList) {
							   StringBuilder strB = new StringBuilder();
							   for (String temp : ((List<String>)result)) {
								   if (StringUtil.isEmpty(strB.toString())) {
									   strB.append(temp);
								   } else {
									   strB.append(",");
									   strB.append(temp);
								   }
							   }
							   return strB.toString();
						   }
						   return result.toString();
					   }
				   }
				   
			   }
		   }
		   return null;
  }

   /**  
    * 修改或添加键值对 如果key存在，修改 反之，添加。  
    *   
    * @param key  
    * @param value  
    */  
   /**  
    * 修改或添加键值对 如果key存在，修改 反之，添加。  
    *   
    * @param key  
    * @param value  
    */  
  public static void writeData(String propertyName, String key, String value, boolean fullpath) throws IOException, ConfigurationException {
	   
	   //创建文件夹
	   String profilepath = propertyName;
	   if (!fullpath) {
		   profilepath = getFilePath(propertyName);
	   } 
	   PropertiesConfiguration config  = new PropertiesConfiguration(profilepath);
	   config.setEncoding(Constant.UTF_CODE);
       config.setAutoSave(true);
       config.setProperty(key, value);
  }
  
  /**  
   * 修改或添加键值对 如果key存在，修改 反之，添加。  
   *   
   * @param key  
   * @param value  
   */  
  /**  
   * 修改或添加键值对 如果key存在，修改 反之，添加。  
   *   
   * @param key  
   * @param value  
   */  
  public static void writeData(String path, String likeFileName, String suffix, String key, String value, boolean fullpath) throws IOException, ConfigurationException {
	   
	 //创建文件夹
	   String profilepath = path;
	   if (!fullpath) {
		   profilepath = getFilePath(path);
	   } 
	   File file = new File(profilepath);
	   File[] files = file.listFiles();
	   if (files == null) {
		   return;
	   }
	   for (File f : files) {
		   if (f.isFile() && f.getName().contains(likeFileName)) {
			   if (f.getName().length() >= suffix.length() && suffix.equals(f.getName().substring(f.getName().length() - suffix.length()))) {
				   PropertiesConfiguration config  = new PropertiesConfiguration(f);
				   config.setEncoding(Constant.UTF_CODE);
				   Object result = config.getProperty(key);
				   if (result != null) {
					   config.setAutoSave(true);
				       config.setProperty(key, value);
					   break;
				   }
			   }
			   
		   }
	   }
  }
  
	@SuppressWarnings("rawtypes")
	public static String getPropertiesFileContent(String fileName) {
		StringBuffer buffer = new StringBuffer();
		try (InputStream in = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(fileName)) {
			if (in == null) {
				return null;
			}
          Properties p = new Properties();
          p.load(in);
          for (Map.Entry entry : p.entrySet()) {
              String key = (String) entry.getKey();
              buffer.append(key + "=" + entry.getValue());
              buffer.append(Constant.LINE_SEP);
          }
      } catch (IOException e) {
      	e.printStackTrace();
      }
		return buffer.toString();
	}
}
