package com.polaris.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ExceptionUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);
	public static String toString(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw, true);
    	logger.error(e.getMessage());
    	e.printStackTrace(pw);
    	pw.flush();
    	sw.flush();
    	return sw.toString();
	}
}
