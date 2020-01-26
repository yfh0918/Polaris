package com.polaris.container.servlet.springmvc;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.ReflectionUtil;

@Configuration 
@EnableWebMvc
public class SpringMvcConfigurerImpl implements WebMvcConfigurer {
	
	@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		ReflectionUtils.doWithMethods(CommonsMultipartResolver.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, multipartResolver, "multipartResolver.");
			}
		});
		
		//存在默认值
        multipartResolver.setDefaultEncoding(Charset.defaultCharset().toString());
        multipartResolver.setMaxUploadSize(Long.parseLong(ConfClient.get("multipartResolver.maxUploadSize","1073741824")));//1G
        multipartResolver.setMaxInMemorySize(Integer.parseInt(ConfClient.get("multipartResolver.maxInMemorySize","40960")));
        return multipartResolver;
    }
	
	/**
       * 
       * Description：FreeMarker视图配置
       * @return 
       */
	@Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
		ReflectionUtils.doWithMethods(FreeMarkerConfigurer.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, freeMarkerConfigurer, "freeMarkerConfigurer.");
			}
		});

		//存在默认值
		freeMarkerConfigurer.setTemplateLoaderPath(ConfClient.get("freeMarkerConfigurer.templateLoaderPath", "classpath:template/"));
		freeMarkerConfigurer.setDefaultEncoding(Charset.defaultCharset().toString());
	    return freeMarkerConfigurer;
   }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
	    FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
	    
		ReflectionUtils.doWithMethods(FreeMarkerViewResolver.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, viewResolver, "viewResolver.");
			}
		});
		
		//存在默认值
	    viewResolver.setSuffix(ConfClient.get("viewResolver.suffix", ".ftl"));
	    viewResolver.setCache(Boolean.parseBoolean(ConfClient.get("viewResolver.cache","true")));
	    viewResolver.setContentType("text/html;charset="+Charset.defaultCharset().toString());
	    viewResolver.setExposeRequestAttributes(Boolean.parseBoolean(ConfClient.get("viewResolver.exposeRequestAttributes","true")));
	    viewResolver.setExposeSessionAttributes(Boolean.parseBoolean(ConfClient.get("viewResolver.exposeSessionAttributes","true")));
	    viewResolver.setRequestContextAttribute(ConfClient.get("viewResolver.requestContextAttribute", "request"));
	    viewResolver.setOrder(Integer.parseInt(ConfClient.get("viewResolver.order", "0")));
	    return viewResolver;
   }
    
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	
    	//string utf-8
        converters.add(new StringHttpMessageConverter(Charset.defaultCharset()));
        
        //pojo utf-8
    	List<MediaType> list = new ArrayList<MediaType>();
    	list.add(MediaType.APPLICATION_JSON);
    	MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
    	mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
        converters.add(mappingJackson2HttpMessageConverter);
    }
    
}
