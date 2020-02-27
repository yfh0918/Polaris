package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.YamlUtil;

public class ConfYamlReader implements ConfReader{

	@Override
	public Properties getProperties(String fileName) {
		return getProperties(fileName, true);
	}

	@Override
	public Properties getProperties(String fileName, boolean includeClassPath) {
		return YamlUtil.getProperties(fileName, includeClassPath);
	}

	@Override
	public Properties getProperties(String fileName, String lines) {
		return YamlUtil.getProperties(lines); 
	}

}
