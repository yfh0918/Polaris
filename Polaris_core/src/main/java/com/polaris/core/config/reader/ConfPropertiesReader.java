package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.PropertyUtils;

public class ConfPropertiesReader implements ConfReader{

	@Override
	public Properties getProperties(String fileName, boolean includePath, boolean includeClassPath) {
		return PropertyUtils.getProperties(fileName, includePath, includeClassPath);
	}

	@Override
	public Properties getProperties(String contectLines) {
		return PropertyUtils.getProperties(contectLines);
	}

}
