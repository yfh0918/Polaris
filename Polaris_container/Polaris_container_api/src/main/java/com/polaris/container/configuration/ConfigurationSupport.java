package com.polaris.container.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.annotation.PolarisWebApplication;
import com.polaris.core.config.ConfPropertyPlaceholderConfigurer;

abstract public class ConfigurationSupport {

	private static Class<?> rootConfigClass = null;
	private static Set<String> basePackages = new HashSet<>();
	private static Set<String> basePackagesForMapper = new HashSet<>();
	private static String[] args;
	
	public static void set(Class<?> clazz, String... arg) {
		args = arg;
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
			basePackages.add(InnerConfiguration.BASE_PACKAGE);
			if (rootConfigClass != null) {
				basePackages.add(rootConfigClass.getPackage().getName());
			}
		}
		
		//mapper-scan
		if (rootConfigClass != null) {
			PolarisWebApplication polarisAnnotation = rootConfigClass.getAnnotation(PolarisWebApplication.class);
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
	public static Class<?>[] getConfiguration() {
		if (rootConfigClass == null) {
			return new Class[] {InnerConfiguration.class};
		}
		return new Class[] {InnerConfiguration.class, rootConfigClass};
	}
	public static Set<String> getBasePackages() {
		return basePackages;
	}
	public static Set<String> getBasePackagesForMapper() {
		return basePackagesForMapper;
	}
	public static String[] getArgs() {
		return args;
	}
	
	@Configuration
	@ComponentScan(basePackages={InnerConfiguration.BASE_PACKAGE})
	public static class InnerConfiguration {
		public static final String BASE_PACKAGE = "com.polaris";
		@Bean
		public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
			return new ConfPropertyPlaceholderConfigurer();
		}
	}
	
}
