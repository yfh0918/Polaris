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
import org.slf4j.helpers.BasicMarker;
import org.slf4j.impl.StaticMarkerBinder;
import org.slf4j.spi.LocationAwareLogger;

import com.polaris.core.Constant;
import com.polaris.core.GlobalContext;
import com.polaris.core.config.provider.ConfSystemHandlerProvider;
import com.polaris.core.util.StringUtil;

public final class ExtendedLogger  implements LocationAwareLogger,Serializable {
	
	Log4jMarkerFactory log4jMarkerFactory = new Log4jMarkerFactory();
	private volatile boolean initialized = false;
	private boolean init() {
		if (initialized) {
			return initialized;
		}
		synchronized(ExtendedLogger.class) {
			//载入日志文件
			try {

				//从系统目录获取logging.config 
				String logFile = ConfSystemHandlerProvider.INSTANCE.getProperties().getProperty(Constant.LOG_CONFIG, Constant.DEFAULT_LOG_FILE);
				
				//设置具体的日志
				if (logFile != null && !logFile.isEmpty()) {
					System.setProperty("log4j.configurationFile", logFile);
					
			        Logger templogger = LogManager.getLogger(this.name);
			        logger = new ExtendedLoggerWrapper((AbstractLogger)templogger,templogger.getName(),templogger.getMessageFactory());

					initialized = true;
					return initialized;
				}
			} catch (Exception e) {
			}
		}
		return false;
		
	}
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ExtendedLoggerWrapper logger;
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
    
	public ExtendedLogger(String rootLoggerName, Object object, ExtendedLoggerContext loggerContext) {
		this.name = rootLoggerName;
        this.parent = (ExtendedLogger)object;
        this.loggerContext = loggerContext;
		init();
	}
	
	public String getName() {
		return "Polaris.log";
	}

	public boolean isTraceEnabled() {
		if (!init()) {
			return false;
		}
		return logger.isTraceEnabled();
	}

	public void trace(String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(msg), (Throwable) null);
	}

	public void trace(String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(format), arg);
	}

	public void trace(String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(format), arg1, arg2);
	}

	public void trace(String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(format), arguments);
	}

	public void trace(String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(msg), t);
	}

	public boolean isTraceEnabled(Marker marker) {
		if (!init()) {
			return false;
		}
		return logger.isTraceEnabled(getMarker(marker));
	}

	public void trace(Marker marker, String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void trace(Marker marker, String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(format), arg);
	}

	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void trace(Marker marker, String format, Object... argArray) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(format), argArray);
	}

	public void trace(Marker marker, String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(msg), t);
	}

	public boolean isDebugEnabled() {
		if (!init()) {
			return false;
		}
		return logger.isDebugEnabled();
	}

	public void debug(String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(msg), (Throwable) null);
	}

	public void debug(String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(format), arg);
	}

	public void debug(String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(format), arg1, arg2);
	}

	public void debug(String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(format), arguments);
	}

	public void debug(String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(msg), t);
	}

	public boolean isDebugEnabled(Marker marker) {
		if (!init()) {
			return false;
		}
		return logger.isDebugEnabled(getMarker(marker));
	}

	public void debug(Marker marker,String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void debug(Marker marker,String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(format), arg);
	}

	public void debug(Marker marker,String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void debug(Marker marker,String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(format), arguments);
	}

	public void debug(Marker marker,String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(msg), t);
	}

	public boolean isInfoEnabled() {
		if (!init()) {
			return false;
		}
		return logger.isInfoEnabled();
	}

	public void info(String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(msg), (Throwable) null);
	}

	public void info(String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(format), arg);
	}

	public void info(String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(format), arg1, arg2);
	}

	public void info(String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(format), arguments);
	}

	public void info(String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(msg), t);
	}

	public boolean isInfoEnabled(Marker marker) {
		if (!init()) {
			return false;
		}
		return logger.isInfoEnabled(getMarker(marker));
	}

	public void info(Marker marker,String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void info(Marker marker,String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(format), arg);
	}

	public void info(Marker marker,String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void info(Marker marker,String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(format), arguments);
	}

	public void info(Marker marker,String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(msg), t);
	}
	public boolean isWarnEnabled() {
		if (!init()) {
			return false;
		}
		return logger.isWarnEnabled();
	}
	
	public void warn(String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(msg), (Throwable) null);
	}

	public void warn(String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(format), arg);
	}

	public void warn(String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(format), arg1, arg2);
	}

	public void warn(String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(format), arguments);
	}

	public void warn(String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(msg), t);
	}

	public boolean isWarnEnabled(Marker marker) {
		if (!init()) {
			return false;
		}
		return logger.isWarnEnabled(getMarker(marker));
	}

	public void warn(Marker marker,String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void warn(Marker marker,String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(format), arg);
	}

	public void warn(Marker marker,String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void warn(Marker marker,String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(format), arguments);
	}

	public void warn(Marker marker,String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(msg), t);
	}

	public boolean isErrorEnabled() {
		if (!init()) {
			return false;
		}
		return logger.isErrorEnabled();
	}

	public void error(String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(msg), (Throwable) null);
	}

	public void error(String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(format), arg);
	}

	public void error(String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(format), arg1, arg2);
	}

	public void error(String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(format), arguments);
	}

	public void error(String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(msg), t);
	}

	public boolean isErrorEnabled(Marker marker) {
		if (!init()) {
			return false;
		}
		return logger.isErrorEnabled(getMarker(marker));
	}

	public void error(Marker marker,String msg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void error(Marker marker,String format, Object arg) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(format), arg);
	}

	public void error(Marker marker,String format, Object arg1, Object arg2) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void error(Marker marker,String format, Object... arguments) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(format), arguments);
	}

	public void error(Marker marker,String msg, Throwable t) {
		if (!init()) {
			return;
		}
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(msg), t);
	}

	public void setLevel(int debug) {
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
    


	//日志输出
	private String getMessage(Object strO) {
		String str = null;
		if (strO != null) {
			str = strO.toString();
		} else {
			str = "";
		}

		//日志本地输出格式设定
		StringBuilder strB = new StringBuilder();
		if (StringUtil.isNotEmpty(GlobalContext.getTraceId())) {
			strB.append(GlobalContext.TRACE_ID);
			strB.append(":");
			strB.append(GlobalContext.getTraceId());
			strB.append(' ');
		}
		if (StringUtil.isNotEmpty(GlobalContext.getParentId())) {
			strB.append(GlobalContext.PARENT_ID);
			strB.append(":");
			strB.append(GlobalContext.getParentId());
			strB.append(' ');
		}
		if (StringUtil.isNotEmpty(GlobalContext.getModuleId())) {
			strB.append(GlobalContext.MODULE_ID);
			strB.append(":");
			strB.append(GlobalContext.getModuleId());
			strB.append(' ');
		}

		strB.append(str);
		return strB.toString();
	}
	
    private org.apache.logging.log4j.Marker getMarker(final Marker marker) {
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

	@Override
	public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t) {
		logger.logIfEnabled(fqcn, getLevel(level), getMarker(marker), getMessage(message), argArray, t);
	}
	
	private Level getLevel(int level) {
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
