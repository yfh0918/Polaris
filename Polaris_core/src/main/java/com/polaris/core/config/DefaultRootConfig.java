package com.polaris.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultRootConfig {
	@Bean
	public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new ConfPropertyPlaceholderConfigurer();
	}
}
