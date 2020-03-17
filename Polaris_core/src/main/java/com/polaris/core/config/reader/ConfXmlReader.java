package com.polaris.core.config.reader;

import java.io.InputStream;
import java.util.Properties;

import com.polaris.core.util.XmlUtil;

public class ConfXmlReader implements ConfReader{

	public static ConfXmlReader INSTANCE = new ConfXmlReader();
	
	private ConfXmlReader() {}
	
	@Override
	public Properties getProperties(InputStream inputStream) {
		return XmlUtil.getProperties(inputStream);
	}

	@Override
	public Properties getProperties(String fileContent) {
		return XmlUtil.getProperties(fileContent);
	}
}
