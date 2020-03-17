package com.polaris.core.config.reader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.util.FileUtil;
import com.polaris.core.util.StringUtil;

public class ConfReaderFactory {

	private static Map<String , ConfReader> confReaderMap = new ConcurrentHashMap<>();
    public static final String[] SUPPORT_TYPE = {"properties","yaml","yml","xml"};
	static {
		confReaderMap.put(SUPPORT_TYPE[0], ConfPropertiesReader.INSTANCE);
		confReaderMap.put(SUPPORT_TYPE[1], ConfYamlReader.INSTANCE);
		confReaderMap.put(SUPPORT_TYPE[2], ConfYamlReader.INSTANCE);
		confReaderMap.put(SUPPORT_TYPE[3], ConfXmlReader.INSTANCE);
	}
	
	public static ConfReader get(String fileName) {
		String suffix = FileUtil.getSuffix(fileName);
		if (StringUtil.isEmpty(suffix)) {
			throw new RuntimeException("file:"+fileName+" is not supported ");
		}
		ConfReader confReader = confReaderMap.get(suffix);
		if (confReader == null) {
			throw new RuntimeException("file:"+fileName+" is not supported ");
		}
		return confReader;
	}
	
	public static void set(String suffix, ConfReader confReader) {
		confReaderMap.put(suffix, confReader);
	}
}
