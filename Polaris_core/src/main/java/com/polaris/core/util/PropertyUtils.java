package com.polaris.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;

public abstract class PropertyUtils {
	
    
    
	
	public static String readData(String content, String key, String defaultValue) throws IOException{
		
		String[] contents = content.split(Constant.LINE_SEP);
		for (String line : contents) {
			String[] keyvalue = getKeyValue(line);
			if (keyvalue != null) {
				if (keyvalue[0].equals(key)) {
					return keyvalue[1];
				}
			}
		}
		return defaultValue;
}

	@SuppressWarnings("rawtypes")
	public static String getPropertiesFileContent(String fileName) {
		StringBuffer buffer = new StringBuffer();
		try (InputStream in = FileUitl.getStream(fileName)) {
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
