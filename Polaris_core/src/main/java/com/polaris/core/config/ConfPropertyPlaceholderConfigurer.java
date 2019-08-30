package com.polaris.core.config;

import java.util.HashSet;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * rewrite PropertyPlaceholderConfigurer
 * @version 1.0
 *
 * <bean id="confPropertyPlaceholderConfigurer" class="com.polaris_conf_core.spring.ConfPropertyPlaceholderConfigurer" />
 *
 */
@Component
public class ConfPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		
		
		// init value resolver
		StringValueResolver valueResolver = new StringValueResolver() {
			@Override
			public String resolveStringValue(String strVal) {
				return PlaceholderHelper.parseStringValue(strVal, new HashSet<String>()).trim();
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
