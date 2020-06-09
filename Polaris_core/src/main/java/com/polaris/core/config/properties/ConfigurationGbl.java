package com.polaris.core.config.properties;

import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;

import com.polaris.core.config.Config.Type;

public class ConfigurationGbl extends ConfigurationAbs {
	public static final String BEAN_NAME = ConfigurationGbl.class.getName();
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 2;
	}

	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		PolarisConfigurationGbl annotation = getAnnotation(bean, beanName, PolarisConfigurationGbl.class);
		if (annotation != null) {
			init(bean,annotation.value(),Type.GBL);
		}
		return bean;
	}
}
