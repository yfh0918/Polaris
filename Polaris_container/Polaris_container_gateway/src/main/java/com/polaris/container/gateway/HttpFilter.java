package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.pojo.HttpFilterMessage;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public abstract class HttpFilter implements HttpFilterLifeCycle ,HttpFileListener {
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
		HttpFile[] files = httpFilterEntity.getFiles();
		if (files == null) {
			return;
		}
		for (HttpFile file : files) {
			if (file.getData() == null) {
				HttpFileReader.INSTANCE.readFile(this, file);
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
	
	/**
     * httpRequest拦截逻辑
     *
     * @param originalRequest original request
     * @param httpObject      http请求
     * @param httpMessage     httpFilterMessage
     * @return true:正则匹配成功,false:正则匹配失败
     */
	public abstract boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, HttpFilterMessage httpFilterMessage);

}
