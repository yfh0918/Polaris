package com.polaris.sentinel;

import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;

public class ServletFilterInitializer implements  ServletContainerInitializer { 
	private final String SENTINEL_SERVLET_FILTER = "sentinelServletFilter";

	public void onStartup(Set<Class<?>> commonFilters, ServletContext servletContext)  throws ServletException {
		servletContext.addFilter(SENTINEL_SERVLET_FILTER, new CommonFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	} 
}
