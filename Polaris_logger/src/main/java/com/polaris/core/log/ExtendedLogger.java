package com.polaris.core.log;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import org.slf4j.Marker;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfHandlerSystem;

public final class ExtendedLogger extends ExtendedLoggerCallBack implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AbstractLogger logger = NullLogger.INSTANCE;
	public static final String YEAR_MONTH_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
	
	public static final String LOG_SEPARATOR = "->";// 分割符号
	private static final String FQCN = ExtendedLogger.class.getName();
	
	// The effective levelInt is the assigned levelInt and if null, a levelInt is
    // inherited form a parent.
    transient private int effectiveLevelInt;
    /**
     * The parent of this category. All categories have at least one ancestor
     * which is the root category.
     */
    @SuppressWarnings("unused")
	transient private ExtendedLogger parent;
	final transient ExtendedLoggerContext loggerContext;
    /**
     * The name of this logger
     */
    private String name;
    /**
     * The children of this logger. A logger may have zero or more children.
     */
    transient private List<ExtendedLogger> childrenList;
    
	private boolean traceEnable = false;
	private void initial() {
		if (!(logger instanceof NullLogger)) {
			return;
		}
		synchronized(ExtendedLogger.class) {
			try {
				String logFile = ConfHandlerSystem.getProperties().getProperty(Constant.LOG_CONFIG_KEY, Constant.DEFAULT_LOG_FILE);
				if (logFile != null && !logFile.isEmpty()) {
					System.setProperty(Constant.LOG_CONFIG_FILE_KEY, logFile);
			        Logger templogger = LogManager.getLogger(this.name);
			        logger = new ExtendedLoggerWrapper((AbstractLogger)templogger,templogger.getName(),templogger.getMessageFactory());
			        traceEnable = Boolean.parseBoolean(ConfHandlerSystem.getProperties().getProperty(Constant.LOG_TRACE_ENABEL,"false"));
				}
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	public ExtendedLogger(String rootLoggerName, Object object, ExtendedLoggerContext loggerContext) {
		this.name = rootLoggerName;
        this.parent = (ExtendedLogger)object;
        this.loggerContext = loggerContext;
        initial();
	}
	
	private AbstractLogger getLogger() {
		initial();
		return logger;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean isTraceEnabled() {
		return getLogger().isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.trace(wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.TRACE, null, wrappedMessage, (Throwable) null);
	}

	@Override
	public void trace(String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.trace(wrappedMessage, arg);
		getLogger().logIfEnabled(FQCN, Level.TRACE, null, wrappedMessage, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.trace(wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.TRACE, null, wrappedMessage, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.trace(wrappedMessage, arguments);
		getLogger().logIfEnabled(FQCN, Level.TRACE, null, wrappedMessage, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.trace(wrappedMessage, t);
		getLogger().logIfEnabled(FQCN, Level.TRACE, null, wrappedMessage, t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return getLogger().isTraceEnabled(Log4jHelper.getMarker(marker));
	}

	@Override
	public void trace(Marker marker, String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.trace(marker, wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.TRACE, Log4jHelper.getMarker(marker), wrappedMessage, (Throwable) null);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.trace(marker, wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.TRACE, Log4jHelper.getMarker(marker), wrappedMessage, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.trace(marker, wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.TRACE, Log4jHelper.getMarker(marker), wrappedMessage, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.trace(marker, wrappedMessage,argArray);
		getLogger().logIfEnabled(FQCN, Level.TRACE, Log4jHelper.getMarker(marker), wrappedMessage, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.trace(marker, wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.TRACE, Log4jHelper.getMarker(marker), wrappedMessage, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.debug(wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, null, wrappedMessage, (Throwable) null);
	}

	@Override
	public void debug(String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.debug(wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, null, wrappedMessage, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.debug(wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, null, wrappedMessage, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.debug(wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, null, wrappedMessage, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.debug(wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, null, wrappedMessage, t);
	}

	public boolean isDebugEnabled(Marker marker) {
		return getLogger().isDebugEnabled(Log4jHelper.getMarker(marker));
	}

	@Override
	public void debug(Marker marker,String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.debug(marker,wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, Log4jHelper.getMarker(marker), wrappedMessage, (Throwable) null);
	}

	@Override
	public void debug(Marker marker,String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.debug(marker,wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, Log4jHelper.getMarker(marker), wrappedMessage, arg);
	}

	@Override
	public void debug(Marker marker,String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.debug(marker,wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, Log4jHelper.getMarker(marker), wrappedMessage, arg1, arg2);
	}

	public void debug(Marker marker,String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.debug(marker,wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, Log4jHelper.getMarker(marker), wrappedMessage, arguments);
	}

	@Override
	public void debug(Marker marker,String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.debug(marker,wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.DEBUG, Log4jHelper.getMarker(marker), wrappedMessage, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return getLogger().isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.info(wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.INFO, null, wrappedMessage, (Throwable) null);
	}

	@Override
	public void info(String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.info(wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.INFO, null, wrappedMessage, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.info(wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.INFO, null, wrappedMessage, arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.info(wrappedMessage, arguments);
		getLogger().logIfEnabled(FQCN, Level.INFO, null, wrappedMessage, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.info(wrappedMessage, t);
		getLogger().logIfEnabled(FQCN, Level.INFO, null, wrappedMessage, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return getLogger().isInfoEnabled(Log4jHelper.getMarker(marker));
	}

	@Override
	public void info(Marker marker,String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.info(marker,wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.INFO, Log4jHelper.getMarker(marker), wrappedMessage, (Throwable) null);
	}

	@Override
	public void info(Marker marker,String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.info(marker,wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.INFO, Log4jHelper.getMarker(marker), wrappedMessage, arg);
	}

	@Override
	public void info(Marker marker,String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.info(marker,wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.INFO, Log4jHelper.getMarker(marker), wrappedMessage, arg1, arg2);
	}

	@Override
	public void info(Marker marker,String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.info(marker,wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.INFO, Log4jHelper.getMarker(marker), wrappedMessage, arguments);
	}

	@Override
	public void info(Marker marker,String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.info(marker,wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.INFO, Log4jHelper.getMarker(marker), wrappedMessage, t);
	}
	public boolean isWarnEnabled() {
		return getLogger().isWarnEnabled();
	}
	
	@Override
	public void warn(String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.warn(wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.WARN, null, wrappedMessage, (Throwable) null);
	}

	@Override
	public void warn(String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.warn(wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.WARN, null, wrappedMessage, arg);
	}

	public void warn(String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.warn(wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.WARN, null, wrappedMessage, arg1, arg2);
	}

	@Override
	public void warn(String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.warn(wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.WARN, null, wrappedMessage, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.warn(wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.WARN, null, wrappedMessage, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return getLogger().isWarnEnabled(Log4jHelper.getMarker(marker));
	}

	@Override
	public void warn(Marker marker,String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.warn(marker,wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.WARN, Log4jHelper.getMarker(marker), wrappedMessage, (Throwable) null);
	}

	@Override
	public void warn(Marker marker,String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.warn(marker,wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.WARN, Log4jHelper.getMarker(marker), wrappedMessage, arg);
	}

	@Override
	public void warn(Marker marker,String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.warn(marker,wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.WARN, Log4jHelper.getMarker(marker), wrappedMessage, arg1, arg2);
	}

	@Override
	public void warn(Marker marker,String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.warn(marker,wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.WARN, Log4jHelper.getMarker(marker), wrappedMessage, arguments);
	}

	@Override
	public void warn(Marker marker,String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.warn(marker,wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.WARN, Log4jHelper.getMarker(marker), wrappedMessage, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return getLogger().isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.error(wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.ERROR, null, wrappedMessage, (Throwable) null);
	}

	@Override
	public void error(String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.error(wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.ERROR, null, wrappedMessage, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.error(wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.ERROR, null, wrappedMessage, arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.error(wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.ERROR, null, wrappedMessage, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.error(wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.ERROR, null, wrappedMessage, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return getLogger().isErrorEnabled(Log4jHelper.getMarker(marker));
	}

	@Override
	public void error(Marker marker,String msg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.error(marker,wrappedMessage);
		getLogger().logIfEnabled(FQCN, Level.ERROR, Log4jHelper.getMarker(marker), wrappedMessage, (Throwable) null);
	}

	@Override
	public void error(Marker marker,String format, Object arg) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.error(marker,wrappedMessage,arg);
		getLogger().logIfEnabled(FQCN, Level.ERROR, Log4jHelper.getMarker(marker), wrappedMessage, arg);
	}

	@Override
	public void error(Marker marker,String format, Object arg1, Object arg2) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.error(marker,wrappedMessage,arg1,arg2);
		getLogger().logIfEnabled(FQCN, Level.ERROR, Log4jHelper.getMarker(marker), wrappedMessage, arg1, arg2);
	}

	@Override
	public void error(Marker marker,String format, Object... arguments) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(format,traceEnable);
		super.error(marker,wrappedMessage,arguments);
		getLogger().logIfEnabled(FQCN, Level.ERROR, Log4jHelper.getMarker(marker), wrappedMessage, arguments);
	}

	@Override
	public void error(Marker marker,String msg, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(msg,traceEnable);
		super.error(marker,wrappedMessage,t);
		getLogger().logIfEnabled(FQCN, Level.ERROR, Log4jHelper.getMarker(marker), wrappedMessage, t);
	}

	@Override
	public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t) {
		String wrappedMessage = Log4jHelper.getWrappedMessage(message,traceEnable);
		super.log(marker,fqcn,level,wrappedMessage,argArray,t);
		getLogger().logIfEnabled(fqcn, Log4jHelper.getLevel(level), Log4jHelper.getMarker(marker), wrappedMessage, argArray, t);
	}

    ExtendedLogger getChildByName(final String childName) {
        if (childrenList == null) {
            return null;
        } else {
            int len = this.childrenList.size();
            for (int i = 0; i < len; i++) {
                final ExtendedLogger childLogger_i = (ExtendedLogger) childrenList.get(i);
                final String childName_i = childLogger_i.getName();

                if (childName.equals(childName_i)) {
                    return childLogger_i;
                }
            }
            // no child found
            return null;
        }
    }

    /**
     * The default size of child list arrays. The JDK 1.5 default is 10. We use a
     * smaller value to save a little space.
     */

    ExtendedLogger createChildByName(final String childName) {
        int i_index = LoggerNameUtil.getSeparatorIndexOf(childName, this.name.length() + 1);
        if (i_index != -1) {
            throw new IllegalArgumentException("For logger [" + this.name + "] child name [" + childName
                            + " passed as parameter, may not include '.' after index" + (this.name.length() + 1));
        }

        if (childrenList == null) {
            childrenList = new CopyOnWriteArrayList<ExtendedLogger>();
        }
        ExtendedLogger childLogger;
        childLogger = new ExtendedLogger(childName, this, this.loggerContext);
        childrenList.add(childLogger);
        childLogger.effectiveLevelInt = this.effectiveLevelInt;
        return childLogger;
    }
}
