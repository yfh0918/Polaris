package com.polaris.core.config.reader;

import java.io.InputStream;
import java.util.Properties;

import com.polaris.core.util.YamlUtil;

public class ConfYamlReader implements ConfReader{


	public static ConfYamlReader INSTANCE = new ConfYamlReader();
	
	private ConfYamlReader() {}
	
	@Override
	public Properties getProperties(InputStream inputStream) {
		return YamlUtil.getProperties(inputStream);
	}

	@Override
	public Properties getProperties(String fileContent) {
		return YamlUtil.getProperties(fileContent); 
	}

}
