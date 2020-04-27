package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.pojo.HttpFilterFile;

public abstract class HttpFilter extends HttpFilterMessage 
								 implements HttpFilterLifeCycle ,HttpFilterCallback {
	protected HttpFilterEntity httpFilterEntity;
	
    /**
     * 启动过滤器
     *
     */
	@Override
	public void start() {
		if (httpFilterEntity == null) {
			return;
		}
		HttpFilterFile[] files = httpFilterEntity.getFiles();
		if (files == null) {
			return;
		}
		for (HttpFilterFile file : files) {
			if (file.getData() == null) {
				HttpFilterFileReader.INSTANCE.readFile(this, file);
			}
		}
	} 
	
    /**
     * 获取entity对象
     *
     */
	public HttpFilterEntity getHttpFilterEntity() {
    	return httpFilterEntity;
    }
	
    /**
     * 设定entity对象
     *
     */
	public void setHttpFilterEntity(HttpFilterEntity httpFilterEntity) {
    	this.httpFilterEntity = httpFilterEntity;
    }
}
