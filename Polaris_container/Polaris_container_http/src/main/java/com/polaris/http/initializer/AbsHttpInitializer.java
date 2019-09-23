package com.polaris.http.initializer;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;

import com.polaris.core.config.ConfClient;
import com.polaris.http.filter.FlowControlFilter;
import com.polaris.http.filter.RequestFirstFilter;
import com.polaris.http.initializer.WebConfigInitializer.InnerFilter;

public abstract class AbsHttpInitializer implements HttpInitializer {
	protected ServletContext servletContext = null;
	public void onStartup(ServletContext servletContext) {
		this.servletContext = servletContext;
		loadContext();
		addInitParameter();
		addListener();
		addFilter();
		addServlet();
	}
	public abstract void loadContext();
	public void addInitParameter() {
		Map<String, String> initParameters = WebConfigInitializer.getInitParameters();
		if (initParameters != null) {
			for (Map.Entry<String, String> entry : initParameters.entrySet()) {
				servletContext.setInitParameter(entry.getKey(), entry.getValue());
			}
		}
	}
	public void addListener() {
		List<ServletContextListener> listeners = WebConfigInitializer.getListeners();
		if (listeners != null) {
			for (ServletContextListener listener : listeners) {
				servletContext.addListener(listener.getClass());
			}
		}
	}
	public void addFilter() {
		String POLARIS_REQUEST_FIRST_FILTER = "PolarisRequestFirstFilter";
		String POLARIS_FLOW_CONTROL_FILTER = "PolarisFlowControlFilter";
		
		// filter
		servletContext.addFilter(POLARIS_REQUEST_FIRST_FILTER, new RequestFirstFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		// 流控
		if ("true".equals(ConfClient.get("server.flowcontrol.enabled", "false"))) {
			servletContext.addFilter(POLARIS_FLOW_CONTROL_FILTER, new FlowControlFilter())
			  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		}
		
		List<InnerFilter> listeners = WebConfigInitializer.getFilters();
		if (listeners != null) {
			for (InnerFilter innerFilter : listeners) {
				servletContext.addFilter(innerFilter.getFilterName(), innerFilter.getFilter())
				  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, innerFilter.getUrlPatterns());
			}
		}
		
	}
	public void addServlet(){
	}
}
