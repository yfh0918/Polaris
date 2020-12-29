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

public class ConfigurationProxy {
    
    public static ConfigurationProxy INSTANCE = new ConfigurationProxy();
    private ConfigurationProxy() {}

	private List<Class<?>> configClassList = new ArrayList<>();
	private String[] args;
	private Class<?>[] classes;
	private Set<String> basePackageSet = new HashSet<>();
	
	public void init(String[] arg, Class<?>... clazz) {
		//设置
		args = arg;
		classes = clazz;
		addConfiguration(ConfPropertyConfiguration.class);
		addConfiguration(clazz);
		addConfigurationExtension();
		addBasePackage(clazz);
	}
	
	private Class<?>[] getConfiguration() {
		Class<?>[] returnClass = new Class[configClassList.size()];
		return configClassList.toArray(returnClass);
	}
    public Class<?>[] getConfiguration(Class<?>... clazz) {
    	addConfiguration(clazz);
    	return getConfiguration();
 	} 
    public void addConfiguration(Class<?>... clazz) {
    	if (clazz != null && clazz.length > 0) {
    		for (Class<?> clazz0 : clazz) {
    			configClassList.add(clazz0);
    		}
    	}
 	}
	private void addConfigurationExtension() {
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
	private void addBasePackage(Class<?>... clazz) {
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
	public String[] getArgs() {
		return args;
	}
	public Class<?>[] getClasses() {
		return classes;
	}
	public Set<String> getBasePackageSet() {
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
