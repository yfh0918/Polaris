package com.polaris.demo.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc 
@ComponentScan("com.polaris.demo.rest.controller")
public class WebConfig extends WebMvcConfigurerAdapter {
	
	
	@Bean
    public ViewResolver viewResolver() {
		 InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		 resolver.setPrefix("/WEB-INF/views/");
		 resolver.setSuffix(".jsp");
		 resolver.setExposeContextBeansAsAttributes(true);
		 return resolver;
	}

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    
        registry.addResourceHandler("/resource/img/**")
                .addResourceLocations("file:/var/www/resources/upload/distribution/img/");
        
        registry.addResourceHandler("/resource/file/**")
        .addResourceLocations("file:/var/www/resources/upload/distribution/file/");
        super.addResourceHandlers(registry);
    }

	 //配置静态资源的处理
	 @Override
	 public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		 configurer.enable();
	 }

}
