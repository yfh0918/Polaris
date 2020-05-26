package com.polaris.core.config.reader;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public interface ConfReaderStrategy {
	
	/**
	 * get file contents from fileName according to strategy rule
	 */
	String getContents (String fileName);
	
	/**
	 * get Properties from fileName according to strategy rule
	 */
	Properties getProperties (String fileName);
	
	/**
	 * get InputStream from fileName according to strategy rule
	 */
	InputStream getInputStream (String fileName);
	
	/**
	 * get File from fileName according to strategy rule
	 */
	File getFile (String fileName);
}
