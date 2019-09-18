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
import org.slf4j.impl.StaticMarkerBinder;

import com.polaris.core.Constant;
import com.polaris.core.GlobalContext;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;

public final class ExtendedLogger  implements org.slf4j.Logger,Serializable {
	
	static {
		//载入日志文件
		try {
			String logFile = PropertyUtils.readData(ConfClient.getConfigFileName(Constant.DEFAULT_CONFIG_NAME), Constant.LOG_CONFIG);
			System.setProperty("log4j.configurationFile", logFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ExtendedLoggerWrapper logger;
	public static final String YEAR_MONTH_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
	
	public static final String TRACE_ID = "traceId";// 调用链唯一的ID
	public static final String PARENT_ID = "parentId";// 调用关系ID
	public static final String MODULE_ID = "moduleId";// 本模块ID
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
		// TODO Auto-generated constructor stub
		this.name = rootLoggerName;
        this.parent = (ExtendedLogger)object;
        this.loggerContext = loggerContext;
        
        Logger templogger = LogManager.getLogger(rootLoggerName);
        logger = new ExtendedLoggerWrapper((AbstractLogger)templogger,templogger.getName(),templogger.getMessageFactory());
	}

	public String getTraceId() {
		return GlobalContext.getContext(TRACE_ID);
	}

	public void setTraceId(String traceId) {
		GlobalContext.setContext(TRACE_ID, traceId);
	}
	
	public String getParentId() {
		return GlobalContext.getContext(PARENT_ID);
	}

	public void setParentId(String parentId) {
		GlobalContext.setContext(PARENT_ID, parentId);
	}

	public static String getModuleId() {
		return ConfClient.getAppName();
	}
	
	public String getName() {
		return "Polaris.log";
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void trace(String msg) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(msg), (Throwable) null);
	}

	public void trace(String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(format), arg);
	}

	public void trace(String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(format), arg1, arg2);
	}

	public void trace(String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(format), arguments);
	}

	public void trace(String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, getMessage(msg), t);
	}

	public boolean isTraceEnabled(Marker marker) {
		return logger.isTraceEnabled(getMarker(marker));
	}

	public void trace(Marker marker, String msg) {
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void trace(Marker marker, String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(format), arg);
	}

	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void trace(Marker marker, String format, Object... argArray) {
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(format), argArray);
	}

	public void trace(Marker marker, String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.TRACE, getMarker(marker), getMessage(msg), t);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void debug(String msg) {
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(msg), (Throwable) null);
	}

	public void debug(String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(format), arg);
	}

	public void debug(String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(format), arg1, arg2);
	}

	public void debug(String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(format), arguments);
	}

	public void debug(String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.DEBUG, null, getMessage(msg), t);
	}

	public boolean isDebugEnabled(Marker marker) {
		return logger.isDebugEnabled(getMarker(marker));
	}

	public void debug(Marker marker,String msg) {
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void debug(Marker marker,String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(format), arg);
	}

	public void debug(Marker marker,String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void debug(Marker marker,String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(format), arguments);
	}

	public void debug(Marker marker,String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(marker), getMessage(msg), t);
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public void info(String msg) {
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(msg), (Throwable) null);
	}

	public void info(String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(format), arg);
	}

	public void info(String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(format), arg1, arg2);
	}

	public void info(String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(format), arguments);
	}

	public void info(String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.INFO, null, getMessage(msg), t);
	}

	public boolean isInfoEnabled(Marker marker) {
		return logger.isInfoEnabled(getMarker(marker));
	}

	public void info(Marker marker,String msg) {
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void info(Marker marker,String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(format), arg);
	}

	public void info(Marker marker,String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void info(Marker marker,String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(format), arguments);
	}

	public void info(Marker marker,String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.INFO, getMarker(marker), getMessage(msg), t);
	}
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}
	
	public void warn(String msg) {
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(msg), (Throwable) null);
	}

	public void warn(String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(format), arg);
	}

	public void warn(String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(format), arg1, arg2);
	}

	public void warn(String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(format), arguments);
	}

	public void warn(String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.WARN, null, getMessage(msg), t);
	}

	public boolean isWarnEnabled(Marker marker) {
		return logger.isWarnEnabled(getMarker(marker));
	}

	public void warn(Marker marker,String msg) {
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void warn(Marker marker,String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(format), arg);
	}

	public void warn(Marker marker,String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void warn(Marker marker,String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(format), arguments);
	}

	public void warn(Marker marker,String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.WARN, getMarker(marker), getMessage(msg), t);
	}

	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	public void error(String msg) {
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(msg), (Throwable) null);
	}

	public void error(String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(format), arg);
	}

	public void error(String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(format), arg1, arg2);
	}

	public void error(String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(format), arguments);
	}

	public void error(String msg, Throwable t) {
		logger.logIfEnabled(FQCN, Level.ERROR, null, getMessage(msg), t);
	}

	public boolean isErrorEnabled(Marker marker) {
		return logger.isErrorEnabled(getMarker(marker));
	}

	public void error(Marker marker,String msg) {
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(msg), (Throwable) null);
	}

	public void error(Marker marker,String format, Object arg) {
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(format), arg);
	}

	public void error(Marker marker,String format, Object arg1, Object arg2) {
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(format), arg1, arg2);
	}

	public void error(Marker marker,String format, Object... arguments) {
		logger.logIfEnabled(FQCN, Level.ERROR, getMarker(marker), getMessage(format), arguments);
	}

	public void error(Marker marker,String msg, Throwable t) {
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
		if (StringUtil.isNotEmpty(getTraceId())) {
			strB.append(TRACE_ID);
			strB.append(":");
			strB.append(getTraceId());
			strB.append(' ');
		}
		if (StringUtil.isNotEmpty(getParentId())) {
			strB.append(PARENT_ID);
			strB.append(":");
			strB.append(getParentId());
			strB.append(' ');
		}
		if (StringUtil.isNotEmpty(getModuleId())) {
			strB.append(MODULE_ID);
			strB.append(":");
			strB.append(getModuleId());
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
        } else {
            final Log4jMarkerFactory factory = (Log4jMarkerFactory) StaticMarkerBinder.SINGLETON.getMarkerFactory();
            return ((Log4jMarker) factory.getMarker(marker)).getLog4jMarker();
        }
    }
}
