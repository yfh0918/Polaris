package com.polaris.database.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.polaris.container.config.ConfigurationSupport;
import com.polaris.database.annotation.EnablePolarisDB;



public class MybatisScanPackagesImportSelector implements ImportBeanDefinitionRegistrar{
	private static String[] scanBasePackages;
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				importingClassMetadata.getAnnotationAttributes(EnablePolarisDB.class.getName()));
		scanBasePackages = attributes.getStringArray("scanBasePackages");
	}
	
	public static Set<String> getScanBasePackages() {
        if (scanBasePackages != null && scanBasePackages.length > 0) {
        	return new HashSet<>(Arrays.asList(scanBasePackages));
        }
        return ConfigurationSupport.getBasePackagesForMapper();

	}
}
