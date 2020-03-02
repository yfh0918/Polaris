package com.polaris.core.util;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvironmentUtil {
	
	public static Properties getSystemProperties() {
		try {
			return System.getProperties();
		}
		catch (AccessControlException ex) {
			return new Properties();
		}
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Map<String, String> getSystemEnvironment() {
		try {
			return (Map) System.getenv();
		}
		catch (AccessControlException ex) {
			return new HashMap<>();
		}
	}
}
