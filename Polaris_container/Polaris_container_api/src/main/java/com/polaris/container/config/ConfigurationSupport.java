package com.polaris.container.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.config.ConfPropertyPlaceholderConfigurer;

abstract public class ConfigurationSupport {

	private static List<Class<?>> configClassList = new ArrayList<>();
	private static String[] args;
	private static Set<String> defaultBasePackagesForMapper = new HashSet<>();
	
	public static void add(String[] arg, Class<?>... clazz) {
		
		//设置默认的mapper-scan
		if (clazz != null) {
			for (Class<?> clazz0 : clazz) {
				defaultBasePackagesForMapper.add(clazz0.getPackage().getName()+".**.mapper");
			}
		}
		
		//设置
		args = arg;
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
			configClassList.add(configurationExtension.getClass());
        }
	}

	public static String[] getArgs() {
		return args;
	}
	public static Set<String> getDefaultBasePackagesForMapper() {
		return defaultBasePackagesForMapper;
	}
	
	@Configuration
	public static class ConfPropertyConfiguration {
		@Bean
		public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
			return new ConfPropertyPlaceholderConfigurer();
		}
	}
}
