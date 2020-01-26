package com.polaris.container.servlet.initializer;

import javax.servlet.ServletContext;

public interface ExtensionInitializer {
	public void onStartup(ServletContext servletContext);
}
