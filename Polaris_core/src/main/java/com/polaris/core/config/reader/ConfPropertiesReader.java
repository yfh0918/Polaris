package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.PropertyUtil;

public class ConfPropertiesReader implements ConfReader{

	public static ConfPropertiesReader INSTANCE = new ConfPropertiesReader();
	
	private ConfPropertiesReader() {}
	
	@Override
	public Properties getProperties(String fileName, boolean includePath, boolean includeClassPath) {
		return PropertyUtil.getProperties(fileName, includePath, includeClassPath);
	}

	@Override
	public Properties getProperties(String fileContent) {
		return PropertyUtil.getProperties(fileContent);
	}

}
