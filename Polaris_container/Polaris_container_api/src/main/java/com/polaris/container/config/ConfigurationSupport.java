package com.polaris.container.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.polaris.core.config.ConfPropertyPlaceholderConfigurer;

abstract public class ConfigurationSupport {

	private static List<Class<?>> configClassList = new ArrayList<>();
	private static Set<String> basePackages = new HashSet<>();
	private static String[] args;
	private static Set<String> basePackagesForMapper = new HashSet<>();
	
	public static void set(Class<?> clazz, String... arg) {
		
		//application-scan
		ComponentScan scanAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, ComponentScan.class);
		if (clazz != null) {
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
			if (clazz != null) {
				basePackages.add(clazz.getPackage().getName());
			}
		}
		
		//设置默认的mapper-scan
		for(String basePackage : basePackages) {
			basePackagesForMapper.add(basePackage+".**.mapper");
		}
		
		//设置
		args = arg;
		configClassList.add(ConfPropertyConfiguration.class);
		configClassList.add(clazz);
		addConfigurationExtension();
	}
	public static Class<?>[] getConfiguration() {
		Class<?>[] returnClass = new Class[configClassList.size()];
		return configClassList.toArray(returnClass);
	}
    public static Class<?>[] getConfiguration(Class<?> clazz) {
    	configClassList.add(clazz);
    	return getConfiguration();
 	} 

	public static Set<String> getBasePackages() {
		return basePackages;
	}

	public static String[] getArgs() {
		return args;
	}
	public static Set<String> getBasePackagesForMapper() {
		return basePackagesForMapper;
	}
	
	private static void addConfigurationExtension() {
		ServiceLoader<ConfigurationExtension> configurationExtensions = ServiceLoader.load(ConfigurationExtension.class);
		for (ConfigurationExtension configurationExtension : configurationExtensions) {
			configClassList.add(configurationExtension.getClass());
        }
	}
	
	@Configuration
	public static class ConfPropertyConfiguration {
		@Bean
		public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
			return new ConfPropertyPlaceholderConfigurer();
		}
	}
}
