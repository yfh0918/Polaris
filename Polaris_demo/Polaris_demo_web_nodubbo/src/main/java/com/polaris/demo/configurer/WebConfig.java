//package com.polaris.demo.configurer;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.multipart.MultipartResolver;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration 
//public class WebConfig extends WebMvcConfigurerAdapter {
//	
//	@Value("${uploadFilePath}")
//	private String uploadFilePath;
//	
//	@Value("${uploadImgPath}")
//	private String uploadImgPath;
//	

//	
//	@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	    
//		//支持静态资源访问
//		registry.addResourceHandler("/**")
//        	.addResourceLocations("classpath:/resources/")
//        	.addResourceLocations("classpath:/static/")
//        	.addResourceLocations("classpath:/public/");
//		
//		//静态文件映射
//        registry.addResourceHandler("/resource/img/**")
//        	.addResourceLocations("file:"+uploadImgPath+File.separator);
//        registry.addResourceHandler("/resource/file/**")
//        	.addResourceLocations("file:"+uploadFilePath+File.separator);
//        super.addResourceHandlers(registry);
//    }
//

//}
