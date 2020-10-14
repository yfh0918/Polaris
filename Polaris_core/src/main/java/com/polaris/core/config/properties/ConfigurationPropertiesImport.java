package com.polaris.core.config.properties;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.AnnotationMetadata;

import com.polaris.core.config.ConfClient;
import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.annotation.PolarisConfigurationProperties;
import com.polaris.core.config.annotation.PolarisMultiConfigurationProperties;
import com.polaris.core.config.provider.ConfHandlerFactory;
import com.polaris.core.config.provider.ConfigFactory;
import com.polaris.core.exception.ConfigException;
import com.polaris.core.util.StringUtil;

public class ConfigurationPropertiesImport implements ImportBeanDefinitionRegistrar{
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		String beanName = ConfigurationProperties.BEAN_NAME;
		if (!registry.containsBeanDefinition(beanName)) {
			GenericBeanDefinition definition = new GenericBeanDefinition();
			definition.setBeanClass(ConfigurationProperties.class);
			definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition(beanName, definition);
		}
		Class<?> clazz = null;
		try {
		    clazz = Class.forName(importingClassMetadata.getClassName());
        } catch (ClassNotFoundException e) {
            throw new ConfigException(importingClassMetadata.getClassName()+" is not found");
        }
		PolarisConfigurationProperties annotation = AnnotatedElementUtils.findMergedAnnotation(clazz, PolarisConfigurationProperties.class);
		if (annotation != null) {
		    loadPropertiesFromAnnotation(annotation);
		}
		PolarisMultiConfigurationProperties annotations = AnnotatedElementUtils.findMergedAnnotation(clazz, PolarisMultiConfigurationProperties.class);
		if (annotations != null && annotations.value() != null && annotations.value().length > 0) {
            for (PolarisConfigurationProperties annotationElement : annotations.value()) {
                loadPropertiesFromAnnotation(annotationElement);
            }
        }
	}

	private void loadPropertiesFromAnnotation(PolarisConfigurationProperties annotation) {
        if (StringUtil.isNotEmpty(annotation.value())) {
            if (ConfigFactory.get(Type.EXT).getProperties(getPropertiesKey(annotation)) == null) {
                if (annotation.autoRefreshed()) {
                    if (ConfHandlerFactory.get(Type.EXT).getAndListen(getGroup(annotation),annotation.value())==null) {
                        throw new ConfigException("type:EXT file:" + getPropertiesKey(annotation) + " is not exsit");
                    }
                } else {
                    if (ConfHandlerFactory.get(Type.EXT).get(getGroup(annotation),annotation.value())==null) {
                        throw new ConfigException("type:EXT file:" + getPropertiesKey(annotation) + " is not exsit");
                    }
                }
                
            } 
        } 
    }
	
	private String getPropertiesKey(PolarisConfigurationProperties annotation) {
        return Config.merge(getGroup(annotation), annotation.value());
    }
	
	public static String getGroup(PolarisConfigurationProperties annotation) {
	    return ConfClient.getAppGroup(annotation.group());
	}

}
