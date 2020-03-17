package com.polaris.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public abstract class PropertyUtil {
	public static Properties getProperties (String fileName,boolean includePath, boolean includeClassPath) {
		if (includePath) {
			try (InputStream in = FileUtil.getStreamFromPath(fileName)) {
				if (in != null) {
					return getProperties(in);
			    }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		if (includeClassPath) {
			try (InputStream in = FileUtil.getStreamFromClassPath(fileName)) {
				if (in != null) {
					return getProperties(in);
			    }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		return null;
	}
	
	public static Properties getProperties(String fileContent) {
		Properties properties = new Properties();
		if (StringUtil.isEmpty(fileContent)) {
			return properties;
		}
		
		try (InputStream inStream = new ByteArrayInputStream(fileContent.getBytes())) {
			properties = getProperties(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return properties;
    }
	
	private static Properties getProperties(InputStream inputStream) throws IOException {
  		Properties properties = new Properties();
        try (InputStreamReader read = new InputStreamReader(inputStream, Charset.defaultCharset())) {
        	properties.load(read);
        } 
	    return properties;
	}

	public static Properties getProperties(Map<String, Object> map) {
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		properties.putAll(getFlattenedMap(map));
		return properties;
	}
	
	public static final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
		Map<String, Object> result = new LinkedHashMap<>();
		buildFlattenedMap(result, source, null);
		return result;
	}

	private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
		source.forEach((key, value) -> {
			if (StringUtils.hasText(path)) {
				if (key.startsWith("[")) {
					key = path + key;
				}
				else {
					key = path + '.' + key;
				}
			}
			if (value instanceof String) {
				
				result.put(key, value);
			}
			else if (value instanceof Map) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
				buildFlattenedMap(result, map, key);
			}
			else if (value instanceof Collection) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) value;
				if (collection.isEmpty()) {
					result.put(key, "");
				}
				else {
					int count = 0;
					for (Object object : collection) {
						buildFlattenedMap(result, Collections.singletonMap(
								"[" + (count++) + "]", object), key);
					}
				}
			}
			else {
				result.put(key, (value != null ? value.toString() : ""));
			}
		});
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
