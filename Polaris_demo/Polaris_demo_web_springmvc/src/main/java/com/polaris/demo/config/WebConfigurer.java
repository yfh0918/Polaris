package com.polaris.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.polaris.extension.mvc.AbstractMVCConfigurerEventListener;
import com.polaris.extension.mvc.MVCConfigurerEvent;

@Configuration
public class WebConfigurer extends AbstractMVCConfigurerEventListener implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//支持静态资源访问
		registry.addResourceHandler("/**")
        	.addResourceLocations("classpath:/resources/")
        	.addResourceLocations("classpath:/static/")
        	.addResourceLocations("classpath:/public/");
	}

	@Override
	protected void onMVCEvent(MVCConfigurerEvent event) {
		
//		//重新构建
//		if (event.getEventType() == HttpMessageConverter.class) {
//			List<HttpMessageConverter<?>> converters = event.getEventParameter(List.class);
//			event.setEventResult(converters);
//		} else if (event.getEventType() == MultipartResolver.class) {
//			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//			event.setEventResult(multipartResolver);
//		} else if (event.getEventType() == FreeMarkerConfigurer.class) {
//			FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
//			event.setEventResult(freeMarkerConfigurer);
//			
//		} else if (event.getEventType() == FreeMarkerViewResolver.class) {
//			FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
//			event.setEventResult(viewResolver);
//		}
		
	}
}
