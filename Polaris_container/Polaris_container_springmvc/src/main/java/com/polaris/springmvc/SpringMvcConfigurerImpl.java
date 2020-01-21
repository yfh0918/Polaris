package com.polaris.springmvc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

@Configuration 
@EnableWebMvc
public class SpringMvcConfigurerImpl implements WebMvcConfigurer {
	
	@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding(Charset.defaultCharset().toString());
        long maxUploadSize = Long.parseLong(ConfClient.get("multipart.maxUploadSize","1073741824"));
        multipartResolver.setMaxUploadSize(maxUploadSize);//1G
        int maxInMemorySize = Integer.parseInt(ConfClient.get("multipart.maxInMemorySize","40960"));
        multipartResolver.setMaxInMemorySize(maxInMemorySize);
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
		String templateLoaderPath = ConfClient.get("freeMarker.templateLoaderPath", "classpath:template/");
		freeMarkerConfigurer.setTemplateLoaderPath(templateLoaderPath);
		freeMarkerConfigurer.setDefaultEncoding(Charset.defaultCharset().toString());
	    return freeMarkerConfigurer;
   }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
	    FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
	    String freeMarkerViewResolverSuffix = ConfClient.get("freeMarker.viewResolver.suffix", ".ftl");
	    viewResolver.setSuffix(freeMarkerViewResolverSuffix);
	    String freeMarkerViewResolverprefix = ConfClient.get("freeMarker.viewResolver.prefix");
	    if (StringUtil.isNotEmpty(freeMarkerViewResolverprefix)) {
	    	viewResolver.setPrefix(freeMarkerViewResolverprefix);
	    }
	    viewResolver.setCache(Boolean.parseBoolean(ConfClient.get("freeMarker.viewResolver.cache","true")));
	    viewResolver.setContentType("text/html;charset="+Charset.defaultCharset().toString());
	    viewResolver.setExposeRequestAttributes(Boolean.parseBoolean(ConfClient.get("freeMarker.viewResolver.exposeRequestAttributes","true")));
	    viewResolver.setExposeSessionAttributes(Boolean.parseBoolean(ConfClient.get("freeMarker.viewResolver.exposeSessionAttributes","true")));
	    viewResolver.setRequestContextAttribute("request");
	    viewResolver.setOrder(0);
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
