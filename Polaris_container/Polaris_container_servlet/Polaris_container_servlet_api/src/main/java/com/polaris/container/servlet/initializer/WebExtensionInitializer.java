package com.polaris.container.servlet.initializer;

import javax.servlet.ServletContext;

public interface WebExtensionInitializer {
	public void onStartup(ServletContext servletContext);
}
