package com.polaris.core.util;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentUtil {
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Map<String, String> getSystemProperties() {
		try {
			return (Map) System.getProperties();
		}
		catch (AccessControlException ex) {
			return new HashMap<>();
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
