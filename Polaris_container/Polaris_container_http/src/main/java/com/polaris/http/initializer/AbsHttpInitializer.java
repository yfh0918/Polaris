package com.polaris.http.initializer;

import javax.servlet.ServletContext;

public abstract class AbsHttpInitializer implements HttpInitializer {
	protected ServletContext servletContext = null;
	public void onStartup(ServletContext servletContext) {
		this.servletContext = servletContext;
		addInitParameter();
		addListener();
		addFilter();
		addServlet();
	}
	public abstract void addInitParameter();
	public abstract void addListener();
	public abstract void addFilter();
	public abstract void addServlet();
}
