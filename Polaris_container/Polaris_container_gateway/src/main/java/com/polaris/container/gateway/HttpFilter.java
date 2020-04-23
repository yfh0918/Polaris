package com.polaris.container.gateway;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class HttpFilter extends HttpFilterOrder implements HttpFilterInit {
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
