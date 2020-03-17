package com.polaris.core.config.reader;

import java.io.InputStream;
import java.util.Properties;

import com.polaris.core.util.PropertyUtil;

public class ConfPropertiesReader implements ConfReader{

	public static ConfPropertiesReader INSTANCE = new ConfPropertiesReader();
	
	private ConfPropertiesReader() {}
	
	@Override
	public Properties getProperties(InputStream inputStream) {
		return PropertyUtil.getProperties(inputStream);
	}

	@Override
	public Properties getProperties(String fileContent) {
		return PropertyUtil.getProperties(fileContent);
	}

}
