package com.polaris.core.config;

import java.util.HashSet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.util.StringValueResolver;

/**
 * rewrite PropertyPlaceholderConfigurer
 * @version 1.0
 *
 * <bean id="confPropertyPlaceholderConfigurer" class="com.polaris_conf_core.spring.ConfPropertyPlaceholderConfigurer" />
 *
 */
public class ConfPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, ConfigurablePropertyResolver propertyResolver) throws BeansException {
		
		
		// init value resolver
		StringValueResolver valueResolver = new StringValueResolver() {
			@Override
			public String resolveStringValue(String strVal) {
				return ConfPlaceholderHelper.parseStringValue(strVal, new HashSet<String>()).trim();
			}
		};
		super.doProcessProperties(beanFactoryToProcess, valueResolver);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}


	@Override
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		super.setIgnoreUnresolvablePlaceholders(true);
	}

}
