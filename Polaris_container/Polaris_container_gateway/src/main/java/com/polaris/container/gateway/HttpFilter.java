package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.FileType;
import com.polaris.container.gateway.pojo.HttpFilterEntity;

public abstract class HttpFilter extends HttpFilterResult implements HttpFilterLifeCycle  {
	protected HttpFilterEntity httpFilterEntity;
	
	@Override
	public void start(HttpFilterEntity httpFilterEntity) {
		this.httpFilterEntity = httpFilterEntity;
		if (httpFilterEntity == null) {
			return;
		}
		FileType[] fileTypes = httpFilterEntity.getFileTypes();
		if (fileTypes == null) {
			return;
		}
		for (FileType fileType : fileTypes) {
			HttpFilterHelper.create(fileType);
		}
	} 
	
    /**
     * 获取entity对象
     *
     */
	public HttpFilterEntity getHttpFilterEntity() {
    	return httpFilterEntity;
    }
	
}
