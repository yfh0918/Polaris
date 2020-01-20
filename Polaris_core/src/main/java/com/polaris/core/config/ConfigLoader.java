package com.polaris.core.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.ComponentScan;

import com.polaris.core.annotation.PolarisApplication;

abstract public class ConfigLoader {

	private static Class<?> rootConfigClass = null;
	private static Set<String> basePackages = new HashSet<>();
	private static Set<String> basePackagesForMapper = new HashSet<>();
	
	public static void loadRootConfig(Class<?> clazz) {
		rootConfigClass = clazz;
		
		//application-scan
		if (rootConfigClass != null) {
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
		}
		if (basePackages.size() == 0) {
			basePackages.add(DefaultConfig.BASE_PACKAGE);
			if (rootConfigClass != null) {
				basePackages.add(rootConfigClass.getPackage().getName());
			}
		}
		
		//mapper-scan
		if (rootConfigClass != null) {
			PolarisApplication polarisAnnotation = rootConfigClass.getAnnotation(PolarisApplication.class);
			if (polarisAnnotation != null) {
				String[] tempPasePackagesForMapper = polarisAnnotation.scanBasePackagesForMapper();
				if (tempPasePackagesForMapper != null && tempPasePackagesForMapper.length > 0) {
					for (String basePackageForMapper : tempPasePackagesForMapper) {
						basePackagesForMapper.add(basePackageForMapper);
					}
				}
			}
		}
		if (basePackagesForMapper.size() == 0) {
			for(String basePackage : basePackages) {
				basePackagesForMapper.add(basePackage+".**.mapper");
			}
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
	public static Set<String> getBasePackagesForMapper() {
		return basePackagesForMapper;
	}
	
}
