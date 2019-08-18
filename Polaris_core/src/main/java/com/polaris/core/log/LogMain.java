package com.polaris.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger xLogger = LoggerFactory.getLogger(LogMain.class);
		xLogger.error("hello");
		xLogger.debug("hello");
		xLogger.info("hello");
		xLogger.warn("hello");
		xLogger.trace("hello");
	}

}
