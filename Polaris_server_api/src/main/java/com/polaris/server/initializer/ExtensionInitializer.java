package com.polaris.server.initializer;

import javax.servlet.ServletContext;

public interface ExtensionInitializer {
	public void onStartup(ServletContext servletContext);
}
