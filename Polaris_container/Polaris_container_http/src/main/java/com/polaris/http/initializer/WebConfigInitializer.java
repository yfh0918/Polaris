package com.polaris.http.initializer;

abstract public class WebConfigInitializer {

	private static Class<?> rootConfigClass = null;
	private static String scanPath = null;
	
	public static void loadRootConfig(Class<?> clazz) {
		rootConfigClass = clazz;
		if (rootConfigClass != null) {
			scanPath = rootConfigClass.getPackage().getName();
		}
	}
	public static Class<?> getRootConfigClass() {
		return rootConfigClass;
	}
	public static String getDefaultScanPath() {
		return scanPath;
	}
	
}
