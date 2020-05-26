package com.polaris.core.log;

import org.apache.logging.log4j.Level;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarker;
import org.slf4j.impl.StaticMarkerBinder;

import com.polaris.core.GlobalContext;
import com.polaris.core.util.StringUtil;

public class Log4jHelper {
	private static Log4jMarkerFactory log4jMarkerFactory = new Log4jMarkerFactory();

    public static org.apache.logging.log4j.Marker getMarker(final Marker marker) {
        if (marker == null) {
            return null;
        } else if (marker instanceof Log4jMarker) {
            return ((Log4jMarker) marker).getLog4jMarker();
        } else if (marker instanceof BasicMarker) {
        	return ((Log4jMarker) log4jMarkerFactory.getMarker(marker)).getLog4jMarker();
        } else {
            final Log4jMarkerFactory factory = (Log4jMarkerFactory) StaticMarkerBinder.SINGLETON.getMarkerFactory();
            return ((Log4jMarker) factory.getMarker(marker)).getLog4jMarker();
        }
    }
    
	public static Level getLevel(int level) {
		if (Level.OFF.intLevel() == level) {
			return Level.OFF;
		}
		if (Level.FATAL.intLevel() == level) {
			return Level.FATAL;
		}
		if (Level.ERROR.intLevel() == level) {
			return Level.ERROR;
		}
		if (Level.WARN.intLevel() == level) {
			return Level.WARN;
		}
		if (Level.INFO.intLevel() == level) {
			return Level.INFO;
		}
		if (Level.DEBUG.intLevel() == level) {
			return Level.DEBUG;
		}
		if (Level.TRACE.intLevel() == level) {
			return Level.TRACE;
		}
		if (Level.ALL.intLevel() == level) {
			return Level.ALL;
		}		
        
		return Level.INFO;
	}
	
	public static String getWrappedMessage(String strO, boolean traceEnable) {
		if (!traceEnable) {
			return strO;
		}
		String str = null;
		if (strO != null) {
			str = strO;
		} else {
			str = "";
		}
		//logger
		StringBuilder strB = new StringBuilder();
		String traceId = GlobalContext.getTraceId();
		if (StringUtil.isNotEmpty(traceId)) {
			strB.append(GlobalContext.TRACE_ID);
			strB.append(":");
			strB.append(traceId);
			strB.append(' ');
		}
		String spanId = GlobalContext.getSpanId();
		if (StringUtil.isNotEmpty(spanId)) {
			strB.append(GlobalContext.SPAN_ID);
			strB.append(":");
			strB.append(spanId);
			strB.append(' ');
		}
		String parentId = GlobalContext.getParentId();
		if (StringUtil.isNotEmpty(parentId)) {
			strB.append(GlobalContext.PARENT_ID);
			strB.append(":");
			strB.append(parentId);
			strB.append(' ');
		}
		String moduleId = GlobalContext.getModuleId();
		if (StringUtil.isNotEmpty(moduleId)) {
			strB.append(GlobalContext.MODULE_ID);
			strB.append(":");
			strB.append(moduleId);
			strB.append(' ');
		}
		strB.append(str);
		return strB.toString();
	}
}
