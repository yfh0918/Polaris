package com.polaris.extension.db.interceptor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.util.StringUtil;

@Intercepts({
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }) })
public class MybatisInterceptor implements Interceptor {
	private static final Logger logger = LoggerFactory.getLogger(MybatisInterceptor.class);


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = null;
		if (invocation.getArgs().length > 1) {
			parameter = invocation.getArgs()[1];
		}
		String sqlId = mappedStatement.getId();
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		Configuration configuration = mappedStatement.getConfiguration();
		Object returnValue = null;
		long start = System.currentTimeMillis();
		String sql = showSql(configuration, boundSql);//获取SQL文，并且替换特殊字符
		returnValue = invocation.proceed();
		long end = System.currentTimeMillis();
		long time = (end - start);
		logger.info(getSql(sql, sqlId, time));
		return returnValue;
	}

	public static String getSql(String sql, String sqlId, long time) {
		StringBuilder str = new StringBuilder(100);
		str.append(sqlId);
		str.append("  执行SQL:【");
		str.append(sql);
		str.append("】   执行时间");
		str.append(":");
		str.append(time);
		str.append("ms");
		return str.toString();
	}

	private static String getParameterValue(Object obj) {
		String value = null;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			if (obj != null) {
				value = "'" + formatter.format(((Date)obj)) + "'";
			} 
		} else {
			if (obj != null) {
				value = obj.toString();
			} else {
				value = "";
			}

		}
		return value;
	}
	private static Object escapeCode(Object obj) {
		if (obj == null) {
			return obj;
		}
		if (obj instanceof String && obj.toString().contains("$")) {
			obj = CodeFilter.Replace(obj.toString(), "$", "\\$");
		} 
		return obj;
	}

	public static String showSql(Configuration configuration, BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						obj = escapeCode(obj);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						obj = escapeCode(obj);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					} else {
						Object obj = metaObject.getValue(propertyName);
						obj = escapeCode(obj);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					}
				}
			}
		}
		return sql;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties arg0) {
		//nothing
		return;
	}
	
	static class CodeFilter {
		static String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
		static String escapeFromHtml(String s) {  
	        if (StringUtil.isEmpty(s)) {
	        	return s;
	        }
	        s = Replace(s, "&", "&amp;");  
	        s = Replace(s, "<", "&lt;");  
	        s = Replace(s, ">", "&gt;");  
	        s = Replace(s, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");  
	        s = Replace(s, "\r\n", "\n");  
	        s = Replace(s, "\n", "<br>");  
	        s = Replace(s, "  ", "&nbsp;&nbsp;");  
	        s = Replace(s, "'", "&#39;");  
	        s = Replace(s, "\\", "&#92;");
	        for (String key : fbsArr) {  
	            if (s.contains(key)) {  
	                s = s.replace(key, "\\" + key);  
	            }  
	        } 
	        return s.trim();  
	    }
		
		static String escapeFromDB(String s) {  
	        if (StringUtil.isEmpty(s)) {
	        	return s;
	        }
	        s = Replace(s, "&amp;", "&");  
		    s = Replace(s, "&nbsp;", " ");  
		    s = Replace(s, "&#39;", "'");          
		    s = Replace(s, "&#92;", "\\"); 
		    s = Replace(s, "&lt;", "<");
		    s = Replace(s, "&gt;", ">");  
		    s = Replace(s, "<br>", "\n");  
	        return s.trim();  
	    }
		
		static String Replace(String s, String s1, String s2)  
	    {  
	        if(s == null) {  
	            return null;  
	        }  
	        StringBuffer stringbuffer = new StringBuffer();  
	        int i = s.length();  
	        int j = s1.length();  
	        int k;  
	        int l;  
	        for(k = 0; (l = s.indexOf(s1, k)) >= 0; k = l + j) {  
	            stringbuffer.append(s.substring(k, l));  
	            stringbuffer.append(s2);  
	        }  

	        if(k < i)   {  
	            stringbuffer.append(s.substring(k));  
	        }  
	        return stringbuffer.toString();  
	    }  
	} 
}
