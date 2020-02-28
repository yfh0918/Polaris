package com.polaris.demo.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@Configuration
public class SwaggerConfig {
	
	@Bean
    public ApiListingResource getApiListingResourceJSON() {
		return new io.swagger.jaxrs.listing.ApiListingResource();
    }
	
	@Bean
    public SwaggerSerializers getApiDeclarationProvider() {
		return new io.swagger.jaxrs.listing.SwaggerSerializers();
    }
	
	@Bean
    public BeanConfig getBeanConfig() {
		BeanConfig beanConfig = new io.swagger.jaxrs.config.BeanConfig();
		beanConfig.setTitle("Swagger Sample App");
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[]{"http"});
		beanConfig.setResourcePackage("com.polaris");
		beanConfig.setScan(true);
		beanConfig.setPrettyPrint("true");
        
		return beanConfig;
    }
}
