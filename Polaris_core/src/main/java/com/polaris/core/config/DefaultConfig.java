package com.polaris.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={DefaultConfig.BASE_PACKAGE})
public class DefaultConfig {
	public static final String BASE_PACKAGE = "com.polaris";
	@Bean
	public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new ConfPropertyPlaceholderConfigurer();
	}
}
