package com.polaris.http.initializer;

import javax.servlet.ServletContext;

public interface HttpInitializer {
	public void onStartup(ServletContext servletContext);
}
