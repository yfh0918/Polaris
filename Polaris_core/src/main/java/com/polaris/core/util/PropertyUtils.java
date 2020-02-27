package com.polaris.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;

public abstract class PropertyUtils {
	
	public static Properties getProperties (String fileName,boolean includeClassPath) {
		try (InputStream in = FileUitl.getStream(fileName,includeClassPath)) {
			if (in != null) {
	      		return getProperties(in);
		    }
	    } catch (IOException e) {
		   e.printStackTrace();
	    }
	    return null;
	}
	
	public static Properties getProperties(InputStream inputStream) throws IOException {
  		Properties properties = new Properties();
        try (InputStreamReader read = new InputStreamReader(inputStream, Charset.defaultCharset())) {
        	properties.load(read);
        }
	    return properties;
	}
	
	public static Properties getProperties(String fileName, String lines) {
		Properties properties = new Properties();
    	if (StringUtil.isNotEmpty(lines)) {
			String[] contents = lines.split(Constant.LINE_SEP);
			for (String content : contents) {
				String[] keyvalue = getKeyValue(content);
				if (keyvalue != null) {
					properties.put(keyvalue[0], keyvalue[1]);
				}
			}
		}
    	return properties;
    }
	public static Map<String, Object> getMap(String fileName, String lines) {
    	Map<String, Object> propertyMap = new HashMap<>();
    	if (StringUtil.isNotEmpty(lines)) {
			String[] contents = lines.split(Constant.LINE_SEP);
			for (String content : contents) {
				String[] keyvalue = getKeyValue(content);
				if (keyvalue != null) {
					propertyMap.put(keyvalue[0], keyvalue[1]);
				}
			}
		}
    	return propertyMap;
    }
	
	/**
	* 获取KV对
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getKeyValue(String line) {
		if (StringUtil.isNotEmpty(line)) {
			String[] keyvalue = line.split("=");
			if (keyvalue.length == 0) {
				return new String[] {"",""};
			}
			if (keyvalue.length == 1) {
				return new String[] {keyvalue[0].trim(),""};
			}
			String value = "";
			for (int index = 0; index < keyvalue.length; index++) {
				if (index != 0) {
					if (StringUtil.isEmpty(value)) {
						value = keyvalue[index].trim();
					} else {
						value = value + "=" + keyvalue[index].trim();
					}
				}
			}
			return new String[] {keyvalue[0].trim(),value};
		}
		return null;
	}
}
