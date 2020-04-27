package com.polaris.container.gateway;

import java.util.HashMap;
import java.util.Map;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.container.gateway.pojo.HttpFilterEntity;

import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class HttpFilter implements HttpFilterLifeCycle ,HttpFilterCallback {
	protected HttpFilterEntity httpFilterEntity;
	
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
				HttpFilterReader.INSTANCE.readFile(this, file);
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
	public void setHttpFilterEntity(HttpFilterEntity httpFilterEntity) {
    	this.httpFilterEntity = httpFilterEntity;
    }
	
	
	/**
     * 中途被拦截需要返回的信息
     *
     */
	private String result;
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	private HttpResponseStatus status = HttpResponseStatus.FORBIDDEN;
	public HttpResponseStatus getStatus() {
		return status;
	}
	public void setStatus(HttpResponseStatus status) {
		this.status = status;
	}
	
	private Map<String, Object> headerMap = new HashMap<>();
	public Map<String, Object> getHeaderMap() {
		return headerMap;
	}
	public void putHeaderValue(String key, Object value) {
		this.headerMap.put(key, value);
	}
}
