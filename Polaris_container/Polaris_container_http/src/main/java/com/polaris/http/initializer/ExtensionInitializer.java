package com.polaris.http.initializer;

import javax.servlet.ServletContext;

public interface ExtensionInitializer {
	public void onStartup(ServletContext servletContext);
}
