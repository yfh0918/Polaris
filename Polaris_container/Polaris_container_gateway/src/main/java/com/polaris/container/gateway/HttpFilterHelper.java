package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterEntity;

public class HttpFilterHelper {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterHelper.class);
	public static HttpFilterHelper INSTANCE = new HttpFilterHelper();
	private HttpFilterHelper() {};
	
	//外部调用-替换现有的filter
	public void replaceFilter(HttpFilterEntity httpFilterEntity, HttpFilter filter) {
		removeFilter(httpFilterEntity);
		httpFilterEntity.setFilter(filter);
		addFilter(httpFilterEntity);
	}
	//外部调用-新增filter
	public void addFilter(HttpFilterEntity httpFilterEntity) {
		logger.info("add filter:{}",httpFilterEntity.getFilter().getClass().getSimpleName());
		httpFilterEntity.getFilter().start();
	}
	
	//外部调用-删除filter
	public void removeFilter(HttpFilterEntity httpFilterEntity) {
		logger.info("remove filter:{}",httpFilterEntity.getFilter().getClass().getSimpleName());
		httpFilterEntity.getFilter().stop();
	}
	//外部调用-新增HostResolver
	public void addHostResolver() {
		logger.info("add hostResolver:{}",HttpFilterHostResolver.class.getSimpleName());
		HttpFilterHostResolver.INSTANCE.start();
	}
}