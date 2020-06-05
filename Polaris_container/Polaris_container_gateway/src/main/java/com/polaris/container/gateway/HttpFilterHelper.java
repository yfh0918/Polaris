package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterEntity;

public class HttpFilterHelper {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterHelper.class);
	public static HttpFilterHelper INSTANCE = new HttpFilterHelper();
	private HttpFilterHelper() {};
	
	//replace
	public void replaceFilter(HttpFilterEntity httpFilterEntity, HttpFilter newFilter) {
		removeFilter(httpFilterEntity);
		httpFilterEntity.setFilter(newFilter);
		addFilter(httpFilterEntity);
	}
	
	//add
	public void addFilter(HttpFilterEntity httpFilterEntity) {
		logger.info("add filter:{}",httpFilterEntity.getFilter().getClass().getSimpleName());
		try {
			httpFilterEntity.getFilter().start();
		} catch (Exception e) {
			logger.error("add filter:{} is error:{}",httpFilterEntity.getFilter().getClass().getSimpleName(),e);
		}
	}
	
	//remove
	public void removeFilter(HttpFilterEntity httpFilterEntity) {
		logger.info("remove filter:{}",httpFilterEntity.getFilter().getClass().getSimpleName());
		try {
			httpFilterEntity.getFilter().stop();
		} catch (Exception e) {
			logger.error("remove filter:{} is error:{}",httpFilterEntity.getFilter().getClass().getSimpleName(),e);
		}
	}

}
