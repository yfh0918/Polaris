package com.polaris.springmvc;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.polaris.http.initializer.WebConfigInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	private static boolean initialized = false;

	@Override
	public synchronized void onStartup(ServletContext servletContext) throws ServletException {
		if (initialized) {
			return;
		}
		initialized = true;
		super.onStartup(servletContext);
	}
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return WebConfigInitializer.getRootConfigs();
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return WebConfigInitializer.getWebConfigs();
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	public static boolean isInitialized () {
		return initialized;
	}
	

}
