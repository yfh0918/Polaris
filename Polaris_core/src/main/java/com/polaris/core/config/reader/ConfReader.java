package com.polaris.core.config.reader;

import java.io.InputStream;
import java.util.Properties;

public interface ConfReader {
	
	/**
	 * get properties from InputStream
	 */
	Properties getProperties(InputStream inputStream);
	
	/**
	 * get properties from file Contents 
	 */
	Properties getProperties(String fileContent);
}
