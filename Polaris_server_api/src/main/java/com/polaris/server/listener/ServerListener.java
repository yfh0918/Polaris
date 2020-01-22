package com.polaris.server.listener;

import javax.servlet.ServletContext;

public interface ServerListener {

	default void starting(ServletContext servletContext) {
		return;
	}
	default void started(ServletContext servletContext){
		return;
	}
	default void failure(ServletContext servletContext){
		return;
	}
	default void stopping(ServletContext servletContext) {
		return;
	}
	default void stopped(ServletContext servletContext) {
		return;
	}
	
}
