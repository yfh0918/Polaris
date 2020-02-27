package com.polaris.core.config.reader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CofReaderFactory {

	private static Map<String , ConfReader> confReaderMap = new ConcurrentHashMap<>();
	static {
		confReaderMap.put(ConfReader.PROPERTIES, new ConfPropertiesReader());
		confReaderMap.put(ConfReader.YAML, new ConfYamlReader());
	}
	
	public static ConfReader get(String fileName) {
		if (fileName.endsWith(ConfReader.DOT+ConfReader.PROPERTIES)) {
			return confReaderMap.get(ConfReader.PROPERTIES);
		}
		if (fileName.endsWith(ConfReader.DOT+ConfReader.YAML)) {
			return confReaderMap.get(ConfReader.YAML);
		}
		return confReaderMap.get(ConfReader.PROPERTIES);
	}
	
	public static void set(String key, ConfReader confReader) {
		confReaderMap.put(key, confReader);
	}
}
