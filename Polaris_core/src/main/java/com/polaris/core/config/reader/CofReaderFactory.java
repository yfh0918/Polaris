package com.polaris.core.config.reader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.util.FileUitl;

public class CofReaderFactory {

	private static Map<String , ConfReader> confReaderMap = new ConcurrentHashMap<>();
    public static final String[] SUPPORT_TYPE = {"properties","yaml","xml"};
	static {
		confReaderMap.put(SUPPORT_TYPE[0], new ConfPropertiesReader());
		confReaderMap.put(SUPPORT_TYPE[1], new ConfYamlReader());
		confReaderMap.put(SUPPORT_TYPE[2], new ConfXmlReader());
	}
	
	public static ConfReader get(String fileName) {
		ConfReader confReader = confReaderMap.get(FileUitl.getSuffix(fileName));
		if (confReader == null) {
			throw new RuntimeException("file:"+fileName+" is not supported ");
		}
		return confReader;
	}
	
	public static void set(String suffix, ConfReader confReader) {
		confReaderMap.put(suffix, confReader);
	}
}
