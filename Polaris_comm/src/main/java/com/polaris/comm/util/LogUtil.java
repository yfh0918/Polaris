package com.polaris.comm.util;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.helpers.MessageFormatter;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;

public class LogUtil {

	private final org.apache.log4j.Logger logger; 
	
	public static final String YEAR_MONTH_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
	
	public static final String TRACE_ID = "traceId";// 调用链唯一的ID
	public static final String PARENT_ID = "parentId";// 调用关系ID
	public static final String MODULE_ID = "moduleId";// 本模块ID
	public static final String LOG_SEPARATOR = "->";// 分割符号

	private boolean isCollect = true;//

	private LogUtil(Class<?> clazz, boolean isCollect) {
		this.isCollect = isCollect;
		logger =  org.apache.log4j.Logger.getLogger(clazz);
	}

	private LogUtil() {
		logger = org.apache.log4j.Logger.getRootLogger(); 
	}

	public static LogUtil getInstance(Class<?> cls) {
		return new LogUtil(cls, true);
	}
	public static LogUtil getInstance(Class<?> cls, boolean isCollect) {
		return new LogUtil(cls, isCollect);
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
            forcedLog(logger, Level.DEBUG, message);  
        }  
    }  
  
    public void debug(String message, Throwable t) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(logger, Level.DEBUG, message, t);  
        }  
    }  
  
    public void debug(Throwable t) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(logger, Level.DEBUG, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    }
    
    public void debug(String pattern, Object... arguments) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(logger, Level.DEBUG, format(pattern, arguments));  
        }  
    }  
    public void debug(String pattern, Throwable t, Object... arguments) {  
        if (logger.isDebugEnabled()) {  
            forcedLog(logger, Level.DEBUG, format(pattern, arguments), t);  
        }  
    }  
    
	public void info(String message) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(logger, Level.INFO, message);  
        }  
    }  
  
    public void info(Throwable t) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(logger, Level.INFO, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    }
    
    public void info(String message, Throwable t) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(logger, Level.INFO, message, t);  
        }  
    }  
  
    public void info(String pattern, Object... arguments) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(logger, Level.INFO, format(pattern, arguments));  
        }  
    }  
    public void info(String pattern, Throwable t, Object... arguments) {  
        if (logger.isInfoEnabled()) {  
            forcedLog(logger, Level.INFO, format(pattern, arguments), t);  
        }  
    }


	public void trace(String message) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(logger, Level.TRACE, message);  
        }  
    }  
  
    public void trace(Throwable t) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(logger, Level.TRACE, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    } 
    
    public void trace(String message, Throwable t) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(logger, Level.TRACE, message, t);  
        }  
    }  
  
    public void trace(String pattern, Object... arguments) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(logger, Level.TRACE, format(pattern, arguments));  
        }  
    }  
    public void trace(String pattern, Throwable t, Object... arguments) {  
        if (logger.isTraceEnabled()) {  
            forcedLog(logger, Level.TRACE, format(pattern, arguments), t);  
        }  
    }
    
	public void warn(String message) {  
		if (logger.isEnabledFor(Level.WARN)) {  
            forcedLog(logger, Level.WARN, message);  
        }  
    }  

    public void warn(Throwable t) {  
    	if (logger.isEnabledFor(Level.WARN)) {  
            forcedLog(logger, Level.WARN, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    } 
    
    public void warn(String message, Throwable t) {  
    	if (logger.isEnabledFor(Level.WARN)) {  
            forcedLog(logger, Level.WARN, message, t);  
        }  
    }  
  
    public void warn(String pattern, Object... arguments) {  
        if (logger.isEnabledFor(Level.WARN)) {  
            forcedLog(logger, Level.WARN, format(pattern, arguments));  
        }  
    }  
    public void warn(String pattern, Throwable t, Object... arguments) {  
    	if (logger.isEnabledFor(Level.WARN)) {  
            forcedLog(logger, Level.WARN, format(pattern, arguments), t);  
        }  
    }


	public void error(String message) {  
		if (logger.isEnabledFor(Level.ERROR)) {  
            forcedLog(logger, Level.ERROR, message);  
        }  
    }  

    public void error(Throwable t) {  
    	if (logger.isEnabledFor(Level.ERROR)) {  
            forcedLog(logger, Level.ERROR, t.getMessage() == null ? t.toString() : t.getMessage(), t);  
        }  
    } 
    
    public void error(String message, Throwable t) {  
    	if (logger.isEnabledFor(Level.ERROR)) {  
            forcedLog(logger, Level.ERROR, message, t);  
        }  
    }  
  
    public void error(String pattern, Object... arguments) {  
        if (logger.isEnabledFor(Level.ERROR)) {  
            forcedLog(logger, Level.ERROR, format(pattern, arguments));  
        }  
    }  
    public void error(String pattern, Throwable t, Object... arguments) {  
    	if (logger.isEnabledFor(Level.ERROR)) {  
            forcedLog(logger, Level.ERROR, format(pattern, arguments), t);  
        }  
    }
	
	private void forcedLog(org.apache.log4j.Logger logger, Level level, Object message) { 
		forcedLog(logger, level, message, null);
    }  
  
    private void forcedLog(org.apache.log4j.Logger logger, Level level, Object message, Throwable t) {  
        logger.callAppenders(new LoggingEvent(FQCN, logger, level, getMessage(message.toString(), level), t));  
    }  
  
    private String format(String pattern, Object... arguments) {
        return MessageFormatter.arrayFormat(pattern, arguments).getMessage();  
    }  
  
    private static final String FQCN;  
  
    static {  
        FQCN = LogUtil.class.getName();  
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
				if (level.toInt() >= getLevel(ConfClient.get("log.collect.level", "info"))) {
					
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
			return org.apache.log4j.Level.INFO_INT;
		}
		if (levelStr.toUpperCase().equals(org.apache.log4j.Level.TRACE.toString())) {
			return org.apache.log4j.Level.TRACE_INT;
		}
		if (levelStr.toUpperCase().equals(org.apache.log4j.Level.ERROR.toString())) {
			return org.apache.log4j.Level.ERROR_INT;
		}
		if (levelStr.toUpperCase().equals(org.apache.log4j.Level.WARN.toString())) {
			return org.apache.log4j.Level.WARN_INT;
		}
		if (levelStr.toUpperCase().equals(org.apache.log4j.Level.INFO.toString())) {
			return org.apache.log4j.Level.INFO_INT;
		}
		if (levelStr.toUpperCase().equals(org.apache.log4j.Level.DEBUG.toString())) {
			return org.apache.log4j.Level.DEBUG_INT;
		}
		return org.apache.log4j.Level.TRACE_INT;
	}

}
