package com.polaris.core.config.reader;

import java.util.Properties;

public interface ConfReader {
	
	Properties getProperties(String fileName, boolean includePath, boolean includeClassPath);
	
	Properties getProperties(String contentLines);
}
