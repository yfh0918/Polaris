package com.polaris.demo.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.polaris.container.servlet.springmvc.EnablePolarisSpringMvc;
import com.polaris.database.annotation.EnablePolarisDB;

@EnablePolarisSpringMvc
@EnablePolarisDB
@Configuration 
public class WebConfig implements WebMvcConfigurer {
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    
		//支持静态资源访问
		registry.addResourceHandler("/**")
        	.addResourceLocations("classpath:/resources/")
        	.addResourceLocations("classpath:/static/")
        	.addResourceLocations("classpath:/public/");
		
//		//静态文件映射
//        registry.addResourceHandler("/resource/img/**")
//        	.addResourceLocations("file:"+uploadImgPath+File.separator);
//        registry.addResourceHandler("/resource/file/**")
//        	.addResourceLocations("file:"+uploadFilePath+File.separator);
    }


}
