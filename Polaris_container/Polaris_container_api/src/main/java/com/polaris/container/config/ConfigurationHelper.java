package com.polaris.container.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.polaris.core.config.ConfClient;
import com.polaris.core.config.spring.value.SpringAutoUpdateConfigChangeListener;
import com.polaris.core.config.spring.value.SpringPlaceholderConfigurer;
import com.polaris.core.config.spring.value.SpringValueProcessor;

abstract public class ConfigurationHelper {

	private static List<Class<?>> configClassList = new ArrayList<>();
	private static String[] args;
	private static Class<?>[] classes;
	private static Set<String> basePackageSet = new HashSet<>();
    //private static Set<Class<?>> basePackageClassesSet = new HashSet<>();
	
	public static void init(String[] arg, Class<?>... clazz) {
		//设置
		args = arg;
		classes = clazz;
		addConfiguration(ConfPropertyConfiguration.class);
		addConfiguration(clazz);
		addConfigurationExtension();
		addBasePackage(clazz);
	}
	public static Class<?>[] getConfiguration() {
		Class<?>[] returnClass = new Class[configClassList.size()];
		return configClassList.toArray(returnClass);
	}
    public static Class<?>[] getConfiguration(Class<?>... clazz) {
    	addConfiguration(clazz);
    	return getConfiguration();
 	} 
    public static void addConfiguration(Class<?>... clazz) {
    	if (clazz != null && clazz.length > 0) {
    		for (Class<?> clazz0 : clazz) {
    			configClassList.add(clazz0);
    		}
    	}
 	}
	private static void addConfigurationExtension() {
		ServiceLoader<ConfigurationExtension> configurationExtensions = ServiceLoader.load(ConfigurationExtension.class);
		for (ConfigurationExtension configurationExtension : configurationExtensions) {
			Class<?>[] classes = configurationExtension.getConfigurations();
			if (classes != null && classes.length > 0) {
				for (Class<?> clazz : classes) {
					configClassList.add(clazz);
				}
			}
        }
	}
	private static void addBasePackage(Class<?>... clazz) {
		if (clazz != null && clazz.length > 0) {
    		for (Class<?> clazz0 : clazz) {
    		    ComponentScan componentScan = AnnotationUtils.findAnnotation(clazz0, ComponentScan.class);
    		    if (componentScan != null) {
    		        for (String basePackage : componentScan.basePackages()) {
    		            basePackageSet.add(basePackage);
    		        }
    		        for (Class<?> basePackageClass : componentScan.basePackageClasses()) {
    		            basePackageSet.add(ClassUtils.getPackageName(basePackageClass));
    		        }
                    basePackageSet.add(clazz0.getPackage().getName());
    		    }
    		}
    	}
	}
	public static String[] getArgs() {
		return args;
	}
	public static Class<?>[] getClasses() {
		return classes;
	}
	public static Set<String> getBasePackageSet() {
		return basePackageSet;
	}
	
	@Configuration
	protected static class ConfPropertyConfiguration {
		@Bean
		public static SpringPlaceholderConfigurer propertyPlaceholderConfigurer() {
			return new SpringPlaceholderConfigurer(ConfClient.get());
		}
		@Bean
		public static SpringAutoUpdateConfigChangeListener springAutoUpdateConfigChangeListener() {
			return new SpringAutoUpdateConfigChangeListener();
		}
		@Bean
		public static SpringValueProcessor springValueProcessor() {
			return new SpringValueProcessor();
		}
	}
	
}
