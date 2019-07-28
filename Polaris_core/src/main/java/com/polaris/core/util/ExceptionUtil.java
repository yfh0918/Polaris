package com.polaris.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;



public class ExceptionUtil {
	private static final LogUtil logger = LogUtil.getInstance(ExceptionUtil.class);
	public static String toString(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw, true);
    	logger.error(e);
    	e.printStackTrace(pw);
    	pw.flush();
    	sw.flush();
    	return sw.toString();
	}
}
