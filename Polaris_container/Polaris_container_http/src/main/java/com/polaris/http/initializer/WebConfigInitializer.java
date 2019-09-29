package com.polaris.http.initializer;

import java.util.HashSet;
import java.util.Set;

abstract public class WebConfigInitializer {

	private static Set<Class<?>> rootConfigs = new HashSet<>();
	private static Set<Class<?>> webConfigs = new HashSet<>();
	
	public static void loadRootConfig(Class<?>... clazzs) {
		if (clazzs != null) {
			for (Class<?> clazz : clazzs) {
				rootConfigs.add(clazz);
			}
		}
	}
	public static void loadWebConfig(Class<?>... clazzs) {
		if (clazzs != null) {
			for (Class<?> clazz : clazzs) {
				webConfigs.add(clazz);
			}
		}
	}

	public static Class<?>[] getRootConfigs() {
		return rootConfigs.toArray(new Class[0]);
	}
	
	public static Class<?>[] getWebConfigs() {
		if (webConfigs.size() > 0) {
			return webConfigs.toArray(new Class[0]);
		}
		return null;
	}

}
