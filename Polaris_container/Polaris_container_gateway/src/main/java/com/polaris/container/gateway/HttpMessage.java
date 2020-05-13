package com.polaris.container.gateway;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.pojo.KeyValuePair;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 中途被拦截需要返回的信息
 *
 */
public class HttpMessage {

    /**
     * 是否走hostResolver
     *
     * false:直接ctx.writeAndFlush退出， true : 默认操作
     */
	private boolean runHostResolver = true;
	
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
	
	private Map<String, Object> header = new HashMap<>();

	public boolean isRunHostResolver() {
		return runHostResolver;
	}

	public void setRunHostResolver(boolean runHostResolver) {
		this.runHostResolver = runHostResolver;
	}

	public Map<String, Object> getHeader() {
		return header;
	}
	public void putHeader(String key, Object value) {
		this.header.put(key, value);
	}
	public void putHeader(KeyValuePair pair) {
		putHeader(pair.getKey(),pair.getValue());
	}
	
	public static HttpMessage of(String result,KeyValuePair... kvPairs) {
		return of(result,null,kvPairs);
	}
	public static HttpMessage of(String result,HttpResponseStatus status,KeyValuePair... kvPairs) {
		HttpMessage message = new HttpMessage();
		if (status != null) {
			message.setStatus(status);
		}
		if (kvPairs != null) {
			for (KeyValuePair pair : kvPairs) {
				message.putHeader(pair.getKey(),pair.getValue());
			}
		}
		message.setResult(result);
		return message;
	}
	
	
}
