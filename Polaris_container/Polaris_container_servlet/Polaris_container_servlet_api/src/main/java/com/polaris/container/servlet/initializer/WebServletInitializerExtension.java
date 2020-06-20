package com.polaris.container.servlet.initializer;

import javax.servlet.ServletContext;

public interface WebServletInitializerExtension {
	public void onStartup(ServletContext servletContext);
}
