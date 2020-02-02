package com.polaris.container.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.polaris.container.annotation.PolarisApplication;
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
		ComponentScan scanAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, ComponentScan.class);
		if (rootConfigClass != null) {
			if (scanAnnotation != null) {
				String[] tempBasePackages = scanAnnotation.basePackages();
				if (tempBasePackages != null && tempBasePackages.length > 0) {
					for (String basePackage : tempBasePackages) {
						basePackages.add(basePackage);
					}
				}
				Class<?>[] tempBasePackageClasses = scanAnnotation.basePackageClasses();
				if (tempBasePackageClasses != null && tempBasePackageClasses.length > 0) {
					for (Class<?> basePackageClass : tempBasePackageClasses) {
						basePackages.add(basePackageClass.getPackage().getName());
					}
				}
			}
		}
		if (basePackages.size() == 0) {
			//basePackages.add(InnerConfiguration.BASE_PACKAGE);
			if (rootConfigClass != null) {
				basePackages.add(rootConfigClass.getPackage().getName());
			}
		}
		
		//mapper-scan
		if (rootConfigClass != null) {
			PolarisApplication polarisAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, PolarisApplication.class);
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
	public static class InnerConfiguration {
		@Bean
		public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
			return new ConfPropertyPlaceholderConfigurer();
		}
	}
	
}
