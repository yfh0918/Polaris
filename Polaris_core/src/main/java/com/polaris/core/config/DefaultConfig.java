package com.polaris.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"com.polaris"})
public class DefaultConfig {
	@Bean
	public static ConfPropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new ConfPropertyPlaceholderConfigurer();
	}
}
