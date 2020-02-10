package com.polaris.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//支持静态资源访问
		registry.addResourceHandler("/**")
        	.addResourceLocations("classpath:/resources/")
        	.addResourceLocations("classpath:/static/")
        	.addResourceLocations("classpath:/public/");
	}
}
