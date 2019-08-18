package com.polaris.springmvc;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;

import com.polaris.core.config.ConfClient;
import com.polaris.http.initializer.AbsHttpInitializer;

public class Initializer extends  AbsHttpInitializer { 

	@Override
	public void addInitParameter() {
		servletContext.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
		servletContext.setInitParameter("spring.profiles.default", "production");
	}

	@Override
	public void addListener() {
		servletContext.addListener(org.springframework.web.context.request.RequestContextListener.class);
		servletContext.addListener(org.springframework.web.context.ContextLoaderListener.class);
	}

	@Override
	public void addFilter() {
		FilterRegistration.Dynamic filter = servletContext.addFilter("encodingFilter", org.springframework.web.filter.CharacterEncodingFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		filter.setInitParameter("encoding", "UTF-8");
		filter.setInitParameter("forceEncoding", "true");
	}

	@Override
	public void addServlet() {
		ServletRegistration.Dynamic servletRegistration = servletContext.
	    addServlet("dispatcher", org.springframework.web.servlet.DispatcherServlet.class);
	    servletRegistration.setInitParameter("contextConfigLocation", "classpath*:/spring-context-mvc.xml");
	    servletRegistration.setLoadOnStartup(1);
	    servletRegistration.addMapping("/*");	
	    servletContext.setSessionTimeout(Integer.parseInt(ConfClient.get("session.config.timeout", "1440")));
	} 
	

}
