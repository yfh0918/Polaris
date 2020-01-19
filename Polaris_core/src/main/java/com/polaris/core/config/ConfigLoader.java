package com.polaris.core.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.ComponentScan;

abstract public class ConfigLoader {

	private static Class<?> rootConfigClass = null;
	private static Set<String> basePackages = new HashSet<>();
	
	public static void loadRootConfig(Class<?> clazz) {
		rootConfigClass = clazz;
		ComponentScan compoentScanAnnotation = rootConfigClass.getAnnotation(ComponentScan.class);
		if (compoentScanAnnotation != null) {
			String[] tempBasePackages = compoentScanAnnotation.basePackages();
			if (tempBasePackages != null && tempBasePackages.length > 0) {
				for (String basePackage : tempBasePackages) {
					basePackages.add(basePackage);
				}
			}
			Class<?>[] tempBasePackageClasses = compoentScanAnnotation.basePackageClasses();
			if (tempBasePackageClasses != null && tempBasePackageClasses.length > 0) {
				for (Class<?> basePackageClass : tempBasePackageClasses) {
					basePackages.add(basePackageClass.getPackage().getName());
				}
			}
		}
		if (basePackages.size() == 0) {
			basePackages.add(rootConfigClass.getPackage().getName());
		}
	}
	public static Class<?>[] getRootConfigClass() {
		if (rootConfigClass == null) {
			return new Class[] {DefaultConfig.class};
		}
		return new Class[] {DefaultConfig.class, rootConfigClass};
	}
	public static Set<String> getBasePackages() {
		return basePackages;
	}
	
}
