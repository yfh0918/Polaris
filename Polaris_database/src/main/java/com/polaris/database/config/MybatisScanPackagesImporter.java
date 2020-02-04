package com.polaris.database.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.polaris.database.annotation.EnablePolarisDB;

public class MybatisScanPackagesImporter implements ImportBeanDefinitionRegistrar{
	private static String[] scanBasePackages;
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				importingClassMetadata.getAnnotationAttributes(EnablePolarisDB.class.getName()));
		scanBasePackages = attributes.getStringArray("scanBasePackages");
	}
	
	public static String[] getScanBasePackages() {
		return scanBasePackages;
	}
}
