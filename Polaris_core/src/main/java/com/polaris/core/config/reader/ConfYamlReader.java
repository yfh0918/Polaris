package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.YamlUtil;

public class ConfYamlReader implements ConfReader{

	@Override
	public Properties getProperties(String fileName, boolean includePath, boolean includeClassPath) {
		return YamlUtil.getProperties(fileName, includePath, includeClassPath);
	}

	@Override
	public Properties getProperties(String contentLines) {
		return YamlUtil.getProperties(contentLines); 
	}

}
