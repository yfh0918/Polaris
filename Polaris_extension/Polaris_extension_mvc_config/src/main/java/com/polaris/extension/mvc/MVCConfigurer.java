package com.polaris.extension.mvc;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.polaris.container.config.ConfigurationExtension;
import com.polaris.core.config.ConfClient;
import com.polaris.core.event.EventDispatcher;
import com.polaris.core.util.ReflectionUtil;

public class MVCConfigurer implements ConfigurationExtension {

	@Override
	public Class<?>[] getExtensionConfigurations() {
		return new Class<?>[]{SpringMvcConfigurer.class};
	}
	
	@Configuration 
	protected static class SpringMvcConfigurer implements WebMvcConfigurer{
		
		@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
	    public MultipartResolver multipartResolver() {
			MVCConfigurerEvent event = new MVCConfigurerEvent(MultipartResolver.class);
			EventDispatcher.fireEvent(event);
			MultipartResolver result = event.getEventResult(MultipartResolver.class);
			if (result != null) {
				return result;
			}
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
			MVCConfigurerEvent event = new MVCConfigurerEvent(FreeMarkerConfigurer.class);
			EventDispatcher.fireEvent(event);
			FreeMarkerConfigurer result = event.getEventResult(FreeMarkerConfigurer.class);
			if (result != null) {
				return result;
			}
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
			MVCConfigurerEvent event = new MVCConfigurerEvent(FreeMarkerViewResolver.class);
			EventDispatcher.fireEvent(event);
			FreeMarkerViewResolver result = event.getEventResult(FreeMarkerViewResolver.class);
			if (result != null) {
				return result;
			}
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
	    	
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(converters);
			EventDispatcher.fireEvent(event);
			
			//默认事件
			if (event.getEventResult() == null) {
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
	    
	    @Override
		public void configurePathMatch(PathMatchConfigurer configurer) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(configurer);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Configure content negotiation options.
		 */
	    @Override
		public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(configurer);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Configure asynchronous request handling options.
		 */
	    @Override
		public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(configurer);
			EventDispatcher.fireEvent(event);
		}



		/**
		 * Add {@link Converter Converters} and {@link Formatter Formatters} in addition to the ones
		 * registered by default.
		 */
	    @Override
		public void addFormatters(FormatterRegistry registry) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(registry);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Add Spring MVC lifecycle interceptors for pre- and post-processing of
		 * controller method invocations and resource handler requests.
		 * Interceptors can be registered to apply to all requests or be limited
		 * to a subset of URL patterns.
		 */
	    @Override
		public void addInterceptors(InterceptorRegistry registry) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(registry);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Add handlers to serve static resources such as images, js, and, css
		 * files from specific locations under web application root, the classpath,
		 * and others.
		 */
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(registry);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Configure cross origin requests processing.
		 * @since 4.2
		 */
		@Override
		public void addCorsMappings(CorsRegistry registry) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(registry);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Configure simple automated controllers pre-configured with the response
		 * status code and/or a view to render the response body. This is useful in
		 * cases where there is no need for custom controller logic -- e.g. render a
		 * home page, perform simple site URL redirects, return a 404 status with
		 * HTML content, a 204 with no content, and more.
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(registry);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Configure view resolvers to translate String-based view names returned from
		 * controllers into concrete {@link org.springframework.web.servlet.View}
		 * implementations to perform rendering with.
		 * @since 4.1
		 */
		@Override
		public void configureViewResolvers(ViewResolverRegistry registry) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(registry);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Add resolvers to support custom controller method argument types.
		 * <p>This does not override the built-in support for resolving handler
		 * method arguments. To customize the built-in support for argument
		 * resolution, configure {@link RequestMappingHandlerAdapter} directly.
		 * @param resolvers initially an empty list
		 */
		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(resolvers);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Add handlers to support custom controller method return value types.
		 * <p>Using this option does not override the built-in support for handling
		 * return values. To customize the built-in support for handling return
		 * values, configure RequestMappingHandlerAdapter directly.
		 * @param handlers initially an empty list
		 */
		@Override
		public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(handlers);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * A hook for extending or modifying the list of converters after it has been
		 * configured. This may be useful for example to allow default converters to
		 * be registered and then insert a custom converter through this method.
		 * @param converters the list of configured converters to extend.
		 * @since 4.1.3
		 */
		@Override
		public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(converters);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Configure exception resolvers.
		 * <p>The given list starts out empty. If it is left empty, the framework
		 * configures a default set of resolvers, see
		 * {@link WebMvcConfigurationSupport#addDefaultHandlerExceptionResolvers(List, org.springframework.web.accept.ContentNegotiationManager)}.
		 * Or if any exception resolvers are added to the list, then the application
		 * effectively takes over and must provide, fully initialized, exception
		 * resolvers.
		 * <p>Alternatively you can use
		 * {@link #extendHandlerExceptionResolvers(List)} which allows you to extend
		 * or modify the list of exception resolvers configured by default.
		 * @param resolvers initially an empty list
		 * @see #extendHandlerExceptionResolvers(List)
		 * @see WebMvcConfigurationSupport#addDefaultHandlerExceptionResolvers(List, org.springframework.web.accept.ContentNegotiationManager)
		 */
		@Override
		public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(resolvers);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Extending or modify the list of exception resolvers configured by default.
		 * This can be useful for inserting a custom exception resolver without
		 * interfering with default ones.
		 * @param resolvers the list of configured resolvers to extend
		 * @since 4.3
		 * @see WebMvcConfigurationSupport#addDefaultHandlerExceptionResolvers(List, org.springframework.web.accept.ContentNegotiationManager)
		 */
		@Override
		public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(resolvers);
			EventDispatcher.fireEvent(event);
		}

		/**
		 * Provide a custom {@link Validator} instead of the one created by default.
		 * The default implementation, assuming JSR-303 is on the classpath, is:
		 * {@link org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean}.
		 * Leave the return value as {@code null} to keep the default.
		 */
		@Nullable
		@Override
		public Validator getValidator() {
	    	MVCConfigurerEvent event = new MVCConfigurerEvent(Validator.class);
			EventDispatcher.fireEvent(event);
			Validator result = event.getEventResult(Validator.class);
			if (result != null) {
				return result;
			}
			return null;
		}

		/**
		 * Provide a custom {@link MessageCodesResolver} for building message codes
		 * from data binding and validation error codes. Leave the return value as
		 * {@code null} to keep the default.
		 */
		@Nullable
		@Override
		public MessageCodesResolver getMessageCodesResolver() {
			MVCConfigurerEvent event = new MVCConfigurerEvent(MessageCodesResolver.class);
			EventDispatcher.fireEvent(event);
			MessageCodesResolver result = event.getEventResult(MessageCodesResolver.class);
			if (result != null) {
				return result;
			}
			return null;
		}
	}

}
