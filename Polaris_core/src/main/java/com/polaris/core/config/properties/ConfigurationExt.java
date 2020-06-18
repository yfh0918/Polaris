package com.polaris.core.config.properties;

import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;

import com.polaris.core.config.Config.Type;
import com.polaris.core.config.annotation.PolarisConfigurationExt;

public class ConfigurationExt extends ConfigurationAbs{
	public static final String BEAN_NAME = ConfigurationExt.class.getName();
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		PolarisConfigurationExt annotation = getAnnotation(bean, beanName, PolarisConfigurationExt.class);
		if (annotation != null) {
			init(bean,annotation.value(),Type.EXT);
		}
		return bean;
	}
	
}
