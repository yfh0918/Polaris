package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.YamlUtil;

public class ConfYamlReader implements ConfReader{


	public static ConfYamlReader INSTANCE = new ConfYamlReader();
	
	private ConfYamlReader() {}
	
	@Override
	public Properties getProperties(String fileName, boolean includePath, boolean includeClassPath) {
		return YamlUtil.getProperties(fileName, includePath, includeClassPath);
	}

	@Override
	public Properties getProperties(String fileContent) {
		return YamlUtil.getProperties(fileContent); 
	}

}
