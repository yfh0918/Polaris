package com.polaris.springmvc;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.polaris.http.initializer.WebConfigInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

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

}
