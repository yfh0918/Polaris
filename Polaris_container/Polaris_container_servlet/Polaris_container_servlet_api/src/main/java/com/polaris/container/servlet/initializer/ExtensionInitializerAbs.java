package com.polaris.container.servlet.initializer;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;

import com.polaris.container.servlet.filter.TraceFilter;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

public abstract class ExtensionInitializerAbs implements ExtensionInitializer {
	protected ServletContext servletContext = null;
	public void onStartup(ServletContext servletContext) {
		this.servletContext = servletContext;
		addInitParameter();
		addListener();
		addFilter();
		addServlet();
	}
	public void addInitParameter() {
		String names = ConfClient.get("servlet.init.parameter.names");
		String values = ConfClient.get("servlet.init.parameter.values");
		if (StringUtil.isNotEmpty(names) && StringUtil.isNotEmpty(values)) {
			String[] nameArray = names.split(",");
			String[] valueArray = values.split(",");
			for (int i0 = 0; i0 < nameArray.length; i0++) {
				servletContext.setInitParameter(nameArray[i0], valueArray[i0]);
			}
		}
	}
	public void addListener() {
		String listeners = ConfClient.get("servlet.listeners");
		if (StringUtil.isNotEmpty(listeners)) {
			String[] listenerArray = listeners.split(",");
			for (String listener : listenerArray) {
				servletContext.addListener(listener);
			}
		}
	}
	public void addFilter() {
		String TRACE_FILTER = "TraceFilter";
		
		// filter
		servletContext.addFilter(TRACE_FILTER, new TraceFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		//其他filter
		String names = ConfClient.get("servlet.filter.names");
		String values = ConfClient.get("servlet.filter.values");
		String urls = ConfClient.get("servlet.filter.urls");
		if (StringUtil.isNotEmpty(names) && StringUtil.isNotEmpty(values) && StringUtil.isNotEmpty(urls)) {
			String[] nameArray = names.split(",");
			String[] valueArray = values.split(",");
			String[] urlArray = urls.split(",");
			for (int i0 = 0; i0 < nameArray.length; i0++) {
				servletContext.addFilter(nameArray[i0], valueArray[i0])
				  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, urlArray[i0]);
			}
		}
		
	}
	public void addServlet(){
	}
}
