package com.polaris.comm.util;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import org.slf4j.helpers.MessageFormatter;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;

public class LogUtil extends ExtendedLoggerWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3449587413645986565L;

	private final ExtendedLoggerWrapper  logger; 
	
	public static final String YEAR_MONTH_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
	
	public static final String TRACE_ID = "traceId";// 调用链唯一的ID
	public static final String PARENT_ID = "parentId";// 调用关系ID
	public static final String MODULE_ID = "moduleId";// 本模块ID
	public static final String LOG_SEPARATOR = "->";// 分割符号
	private static final String FQCN = LogUtil.class.getName();
	private boolean isCollect = true;//

	private LogUtil(final Logger logger) {
		super((AbstractLogger) logger, logger.getName(), logger.getMessageFactory());
		this.logger = this;
	}
	
	private LogUtil(final Logger logger, boolean isCollect) {
		this(logger);
		this.isCollect = isCollect;
	}


	public static LogUtil getInstance(Class<?> cls) {
		final Logger wrapped = LogManager.getLogger(cls);
		return new LogUtil(wrapped);
	}
	public static LogUtil getInstance(Class<?> cls, boolean isCollect) {
		final Logger wrapped = LogManager.getLogger(cls);
		return new LogUtil(wrapped, isCollect);
	}

	public static String getTraceId() {
		return Constant.getContext(TRACE_ID);
	}

	public static void setTraceId(String traceId) {
		Constant.setContext(TRACE_ID, traceId);
	}
	
	public static String getParentId() {
		return Constant.getContext(PARENT_ID);
	}

	public static void setParentId(String parentId) {
	        Constant.setContext(PARENT_ID, parentId);
	}

	public static String getModuleId() {
		return ConfClient.getAppName();
	}

	public void debug(String message) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(Level.DEBUG, message);  
        }  
    }  
  
    public void debug(String message, Throwable t) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(Level.DEBUG, message, t);  
        }  
    }  
  
    public void debug(Throwable t) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(Level.DEBUG, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    }
    
    public void debug(String pattern, Object... arguments) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(Level.DEBUG, format(pattern, arguments));  
        }  
    }  
    public void debug(String pattern, Throwable t, Object... arguments) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(Level.DEBUG, format(pattern, arguments), t);  
        }  
    }  
    
	public void info(String message) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(Level.INFO, message);  
        }  
    }  
  
    public void info(Throwable t) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(Level.INFO, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    }
    
    public void info(String message, Throwable t) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(Level.INFO, message, t);  
        }  
    }  
  
    public void info(String pattern, Object... arguments) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(Level.INFO, format(pattern, arguments));  
        }  
    }  
    public void info(String pattern, Throwable t, Object... arguments) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(Level.INFO, format(pattern, arguments), t);  
        }  
    }


	public void trace(String message) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(Level.TRACE, message);  
        }  
    }  
  
    public void trace(Throwable t) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(Level.TRACE, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    } 
    
    public void trace(String message, Throwable t) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(Level.TRACE, message, t);  
        }  
    }  
  
    public void trace(String pattern, Object... arguments) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(Level.TRACE, format(pattern, arguments));  
        }  
    }  
    public void trace(String pattern, Throwable t, Object... arguments) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(Level.TRACE, format(pattern, arguments), t);  
        }  
    }
    
	public void warn(String message) {  
		if (logger.isEnabled(Level.WARN)) {  
            forcedLog(Level.WARN, message);  
        }  
    }  

    public void warn(Throwable t) {  
    	if (logger.isEnabled(Level.WARN)) {  
            forcedLog(Level.WARN, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    } 
    
    public void warn(String message, Throwable t) {  
    	if (logger.isEnabled(Level.WARN)) {  
            forcedLog(Level.WARN, message, t);  
        }  
    }  
  
    public void warn(String pattern, Object... arguments) {  
    	
        if (logger.isEnabled(Level.WARN)) {  
            forcedLog(Level.WARN, format(pattern, arguments));  
        }  
    }  
    public void warn(String pattern, Throwable t, Object... arguments) {  
    	if (logger.isEnabled(Level.WARN)) {  
            forcedLog(Level.WARN, format(pattern, arguments), t);  
        }  
    }


	public void error(String message) {  
		if (logger.isEnabled(Level.ERROR)) {  
            forcedLog(Level.ERROR, message);  
        }  
    }  

    public void error(Throwable t) {  
    	if (logger.isEnabled(Level.ERROR)) {  
            forcedLog(Level.ERROR, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    } 
    
    public void error(String message, Throwable t) {  
    	if (logger.isEnabled(Level.ERROR)) {  
            forcedLog(Level.ERROR, message, t);  
        }  
    }  
  
    public void error(String pattern, Object... arguments) {  
        if (logger.isEnabled(Level.ERROR)) {  
            forcedLog(Level.ERROR, format(pattern, arguments));  
        }  
    }  
    public void error(String pattern, Throwable t, Object... arguments) {  
    	if (logger.isEnabled(Level.ERROR)) {  
            forcedLog(Level.ERROR, format(pattern, arguments), t);  
        }  
    }
	
	private void forcedLog(Level level, Object message) { 
		forcedLog(level, message, null);
    }  
  
    private void forcedLog(Level level, Object message, Throwable t) { 
    	logger.logIfEnabled(FQCN, level, null, getMessage(message.toString(), level), t);
    }  
  
    private String format(String pattern, Object... arguments) {
        return MessageFormatter.arrayFormat(pattern, arguments).getMessage();  
    }  
  

	//日志埋点和收集（需要自定义类型）
	@SuppressWarnings("unchecked")
	public void collect(String str, Level level) {
		if (StringUtil.isNotEmpty(getTraceId())) {
			Object proxy = SpringUtil.getBean("logAdapter");
			if (proxy == null) {
				return;
			}
			LinkedBlockingQueue<Map<String, Object>> queue = null;
			try {
				Method getBlockingQueue = proxy.getClass().getMethod("getBlockingQueue");
				if(getBlockingQueue == null) {
					return;
				}
				queue = (LinkedBlockingQueue<Map<String, Object>>) getBlockingQueue.invoke(proxy);
			} catch (Exception e) {
				return;
			}

			//获取日志等级
			if (queue != null) {
				
				//默认info级别以上输出日志
				if (level.intLevel() >= getLevel(ConfClient.get("log.collect.level", "info"))) {
					
					// 构造logDto
					SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_DAY_TIME);
					
					Map<String ,Object> logMap = new HashMap<>();
					// uuid
					logMap.put("uuid", UuidUtil.generateUuid());
					// 操作时间
					logMap.put("createDate", sdf.format(new Date()));
					logMap.put("content", str);
					logMap.put("logType", level.toString());
					logMap.put("trace_id", getTraceId());
					logMap.put("module_id", getModuleId());
					logMap.put("parent_id", getParentId());
					logMap.put("remoteAddr", NetUtils.LOCALHOST);
					queue.offer(logMap);
				}
			}
		}
	}

	//日志输出
	private Object getMessage(String str, Level level) {

		// 保存log
		if (isCollect) {
			collect(str, level);
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

	//转化日志等级
	private int getLevel(String levelStr) {
		if (StringUtil.isEmpty(levelStr)) {
			return Level.INFO.intLevel();
		}
		if (levelStr.toUpperCase().equals(Level.TRACE.toString())) {
			return Level.INFO.intLevel();
		}
		if (levelStr.toUpperCase().equals(Level.ERROR.toString())) {
			return Level.ERROR.intLevel();
		}
		if (levelStr.toUpperCase().equals(Level.WARN.toString())) {
			return Level.WARN.intLevel();
		}
		if (levelStr.toUpperCase().equals(Level.INFO.toString())) {
			return Level.INFO.intLevel();
		}
		if (levelStr.toUpperCase().equals(Level.DEBUG.toString())) {
			return Level.DEBUG.intLevel();
		}
		return Level.INFO.intLevel();
	}

}
