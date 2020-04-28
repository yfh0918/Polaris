package com.polaris.container.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.config.value.SpringAutoUpdateConfigChangeListener;
import com.polaris.core.config.value.SpringPlaceholderConfigurer;
import com.polaris.core.config.value.SpringValueProcessor;

abstract public class ConfigurationHelper {

	private static List<Class<?>> configClassList = new ArrayList<>();
	private static String[] args;
	private static Class<?>[] classes;
	
	public static void init(String[] arg, Class<?>... clazz) {
		//设置
		args = arg;
		classes = clazz;
		addConfiguration(ConfPropertyConfiguration.class);
		addConfiguration(clazz);
		addConfigurationExtension();
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

	public static String[] getArgs() {
		return args;
	}
	public static Class<?>[] getClasses() {
		return classes;
	}
	
	@Configuration
	protected static class ConfPropertyConfiguration {
		@Bean
		public static SpringPlaceholderConfigurer propertyPlaceholderConfigurer() {
			return new SpringPlaceholderConfigurer();
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
