package com.polaris.core.config.reader;

import java.util.Properties;

import com.polaris.core.util.XmlUtil;

public class ConfXmlReader implements ConfReader{

	@Override
	public Properties getProperties(String fileName, boolean includePath, boolean includeClassPath) {
		return XmlUtil.getProperties(fileName, includePath, includeClassPath);
	}

	@Override
	public Properties getProperties(String fileContent) {
		return XmlUtil.getProperties(fileContent);
	}
}
