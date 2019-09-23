package com.polaris.springmvc;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.http.initializer.AbsHttpInitializer;

public class Initializer extends  AbsHttpInitializer { 
	final static Logger logger = LoggerFactory.getLogger(Initializer.class);
	WebAppInitializer initializer = null;

	@Override
	public void loadContext() {
		initializer = new WebAppInitializer(); 
		try {
			initializer.onStartup(this.servletContext);
		} catch (ServletException e) {
			logger.error(e.getMessage());
		}
	} 

	@Override
	public void addInitParameter() {
		super.addInitParameter();
	}

	@Override
	public void addListener() {
		super.addListener();
	}

	@Override
	public void addFilter() {
		super.addFilter();
	} 
	

}
