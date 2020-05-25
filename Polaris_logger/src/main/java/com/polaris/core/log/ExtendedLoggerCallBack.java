package com.polaris.core.log;

import java.util.ServiceLoader;

import org.apache.logging.log4j.Level;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

public class ExtendedLoggerCallBack implements LocationAwareLogger {
	private static final ServiceLoader<Log4jCallBack> callBacks = ServiceLoader.load(Log4jCallBack.class);

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public void trace(String msg) {
		callBack(Level.TRACE,msg);
	}

	@Override
	public void trace(String format, Object arg) {
		callBack(Level.TRACE,format,arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		callBack(Level.TRACE,format,arg1,arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		callBack(Level.TRACE,format,arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		callBack(Level.TRACE,msg,t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return false;
	}

	@Override
	public void trace(Marker marker, String msg) {
		callBack(Level.TRACE,msg);
		
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		callBack(Level.TRACE,format,arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		callBack(Level.TRACE,format,arg1,arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		callBack(Level.TRACE,format,argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		callBack(Level.TRACE,msg,t);
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void debug(String msg) {
		callBack(Level.DEBUG,msg);
	}

	@Override
	public void debug(String format, Object arg) {
		callBack(Level.DEBUG,format,arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		callBack(Level.DEBUG,format,arg1,arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		callBack(Level.DEBUG,format,arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		callBack(Level.DEBUG,msg,t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return false;
	}

	@Override
	public void debug(Marker marker, String msg) {
		callBack(Level.DEBUG,msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		callBack(Level.DEBUG,format,arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		callBack(Level.DEBUG,format,arg1,arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		callBack(Level.DEBUG,format,arguments);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		callBack(Level.DEBUG,msg,t);
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public void info(String msg) {
		callBack(Level.INFO,msg);
	}

	@Override
	public void info(String format, Object arg) {
		callBack(Level.INFO,format,arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		callBack(Level.INFO,format,arg1,arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		callBack(Level.INFO,format,arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		callBack(Level.INFO,msg,t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return false;
	}

	@Override
	public void info(Marker marker, String msg) {
		callBack(Level.INFO,msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		callBack(Level.INFO,format,arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		callBack(Level.INFO,format,arg1,arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		callBack(Level.INFO,format,arguments);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		callBack(Level.INFO,msg,t);
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(String msg) {
		callBack(Level.WARN,msg);
	}

	@Override
	public void warn(String format, Object arg) {
		callBack(Level.WARN,format,arg);
	}

	@Override
	public void warn(String format, Object... arguments) {
		callBack(Level.WARN,format,arguments);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		callBack(Level.WARN,format,arg1,arg2);
	}

	@Override
	public void warn(String msg, Throwable t) {
		callBack(Level.WARN,msg,t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return false;
	}

	@Override
	public void warn(Marker marker, String msg) {
		callBack(Level.WARN,msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		callBack(Level.WARN,format,arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		callBack(Level.WARN,format,arg1,arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		callBack(Level.WARN,format,arguments);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		callBack(Level.WARN,msg,t);
	}

	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	@Override
	public void error(String msg) {
		callBack(Level.ERROR,msg);
	}

	@Override
	public void error(String format, Object arg) {
		callBack(Level.ERROR,format,arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		callBack(Level.ERROR,format,arg1,arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		callBack(Level.ERROR,format,arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		callBack(Level.ERROR,msg,t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return false;
	}

	@Override
	public void error(Marker marker, String msg) {
		callBack(Level.ERROR,msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		callBack(Level.ERROR,format,arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		callBack(Level.ERROR,format,arg1,arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		callBack(Level.ERROR,format,arguments);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		callBack(Level.ERROR,msg,t);
	}

	@Override
	public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t) {
		if (t == null) {
			callBack(getLevel(level),message,argArray);
		} else {
			callBack(getLevel(level),message,t);
		}
	}
	
	private void callBack(Level level, String message, Object... args) {
		for (Log4jCallBack callBackObj : callBacks) {
			callBackObj.call(level, message, args);
        }
	}
	private void callBack(Level level, String message, Throwable t) {
		for (Log4jCallBack callBackObj : callBacks) {
			callBackObj.call(level, message,t);
        }
	}

	protected Level getLevel(int level) {
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
}
