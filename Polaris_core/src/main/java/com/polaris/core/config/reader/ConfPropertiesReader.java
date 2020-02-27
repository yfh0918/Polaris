package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.PropertyUtils;

public class ConfPropertiesReader implements ConfReader{

	@Override
	public Properties getProperties(String fileName) {
		return getProperties(fileName, true);
	}

	@Override
	public Properties getProperties(String fileName, boolean includeClassPath) {
		return PropertyUtils.getProperties(fileName, includeClassPath);
	}

	@Override
	public Properties getProperties(String fileName, String lines) {
		return PropertyUtils.getProperties(fileName, lines);
	}

}
