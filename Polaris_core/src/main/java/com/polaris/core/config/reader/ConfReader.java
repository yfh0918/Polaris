package com.polaris.core.config.reader;

import java.util.Properties;

public interface ConfReader {
	static final String DOT = ".";
    static final String PROPERTIES = "properties";
    static final String YAML = "yaml";
    static final String XML = "xml";
    static final String TXT = "txt";
	
	Properties getProperties(String fileName);
	
	Properties getProperties(String fileName, boolean includeClassPath);
	
	Properties getProperties(String fileName, String lines);
}
