package com.polaris.core.config.value;

import java.util.HashSet;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.util.StringValueResolver;

/**
 * rewrite PropertySourcesPlaceholderConfigurer
 * @version 1.0
 */
public class SpringPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

    private final Properties properties;
    public SpringPlaceholderConfigurer(Properties properties) {
        this.properties = properties;
    }
    
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, ConfigurablePropertyResolver propertyResolver) throws BeansException {
		StringValueResolver valueResolver = new StringValueResolver() {
			@Override
			public String resolveStringValue(String strVal) {
				return SpringPlaceholderHelper.parseStringValue(properties, strVal, new HashSet<String>()).trim();
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
