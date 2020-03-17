package com.polaris.core.config.reader;

import java.io.InputStream;
import java.util.Properties;

public interface ConfReader {
	
	Properties getProperties(InputStream inputStream);
	
	Properties getProperties(String fileContent);
}
